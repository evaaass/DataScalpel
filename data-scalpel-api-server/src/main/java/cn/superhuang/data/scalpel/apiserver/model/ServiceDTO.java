package cn.superhuang.data.scalpel.apiserver.model;

import cn.superhuang.data.scalpel.apiserver.model.enums.ServiceType;
import lombok.Data;

@Data
public class ServiceDTO {
    private Long id;
    //@ApiModelProperty(value = "服务类型：标准服务：STD,向导式：SQL，交互式：SCRIPT")
    private ServiceType type;
    private String name;
    private String uri;
    private String method;
    private String description;
    private String serviceDefinition;

    private String requestBody;
    private String requestBodyDefinition;
    private String responseBodyDefinition;
    private String responseBody;

    private String datasourceId;
}
