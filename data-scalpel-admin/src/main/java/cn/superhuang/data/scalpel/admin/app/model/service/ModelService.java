package cn.superhuang.data.scalpel.admin.app.model.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.superhuang.data.scalpel.exception.BaseException;
import cn.superhuang.data.scalpel.admin.app.datasource.repository.DatasourceRepository;
import cn.superhuang.data.scalpel.admin.app.model.domain.Model;
import cn.superhuang.data.scalpel.admin.app.model.domain.ModelField;
import cn.superhuang.data.scalpel.admin.app.model.model.ModelUpdateDTO;
import cn.superhuang.data.scalpel.admin.app.model.model.enumeration.ModelState;
import cn.superhuang.data.scalpel.admin.app.model.repository.ModelFieldRepository;
import cn.superhuang.data.scalpel.admin.app.model.repository.ModelRepository;
import cn.superhuang.data.scalpel.model.datasource.config.DatasourceConfig;
import cn.superhuang.data.scalpel.model.datasource.config.JdbcConfig;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
    private ModelDdlService modelDdlService;
    @Resource
    private EntityManager em;


    public Optional<Model> detail(String id) {
        return modelRepository.findById(id);
    }

    @Transactional
    public Model create(final Model model, List<ModelField> fields) {
        model.setRecordCount(0L);
        model.setState(ModelState.DRAFT);
        Boolean modelExist = modelRepository.existsModelByDatasourceIdAndName(model.getDatasourceId(), model.getName());
        if (modelExist) {
            throw new BaseException("存储下已存在模型：" + model.getName());
        }

        modelRepository.save(model);

        if(fields != null && !fields.isEmpty()) {
            final String modelId = model.getId();
            fields.forEach(field -> {
                field.setModelId(modelId);
            });
            modelFieldRepository.saveAll(fields);
        }
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
            List<ModelField> oldFields = modelFieldRepository.findAllByModelId(modelId);
            oldFields.forEach(f -> em.detach(f));
            if (model.getState() == ModelState.DRAFT) {
                mergeFields(modelId, oldFields, newFields);
            } else if (model.getState() == ModelState.OFFLINE) {
                mergeFields(modelId, oldFields, newFields);
                datasourceRepository.findById(model.getDatasourceId()).ifPresentOrElse(datasource -> {
                    try {
                        JdbcConfig jdbcConfig = (JdbcConfig) DatasourceConfig.getConfig(datasource.getType(), datasource.getProps());
                        modelDdlService.updateTable(jdbcConfig, model, oldFields, newFields);
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

    private void mergeFields(String modelId, List<ModelField> oldFields, List<ModelField> newFields) {
        Set<String> newFieldIdSet = newFields.stream().map(ModelField::getId).collect(Collectors.toSet());

        //删除要删除的字段
        oldFields.stream().filter(oldField -> !newFieldIdSet.contains(oldField.getId())).forEach(f -> modelFieldRepository.deleteById(f.getId()));
        for (ModelField newField : newFields) {
            if (newField.isNew()) {
                //没有ID肯定是新增字段
                newField.setModelId(modelId);
                modelFieldRepository.save(newField);
            } else {
                //有ID，就直接更新吧
                modelFieldRepository.findById(newField.getId()).ifPresent(
                        po -> {
                            BeanUtil.copyProperties(newField, po);
                            modelFieldRepository.save(po);
                        }
                );
            }
        }
        newFields.forEach(f -> f.setModelId(modelId));
        modelFieldRepository.saveAll(newFields);
    }

    @Transactional
    public void delete(String id) {
        modelRepository.findById(id).ifPresent(model -> {
            if (model.getState() == ModelState.ONLINE) {
                throw new RuntimeException("上线状态无法删除");
            }
            modelFieldRepository.deleteAllByModelId(id);
            modelRepository.deleteById(id);
            if (model.getState() != ModelState.DRAFT) {
                datasourceRepository.findById(model.getDatasourceId()).ifPresentOrElse(datasource -> {
                    try {
                        JdbcConfig jdbcConfig = (JdbcConfig) DatasourceConfig.getConfig(datasource.getType(), datasource.getProps());
                        modelDdlService.dropTable(jdbcConfig, model);
                    } catch (Exception e) {
                        throw new BaseException("创建JDBC表失败：" + e.getMessage(), e);
                    }
                }, () -> {
                    throw new BaseException("数据源%s不存在".formatted(model.getDatasourceId()));
                });
            }
        });
    }

    public void recreateTable(String id) {
        modelRepository.findById(id).ifPresent(model -> {
            List<ModelField> fields = modelFieldRepository.findAllByModelId(model.getId());
            datasourceRepository.findById(model.getDatasourceId()).ifPresentOrElse(datasource -> {
                try {
                    JdbcConfig jdbcConfig = (JdbcConfig) DatasourceConfig.getConfig(datasource.getType(), datasource.getProps());
                    modelDdlService.dropTable(jdbcConfig, model);
                    modelDdlService.createTable(jdbcConfig, model, fields);
                } catch (Exception e) {
                    throw new BaseException("创建JDBC表失败：" + e.getMessage(), e);
                }
            }, () -> {
                throw new BaseException("数据源%s不存在".formatted(model.getDatasourceId()));
            });
        });
    }

    private void createPhysicalTable(Model model, List<ModelField> fields) {
        datasourceRepository.findById(model.getDatasourceId()).ifPresentOrElse(datasource -> {
            try {
                JdbcConfig jdbcConfig = (JdbcConfig) DatasourceConfig.getConfig(datasource.getType(), datasource.getProps());
                modelDdlService.createTable(jdbcConfig, model, fields);
            } catch (Exception e) {
                throw new BaseException("创建JDBC表失败：" + e.getMessage(), e);
            }
        }, () -> {
            throw new BaseException("数据源%s不存在".formatted(model.getDatasourceId()));
        });
    }


    public void online(String id) {
        modelRepository.findById(id).ifPresent(model -> {
            if (model.getState() == ModelState.DRAFT) {
                model.setState(ModelState.ONLINE);
                modelRepository.save(model);
                List<ModelField> fields = modelFieldRepository.findAllByModelId(model.getId());
                createPhysicalTable(model, fields);
            } else if (model.getState() == ModelState.OFFLINE) {
                model.setState(ModelState.ONLINE);
                modelRepository.save(model);
            } else {
                throw new RuntimeException("该状态%s不允许上线".formatted(model.getState()));
            }
        });
    }

    public void offline(String id) {
        modelRepository.findById(id).ifPresent(model -> {
            if (!model.getState().equals(ModelState.ONLINE)) {
                throw new RuntimeException("模型不是上线状态");
            }
            model.setState(ModelState.OFFLINE);
            modelRepository.save(model);
        });
    }


}
