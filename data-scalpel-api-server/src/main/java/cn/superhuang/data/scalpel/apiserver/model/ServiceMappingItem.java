package cn.superhuang.data.scalpel.apiserver.model;

import cn.superhuang.data.scalpel.apiserver.domain.Service;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServiceMappingItem {
    private RequestMappingInfo requestMappingInfo;
    private Service service;
}
