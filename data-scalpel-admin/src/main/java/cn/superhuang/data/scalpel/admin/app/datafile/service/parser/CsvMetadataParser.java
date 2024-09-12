package cn.superhuang.data.scalpel.admin.app.datafile.service.parser;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.io.file.FileNameUtil;
import cn.superhuang.data.scalpel.admin.app.datafile.domain.DataFile;
import cn.superhuang.data.scalpel.admin.app.datafile.model.DataFileMetadata;
import cn.superhuang.data.scalpel.admin.app.datafile.model.enumeration.DataFileType;
import cn.superhuang.data.scalpel.admin.app.task.service.SparkService;
import cn.superhuang.data.scalpel.admin.config.MinioConfig;
import cn.superhuang.data.scalpel.admin.util.SchemaUtil;
import cn.superhuang.data.scalpel.model.DataTable;
import cn.superhuang.data.scalpel.model.DataTableColumn;
import io.minio.MinioClient;
import jakarta.annotation.Resource;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CsvMetadataParser implements DataFileMetadataParser {
    @Resource
    private MinioClient minioClient;
    @Resource
    private MinioConfig minioConfig;
    @Resource
    private SparkService sparkService;

    public static final Map<String, String> defaultOptions = new HashMap<>();

    static {
        defaultOptions.put("header", "true");
    }

    @Override
    public Boolean support(DataFileType type) {
        return type == DataFileType.CSV;
    }

    @Override
    public DataFileMetadata parse(DataFile dataFile) {
        String objectName = DatePattern.NORM_DATE_FORMAT.format(dataFile.getCreatedDate()) + "/" + dataFile.getId() + "." + FileNameUtil.extName(dataFile.getName());

        Map<String, String> options = new HashMap<>();
        options.putAll(defaultOptions);
        options.putAll(dataFile.getProps());

        Dataset<Row> ds = sparkService.getSparkSession().read()
                .options(options)
                .csv("s3a://" + minioConfig.getBucketName() + "/" + objectName);
        List<DataTableColumn> columns = SchemaUtil.convertToDataTableColumn(ds.schema());
        DataTable dataTable = new DataTable();
        dataTable.setName(dataFile.getName().replaceAll("\\.", "_"));
        dataTable.setAlias(dataFile.getAlias());
        dataTable.setColumns(columns);

        DataFileMetadata metadata = new DataFileMetadata();
        metadata.setDataTables(new ArrayList<>());
        metadata.getDataTables().add(dataTable);
        return metadata;
    }
}
