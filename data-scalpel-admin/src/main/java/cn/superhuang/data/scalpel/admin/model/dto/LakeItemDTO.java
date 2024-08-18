package cn.superhuang.data.scalpel.admin.model.dto;

import cn.superhuang.data.scalpel.admin.app.item.model.enumeration.EntityType;
import lombok.Data;

import java.io.Serializable;
import java.time.Instant;

/**
 * A LakeItem.
 */
@Data
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LakeItemDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String catalogId;

    private String catalogUri;

    private String name;

    private EntityType entityType;

    private String metadataType;

    private String metadata;

    private Instant createTime;

    private Instant modifyTime;

}
