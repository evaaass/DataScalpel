package cn.superhuang.data.scalpel.admin.app.service.model;

import cn.superhuang.data.scalpel.admin.app.service.domain.RestService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RestServiceMappingItem {
    private RequestMappingInfo requestMappingInfo;
    private RestService service;
}
