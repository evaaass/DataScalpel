package cn.superhuang.data.scalpel.admin.app.model.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.superhuang.data.scalpel.admin.BaseException;
import cn.superhuang.data.scalpel.admin.app.datasource.repository.DatasourceRepository;
import cn.superhuang.data.scalpel.admin.app.model.domain.Model;
import cn.superhuang.data.scalpel.admin.app.model.domain.ModelField;
import cn.superhuang.data.scalpel.admin.app.model.model.ModelUpdateDTO;
import cn.superhuang.data.scalpel.admin.model.enumeration.ModelState;
import cn.superhuang.data.scalpel.admin.app.model.repository.ModelFieldRepository;
import cn.superhuang.data.scalpel.admin.app.model.repository.ModelRepository;
import cn.superhuang.data.scalpel.model.datasource.config.DatasourceConfig;
import cn.superhuang.data.scalpel.model.datasource.config.JdbcConfig;
import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import org.apache.spark.sql.connector.catalog.TableChange;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class ModelService {

    @Resource
    private ModelRepository modelRepository;
    @Resource
    private ModelFieldRepository modelFieldRepository;
    @Resource
    private DatasourceRepository datasourceRepository;
    @Resource
    private JdbcTableService jdbcTableService;


    public Optional<Model> detail(String id) {
        return modelRepository.findById(id);
    }

    @Transactional
    public Model create(final Model model, List<ModelField> fields) {
        model.setState(ModelState.DRAFT);
        Boolean modelExist = modelRepository.existsModelByDatasourceIdAndName(model.getDatasourceId(), model.getName());
        if (modelExist) {
            throw new BaseException("存储下已存在模型：" + model.getName());
        }

        modelRepository.save(model);

        final String modelId = model.getId();
        fields.forEach(field -> {
            field.setModelId(modelId);
        });
        modelFieldRepository.saveAll(fields);
        return model;
    }

    @Transactional
    public void update(ModelUpdateDTO modelUpdate) {
        //这里是更新模型
        modelRepository.findById(modelUpdate.getId()).ifPresentOrElse(model -> {
            if (model.getState() == ModelState.ONLINE) {
                throw new RuntimeException("上线状态无法修改");
            }
            BeanUtil.copyProperties(modelUpdate, model, CopyOptions.create().ignoreNullValue());
            modelRepository.save(model);
        }, () -> {
            throw new BaseException("模型%s不存在".formatted(modelUpdate.getId()));
        });


    }

    @Transactional
    public void updateModelFields(String modelId, List<ModelField> newFields) {
        //这里是更新模型
        modelRepository.findById(modelId).ifPresentOrElse(model -> {
            if (model.getState() == ModelState.ONLINE) {
                throw new RuntimeException("上线状态无法修改");
            }
            if (model.getState() == ModelState.DRAFT) {
                modelFieldRepository.saveAll(newFields);
            } else if (model.getState() == ModelState.OFFLINE) {
                List<ModelField> oldFields = modelFieldRepository.findAllByModelId(modelId);
                List<TableChange> tableChanges = jdbcTableService.getTableChanges(oldFields, newFields);
                modelFieldRepository.saveAll(newFields);

                datasourceRepository.findById(model.getDatasourceId()).ifPresentOrElse(datasource -> {
                    try {
                        JdbcConfig jdbcConfig = (JdbcConfig) DatasourceConfig.getConfig(datasource.getType(), datasource.getProps());
                        jdbcTableService.updateTable(jdbcConfig, model.getName(), tableChanges);
                    } catch (Exception e) {
                        throw new BaseException("创建JDBC表失败：" + e.getMessage(), e);
                    }
                }, () -> {
                    throw new BaseException("数据源%s不存在".formatted(model.getDatasourceId()));
                });

            }
        }, () -> {
            throw new BaseException("模型%s不存在".formatted(modelId));
        });


    }

    @Transactional
    public void delete(String id) {
        modelRepository.findById(id).ifPresent(model -> {
            if (model.getState() == ModelState.ONLINE) {
                throw new RuntimeException("上线状态无法删除");
            }
            modelFieldRepository.deleteAllByModelId(id);
            modelRepository.deleteById(id);
            datasourceRepository.findById(model.getDatasourceId()).ifPresentOrElse(datasource -> {
                try {
                    JdbcConfig jdbcConfig = (JdbcConfig) DatasourceConfig.getConfig(datasource.getType(), datasource.getProps());
                    jdbcTableService.dropTable(jdbcConfig, model.getName());
                } catch (Exception e) {
                    throw new BaseException("创建JDBC表失败：" + e.getMessage(), e);
                }
            }, () -> {
                throw new BaseException("数据源%s不存在".formatted(model.getDatasourceId()));
            });
        });


    }

    public void recreateTable(String id) {
        modelRepository.findById(id).ifPresent(model -> {
            List<ModelField> fields = modelFieldRepository.findAllByModelId(model.getId());
            datasourceRepository.findById(model.getDatasourceId()).ifPresentOrElse(datasource -> {
                try {
                    JdbcConfig jdbcConfig = (JdbcConfig) DatasourceConfig.getConfig(datasource.getType(), datasource.getProps());
                    jdbcTableService.dropTable(jdbcConfig, model.getName());
                    jdbcTableService.createTable(jdbcConfig, model.getName(), fields);
                } catch (Exception e) {
                    throw new BaseException("创建JDBC表失败：" + e.getMessage(), e);
                }
            }, () -> {
                throw new BaseException("数据源%s不存在".formatted(model.getDatasourceId()));
            });
        });
    }

    public void createTable(String id) {
        modelRepository.findById(id).ifPresent(model -> {
            if (model.getState().equals(ModelState.DRAFT)) {
                throw new RuntimeException("模型不是草稿状态");
            }
            List<ModelField> fields = modelFieldRepository.findAllByModelId(model.getId());
            createPhysicalTable(model, fields);
        });
    }

    private void createPhysicalTable(Model model, List<ModelField> fields) {
        datasourceRepository.findById(model.getDatasourceId()).ifPresentOrElse(datasource -> {
            try {
                JdbcConfig jdbcConfig = (JdbcConfig) DatasourceConfig.getConfig(datasource.getType(), datasource.getProps());
                jdbcTableService.createTable(jdbcConfig, model.getName(), fields);
                model.setState(ModelState.OFFLINE);
                modelRepository.save(model);
            } catch (Exception e) {
                throw new BaseException("创建JDBC表失败：" + e.getMessage(), e);
            }
        }, () -> {
            throw new BaseException("数据源%s不存在".formatted(model.getDatasourceId()));
        });
    }


    public void online(String id) {
        modelRepository.findById(id).ifPresent(model -> {
            if (model.getState().equals(ModelState.OFFLINE)) {
                throw new RuntimeException("模型不是下线状态");
            }
            model.setState(ModelState.ONLINE);
            modelRepository.save(model);
        });
    }

    public void offline(String id) {
        modelRepository.findById(id).ifPresent(model -> {
            if (model.getState().equals(ModelState.ONLINE)) {
                throw new RuntimeException("模型不是下线状态");
            }
            model.setState(ModelState.OFFLINE);
            modelRepository.save(model);
        });
    }

    public List<ModelField> getModelFields(String id) {
        return modelFieldRepository.findAllByModelId(id);
    }
}
