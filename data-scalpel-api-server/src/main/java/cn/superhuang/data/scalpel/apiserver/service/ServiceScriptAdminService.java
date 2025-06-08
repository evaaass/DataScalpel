package cn.superhuang.data.scalpel.apiserver.service;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.json.JSONUtil;
import cn.superhuang.data.scalpel.apiserver.domain.Service;
import cn.superhuang.data.scalpel.apiserver.domain.repository.ServiceRepository;
import cn.superhuang.data.scalpel.apiserver.model.ServiceDTO;
import cn.superhuang.data.scalpel.apiserver.model.enums.ServiceType;
import cn.superhuang.data.scalpel.apiserver.util.DefinitionUtil;
import cn.superhuang.data.scalpel.model.service.ServiceTestResult;
import cn.superhuang.data.scalpel.model.service.definition.ScriptServiceDefinition;
import cn.superhuang.data.scalpel.util.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.ssssssss.magicapi.core.config.MagicConfiguration;
import org.ssssssss.magicapi.core.context.RequestContext;
import org.ssssssss.magicapi.core.context.RequestEntity;
import org.ssssssss.magicapi.core.model.ApiInfo;
import org.ssssssss.magicapi.core.model.BaseDefinition;
import org.ssssssss.magicapi.core.model.DataType;

import org.ssssssss.magicapi.servlet.jakarta.MagicJakartaHttpServletResponse;

import org.ssssssss.magicapi.utils.ScriptManager;
import org.ssssssss.script.MagicScriptContext;

import java.util.*;
import java.util.stream.Collectors;

import static org.ssssssss.magicapi.core.config.Constants.VAR_NAME_HEADER;
import static org.ssssssss.magicapi.core.config.Constants.VAR_NAME_REQUEST_BODY;

@Slf4j
@org.springframework.stereotype.Service
public class ServiceScriptAdminService implements ServiceAdmin, InitializingBean {
    @Resource
    private DatasourceService dsService;

    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private MagicConfiguration configuration;
    @Resource
    private ServiceRepository serviceRepository;

    @Override
    public Boolean support(ServiceType type) {
        return type == ServiceType.SCRIPT;
    }

