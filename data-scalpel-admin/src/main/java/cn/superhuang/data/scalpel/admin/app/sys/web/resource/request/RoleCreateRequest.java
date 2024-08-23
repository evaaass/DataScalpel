package cn.superhuang.data.scalpel.admin.app.sys.web.resource.request;

import lombok.Data;

import java.util.List;

@Data
public class RoleCreateRequest {
    private String name;

    private List<String> permissions;
}
