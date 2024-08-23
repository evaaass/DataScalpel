package cn.superhuang.data.scalpel.admin.app.sys.web.resource.request;

import lombok.Data;

@Data
public class UserCreateRequest {

    private String name;

    private String nickName;

    private String password;

    private String email;

    private String phone;

    private String roleId;
}
