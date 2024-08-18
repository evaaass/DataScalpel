package cn.superhuang.data.scalpel.admin.app.service.web;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.ssssssss.magicapi.core.servlet.MagicHttpServletRequest;
import org.ssssssss.magicapi.core.servlet.MagicHttpServletResponse;

import java.util.Map;

public interface BaseServiceRequestHandler {
    public Object invoke(MagicHttpServletRequest request, MagicHttpServletResponse response,
                         @PathVariable(required = false) Map<String, Object> pathVariables,
                         @RequestHeader(required = false) Map<String, Object> defaultHeaders,
                         @RequestParam(required = false) Map<String, Object> parameters) throws Throwable;
}
