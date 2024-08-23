package cn.superhuang.data.scalpel.admin.app.sys.web.resource.request;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
