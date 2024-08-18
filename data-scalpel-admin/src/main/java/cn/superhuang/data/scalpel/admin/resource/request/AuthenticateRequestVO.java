package cn.superhuang.data.scalpel.admin.web.resource.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "鉴权")
@Data
public class AuthenticateRequestVO {
    @Schema(description = "用户名")
    private String username;
    @Schema(description = "密码")
    private String password;
}
