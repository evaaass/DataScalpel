package cn.superhuang.data.scalpel.admin.app.service.web.request;

import cn.superhuang.data.scalpel.converter.MapConverter;
import cn.superhuang.data.scalpel.model.service.enumeration.ServiceEngineType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import lombok.Data;

import java.util.Map;

@Data
public class ServiceEngineTestRequest {

    @Schema(description = "引擎名称")
    private String name;

    @Schema(description = "服务引擎配置类型")
    private ServiceEngineType type;

    @Convert(converter = MapConverter.class)
    @Column(length = 1000)
    private Map<String, String> props;
}
