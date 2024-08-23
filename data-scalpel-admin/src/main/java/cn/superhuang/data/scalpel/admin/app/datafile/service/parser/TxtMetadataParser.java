package cn.superhuang.data.scalpel.admin.app.datafile.service.parser;

import cn.superhuang.data.scalpel.admin.app.datafile.domain.DataFile;
import cn.superhuang.data.scalpel.admin.app.datafile.model.DataFileMetadata;
import cn.superhuang.data.scalpel.admin.app.datafile.model.enumeration.DataFileType;
import org.springframework.stereotype.Service;

@Service
public class TxtMetadataParser extends CsvMetadataParser implements DataFileMetadataParser {
    @Override
    public Boolean support(DataFileType type) {
        return type == DataFileType.TXT;
    }
}
