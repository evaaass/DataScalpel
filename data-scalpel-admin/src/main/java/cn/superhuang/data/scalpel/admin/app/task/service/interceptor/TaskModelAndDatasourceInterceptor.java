package cn.superhuang.data.scalpel.admin.app.task.service.interceptor;

import cn.superhuang.data.scalpel.actuator.canvas.Canvas;
import cn.superhuang.data.scalpel.actuator.util.CanvasUtil;
import cn.superhuang.data.scalpel.admin.app.datasource.domain.Datasource;
import cn.superhuang.data.scalpel.admin.app.datasource.repository.DatasourceRepository;
import cn.superhuang.data.scalpel.admin.app.model.domain.Model;
import cn.superhuang.data.scalpel.admin.app.model.repository.ModelRepository;
import cn.superhuang.data.scalpel.app.model.model.ModelDTO;
import cn.superhuang.data.scalpel.model.datasource.config.DatasourceConfig;
import cn.superhuang.data.scalpel.model.enumeration.TaskType;
import cn.superhuang.data.scalpel.model.task.configuration.CanvasTaskConfiguration;
import cn.superhuang.data.scalpel.model.task.configuration.TaskConfiguration;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TaskModelAndDatasourceInterceptor implements TaskSubmitInterceptor {
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private ModelRepository modelRepository;
    @Resource
    private DatasourceRepository datasourceRepository;

    @Override
    public void beforeSubmit(TaskConfiguration taskConfiguration) throws JsonProcessingException {
        Set<String> modelIds = new HashSet<>(getModelIdsFromTask(taskConfiguration));
        List<Model> models = modelRepository.findAllByIdIn(modelIds);
        Set<String> datasourceIds = models.stream().map(Model::getDatasourceId).collect(Collectors.toSet());
        datasourceIds.addAll(getDatasourceIdsFromTask(taskConfiguration));

        List<Datasource> datasourceList = datasourceRepository.findAllByIdIn(new HashSet<>(datasourceIds));

        Map<String, ModelDTO> modelMap = new HashMap<>();
        for (Model model : models) {
            ModelDTO modelDTO = new ModelDTO();
            modelDTO.setId(model.getId());
            modelDTO.setDatasourceId(model.getDatasourceId());
            modelDTO.setName(model.getName());
            modelDTO.setAlias(model.getAlias());
            modelMap.put(model.getId(), modelDTO);
        }

        Map<String, DatasourceConfig> datasourceMap = new HashMap<>();
        for (Datasource datasource : datasourceList) {
            datasourceMap.put(datasource.getId().toString(), DatasourceConfig.getConfig(datasource.getType(), datasource.getProps()));
        }
        taskConfiguration.setModelMap(modelMap);
        taskConfiguration.setDatasourceMap(datasourceMap);
    }

    public Set<String> getModelIdsFromTask(TaskConfiguration taskConfiguration) throws JsonProcessingException {
        Set<String> modelIds = new HashSet<>();
        if (taskConfiguration.getType() == TaskType.BATCH_CANVAS) {
            CanvasTaskConfiguration canvasTaskConfiguration = (CanvasTaskConfiguration) taskConfiguration;
            Canvas canvas = objectMapper.readValue(canvasTaskConfiguration.getCanvas(), Canvas.class);
            modelIds.addAll(CanvasUtil.getModelIds(canvas));
        }
        return modelIds;
    }

    public Set<String> getDatasourceIdsFromTask(TaskConfiguration taskConfiguration) throws JsonProcessingException {
        Set<String> datasourceIds = new HashSet<>();
        if (taskConfiguration.getType() == TaskType.BATCH_CANVAS) {
            CanvasTaskConfiguration canvasTaskConfiguration = (CanvasTaskConfiguration) taskConfiguration;
            Canvas canvas = objectMapper.readValue(canvasTaskConfiguration.getCanvas(), Canvas.class);
            datasourceIds.addAll(CanvasUtil.getDatasourceIds(canvas));
        }
        return datasourceIds;
    }


}
