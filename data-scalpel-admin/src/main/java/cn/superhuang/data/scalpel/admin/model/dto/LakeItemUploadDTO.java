package cn.superhuang.data.scalpel.admin.model.dto;

import cn.superhuang.data.scalpel.admin.app.item.model.enumeration.EntityType;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

/**
 * A LakeItem.
 */
@Data
@SuppressWarnings("common-java:DuplicatedBlocks")
public class LakeItemUploadDTO implements Serializable {

    private static final long serialVersionUID = 1L;


    private String catalogId;

    private EntityType entityType;

    private MultipartFile contentFile;

}
