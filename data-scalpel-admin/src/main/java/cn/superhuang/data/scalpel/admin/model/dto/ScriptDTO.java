package cn.superhuang.data.scalpel.admin.model.dto;

import cn.superhuang.data.scalpel.admin.model.enumeration.ScriptType;
import lombok.Data;

import java.time.Instant;

@Data
public class ScriptDTO {
    private String id;

    private String catalogId;

    private String name;

    private ScriptType type;

    private String content;

    private Instant createTime;

    private Instant modifyTime;
}
