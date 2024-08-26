package cn.superhuang.data.scalpel.admin.app.datafile.service.parser;


import cn.superhuang.data.scalpel.admin.app.datafile.domain.DataFile;
import cn.superhuang.data.scalpel.admin.app.datafile.model.DataFileMetadata;
import cn.superhuang.data.scalpel.admin.app.datafile.model.enumeration.DataFileType;

public interface DataFileMetadataParser {

    public Boolean support(DataFileType type);

    public DataFileMetadata parse(DataFile dataFile) throws Exception;
}