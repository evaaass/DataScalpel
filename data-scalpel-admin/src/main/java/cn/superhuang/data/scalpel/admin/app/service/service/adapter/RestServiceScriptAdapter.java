package cn.superhuang.data.scalpel.admin.app.service.service.adapter;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.superhuang.data.scalpel.admin.app.datasource.repository.DatasourceRepository;
import cn.superhuang.data.scalpel.admin.app.service.domain.RestService;
import cn.superhuang.data.scalpel.admin.app.service.model.RestServiceTestResult;
import cn.superhuang.data.scalpel.admin.app.service.model.definition.ScriptServiceDefinition;
import cn.superhuang.data.scalpel.admin.app.service.model.enumeration.RestServiceType;
import cn.superhuang.data.scalpel.admin.app.service.repository.RestServiceRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.ssssssss.magicapi.core.config.MagicConfiguration;
import org.ssssssss.magicapi.core.model.ApiInfo;
import org.ssssssss.magicapi.utils.ScriptManager;
import org.ssssssss.script.MagicScriptContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.ssssssss.magicapi.core.config.Constants.VAR_NAME_HEADER;

@Service
public class RestServiceScriptAdapter implements RestServiceAdapter, InitializingBean {
    @Resource
    private DatasourceRepository datasourceRepository;
    @Resource
    private RestServiceRepository restServiceRepository;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private MagicConfiguration configuration;

    @Override
    public Boolean support(RestServiceType type) {
        return type == RestServiceType.SCRIPT;
    }

    @Override
    public RestServiceTestResult test(RestService service, HttpServletRequest request, HttpServletResponse response) {
        RestServiceTestResult serviceTestResult = new RestServiceTestResult();
        try {
            ScriptServiceDefinition serviceDefinition = objectMapper.readValue(service.getServiceDefinition(), ScriptServiceDefinition.class);

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
            Object result = ScriptManager.executeScript(serviceDefinition.getScript(), context);
            serviceTestResult.setResponseBody(objectMapper.writeValueAsString(result));
        } catch (Exception e) {
            e.printStackTrace();
            serviceTestResult.setSuccess(false);
            serviceTestResult.setMsg(e.getMessage());
            serviceTestResult.setErrorDetail(ExceptionUtil.stacktraceToString(e));
        }
        return serviceTestResult;
    }

    @Override
    public void up(RestService service) throws JsonProcessingException {
        ScriptServiceDefinition serviceDefinition = objectMapper.readValue(service.getServiceDefinition(), ScriptServiceDefinition.class);

        ApiInfo apiInfo = new ApiInfo();
        apiInfo.setMethod(service.getMethod().name());
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
    public void down(RestService service) {
        MagicConfiguration.getMagicResourceService().delete(service.getId().toString());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        List<RestService> services = restServiceRepository.findAllByType(RestServiceType.SCRIPT);
        for (RestService service : services) {
            System.out.println(objectMapper.writeValueAsString(service));
        }
    }
}