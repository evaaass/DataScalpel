package cn.superhuang.data.scalpel.admin.app.service.model.definition;

import lombok.Data;

import java.util.List;

@Data
public class ScriptServiceDefinition extends BaseServiceDefinition {
    private List<String> modelIds;
    private String script;
}