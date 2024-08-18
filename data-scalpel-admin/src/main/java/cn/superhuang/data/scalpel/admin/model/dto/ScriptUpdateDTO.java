package cn.superhuang.data.scalpel.admin.model.dto;

import lombok.Data;

@Data
public class ScriptUpdateDTO {
    private String id;
    private String catalogId;
    private String name;
    private String content;
}
