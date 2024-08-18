package cn.superhuang.data.scalpel.admin.app.datasource.dto;

import lombok.Data;

@Data
public class DsItem {
    private DsItemType type;
    private String name;
    private String description;

}
