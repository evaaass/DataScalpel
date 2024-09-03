package cn.superhuang.data.scalpel.admin.model.dto;

import cn.superhuang.data.scalpel.admin.app.item.model.enumeration.EntityType;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serial;
import java.io.Serializable;

/**
 * A LakeItem.
 */
@Data
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LakeItemUploadDTO implements Serializable {


    @Serial
    private static final long serialVersionUID = -7407163530211350512L;
    private String catalogId;

    private EntityType entityType;

    private MultipartFile contentFile;

}
