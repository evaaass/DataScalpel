package cn.superhuang.data.scalpel.apiserver.resource;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.ssssssss.magicapi.core.servlet.MagicHttpServletRequest;
import org.ssssssss.magicapi.core.servlet.MagicHttpServletResponse;

import java.util.Map;

public abstract class BaseServiceRequestHandler {
    public static final Integer DEFAULT_PAGE_SIZE = 10;
    public static final Integer DEFAULT_MAX_PAGE_SIZE = 1000;
    public static final String CONFIG_KEY_MAX_PAGE_SIZE = "maxResponseSize";
    public static final String CONFIG_KEY_DEFAULT_PAGE_SIZE = "defaultResponseSize";

    public abstract Object invoke(MagicHttpServletRequest request, MagicHttpServletResponse response,
                                  @PathVariable(required = false) Map<String, Object> pathVariables,
                                  @RequestHeader(required = false) Map<String, Object> defaultHeaders,
                                  @RequestParam(required = false) Map<String, Object> parameters) throws Throwable;


    public Integer getPageSize(Integer requestPageSize, String config) {
        String json = StrUtil.isBlank(config) ? "{}" : config;
        Map<String, Object> configMap = JSONUtil.toBean(config, new TypeReference<Map<String, Object>>() {
        }, true);
        Integer defaultPageSize = DEFAULT_PAGE_SIZE;
        Integer maxPageSize = DEFAULT_MAX_PAGE_SIZE;
        if (config.contains(CONFIG_KEY_DEFAULT_PAGE_SIZE)) {
            defaultPageSize = Integer.parseInt(configMap.get(CONFIG_KEY_DEFAULT_PAGE_SIZE).toString());
        }
        if (configMap.containsKey(CONFIG_KEY_MAX_PAGE_SIZE)) {
            maxPageSize = Integer.parseInt(configMap.get(CONFIG_KEY_MAX_PAGE_SIZE).toString());
        }

        Integer pageSize = null;
        if (requestPageSize != null) {
            pageSize = requestPageSize;
        } else {
            pageSize = defaultPageSize;
        }
        if (pageSize > maxPageSize) {
            pageSize = maxPageSize;
        }
        return pageSize;
    }

}
