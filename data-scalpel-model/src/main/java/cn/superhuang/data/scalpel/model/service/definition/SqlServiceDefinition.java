package cn.superhuang.data.scalpel.model.service.definition;

import lombok.Data;

import java.util.List;

@Data
public class SqlServiceDefinition extends BaseServiceDefinition {
    private String sql;
}