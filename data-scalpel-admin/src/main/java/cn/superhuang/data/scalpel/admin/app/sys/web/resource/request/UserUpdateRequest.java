package cn.superhuang.data.scalpel.admin.app.sys.web.resource.request;

import lombok.Data;

@Data
public class UserUpdateRequest {

    private String nickName;

    private String email;

    private String phone;

    private String roleId;
}