    @Override
    public ServiceTestResult test(Service service, HttpServletRequest request, HttpServletResponse response) {
        ServiceTestResult serviceTestResult = new ServiceTestResult();
        try {
            ScriptServiceDefinition serviceDefinition = objectMapper.readValue(service.getServiceDefinition(), ScriptServiceDefinition.class);
            RequestEntity requestEntity = RequestEntity.create()
                    .response(new MagicJakartaHttpServletResponse(response));
            RequestContext.setRequestEntity(requestEntity);

            //TODO 从service里面拿出参数放进去
            Map<String, String> headersMap = new HashMap<>();
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                String headerValue = request.getHeader(headerName);
                headersMap.put(headerName, headerValue);
            }
            MagicScriptContext context = new MagicScriptContext();
            context.setScriptName(service.getName());
            context.set(VAR_NAME_HEADER, headersMap);
            if (service.getMethod().equals("POST")) {
                context.set(VAR_NAME_REQUEST_BODY, getRequestBody(service.getRequestBodyDefinition()));
            } else {
                context.putMapIntoContext(getRequestBody(service.getRequestBodyDefinition()));
            }

//            DmpDatasourceDTO dmpDatasource = dsService.getDmpDatasource(service.getDatasourceId());
//            String alias = dmpDatasource.getAlias();
//            context.set(Options.DEFAULT_DATA_SOURCE.getValue(), alias);
            requestEntity.setMagicScriptContext(context);
            Object result = ScriptManager.executeScript(serviceDefinition.getScript(), context);
            serviceTestResult.setSuccess(true);
            serviceTestResult.setRequestBody(objectMapper.writeValueAsString(getRequestBody(service.getRequestBodyDefinition())));
            //TODO 返回结果格式
            serviceTestResult.setResponseBody(objectMapper.writeValueAsString(result));
            serviceTestResult.setRequestBodyDefinition(service.getRequestBodyDefinition());
            serviceTestResult.setResponseBodyDefinition(objectMapper.writeValueAsString(getResponseDefinition(result)));
        } catch (Exception e) {
            e.printStackTrace();
            serviceTestResult.setSuccess(false);
            serviceTestResult.setMsg(e.getMessage());
            serviceTestResult.setErrorDetail(ExceptionUtil.stacktraceToString(e));
        }
        return serviceTestResult;
    }

    @Override
    public void up(Service service) throws JsonProcessingException {
        ScriptServiceDefinition serviceDefinition = objectMapper.readValue(service.getServiceDefinition(), ScriptServiceDefinition.class);

        ApiInfo apiInfo = new ApiInfo();
        apiInfo.setMethod(service.getMethod());
        apiInfo.setResponseBody(service.getResponseBody());
        //TODO 增加RequestBody
        apiInfo.setRequestBody("{}");
        apiInfo.setDescription(service.getDescription());
//        apiInfo.setParameters();
        apiInfo.setRequestBodyDefinition(null);
        apiInfo.setResponseBodyDefinition(null);
        apiInfo.setPath(service.getUri());
        apiInfo.setId(service.getId().toString());
        apiInfo.setScript(serviceDefinition.getScript());
        apiInfo.setGroupId("DMP_GROUP");
        apiInfo.setName(service.getName());
        MagicConfiguration.getMagicResourceService().saveFile(apiInfo);
    }

    @Override
    public void down(Service service) {
        MagicConfiguration.getMagicResourceService().delete(service.getId());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        List<Service> serviceList = serviceRepository.findAllByType(ServiceType.SCRIPT);
//        List<DmpServiceDTO> serviceList = dbService.getServices().stream().filter(s -> s.getType() == ServiceType.SCRIPT).collect(Collectors.toList());
        for (Service service : serviceList) {
            System.out.println(objectMapper.writeValueAsString(service));
        }
    }

    private Map<String, Object> getRequestBody(String bodyDefinitionContent) throws JsonProcessingException {
        List<BaseDefinition> parameterDefinitions = objectMapper.readValue(bodyDefinitionContent, new TypeReference<List<BaseDefinition>>() {
        });
        return parameterDefinitions.stream().collect(Collectors.toMap(BaseDefinition::getName, BaseDefinition::getValue));
    }

    private List<BaseDefinition> getResponseDefinition(Object result) throws JsonProcessingException {
        List<BaseDefinition> definitions = new ArrayList<>();
        definitions.add(DefinitionUtil.buildDefinition("code", null, DataType.Integer, "0为正常", false));
        definitions.add(DefinitionUtil.buildDefinition("message", null, DataType.String, "", false));
        BaseDefinition resultDef = DefinitionUtil.buildDefinition("data", null, DataType.Object, "返回数据", false);
        resultDef.setChildren(new ArrayList<>());
        definitions.add(resultDef);
        try {
            JsonNode jsonNode = JsonUtil.objectMapper.readTree(JSONUtil.toJsonStr(result));
            if (jsonNode.isArray()) {
                jsonNode = jsonNode.get(0);
            }
            if (jsonNode.isObject()) {
                Iterator<String> iterator = jsonNode.fieldNames();
                while (iterator.hasNext()) {
                    String fieldName = iterator.next();
                    JsonNode node = jsonNode.get(fieldName);
                    DataType dataType = null;
                    if (node.getNodeType() == JsonNodeType.ARRAY) {
                        dataType = DataType.Array;
                    } else if (node.getNodeType() == JsonNodeType.BOOLEAN) {
                        dataType = DataType.Boolean;
                    } else if (node.getNodeType() == JsonNodeType.NUMBER) {
                        dataType = DataType.Long;
                    } else if (node.getNodeType() == JsonNodeType.STRING) {
                        dataType = DataType.String;
                    } else {
                        dataType = DataType.Object;
                    }
                    resultDef.getChildren().add(DefinitionUtil.buildDefinition(fieldName, node.asText(), dataType, "", false));
                }
            }
        } catch (Exception e) {
            log.warn("解析返回结构失败：" + e.getMessage(), e);
        }
        return definitions;
    }
}