package cn.superhuang.data.scalpel.admin.app.datafile.model;

import cn.superhuang.data.scalpel.admin.app.datafile.model.enumeration.DataFileType;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Data
public class DataFileUpdateDTO {
    private String id;

    private String catalogId;

    private String alias;

    private String description;

    private DataFileType type;

    private Map<String, String> props;

    private MultipartFile file;
}
