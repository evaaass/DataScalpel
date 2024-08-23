package cn.superhuang.data.scalpel.admin.app.sys.web.resource.request;

import lombok.Data;

import java.util.List;

@Data
public class RoleUpdateRequest {
    private String id;

    private String name;

    private List<String> permissions;
}
