package cn.superhuang.data.scalpel.admin.app.sys.web.resource.vo;

import cn.superhuang.data.scalpel.admin.app.sys.model.enumeration.UserState;
import lombok.Data;

import java.util.Date;

@Data
public class UserVO {

    private String id;

    private String name;

    private String nickName;

    private String email;

    private String phone;

    private UserState state;

    private String roleId;

    private String roleName;

    private Date createdDate;

    private Date lastModifiedDate;

}
