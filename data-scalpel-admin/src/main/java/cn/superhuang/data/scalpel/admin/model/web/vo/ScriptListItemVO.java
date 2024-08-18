package cn.superhuang.data.scalpel.admin.model.web.vo;

import cn.superhuang.data.scalpel.admin.model.enumeration.ScriptType;
import lombok.Data;

import java.time.Instant;

@Data
public class ScriptListItemVO {
    private String id;

    private String catalogId;

    private String name;

    private ScriptType type;

    private Instant createTime;

    private Instant modifyTime;
}
