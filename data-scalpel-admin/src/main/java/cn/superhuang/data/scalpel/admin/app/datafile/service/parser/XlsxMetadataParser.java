package cn.superhuang.data.scalpel.admin.app.datafile.service.parser;

import cn.superhuang.data.scalpel.admin.app.datafile.domain.DataFile;
import cn.superhuang.data.scalpel.admin.app.datafile.model.DataFileMetadata;
import cn.superhuang.data.scalpel.admin.app.datafile.model.enumeration.DataFileType;

public class XlsxMetadataParser extends XlsMetadataParser implements DataFileMetadataParser {
    @Override
    public Boolean support(DataFileType type) {
        return type == DataFileType.XLSX;
    }
}
