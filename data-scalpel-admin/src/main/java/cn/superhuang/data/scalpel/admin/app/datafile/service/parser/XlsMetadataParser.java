package cn.superhuang.data.scalpel.admin.app.datafile.service.parser;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.superhuang.data.scalpel.admin.app.datafile.domain.DataFile;
import cn.superhuang.data.scalpel.admin.app.datafile.model.DataFileMetadata;
import cn.superhuang.data.scalpel.admin.app.datafile.model.enumeration.DataFileType;
import cn.superhuang.data.scalpel.admin.app.spark.service.SparkService;
import cn.superhuang.data.scalpel.admin.config.MinioConfig;
import cn.superhuang.data.scalpel.admin.util.SchemaUtil;
import cn.superhuang.data.scalpel.model.DataTable;
import cn.superhuang.data.scalpel.model.DataTableColumn;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import jakarta.annotation.Resource;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class XlsMetadataParser implements DataFileMetadataParser {
    public static final Map<String, String> defaultOptions = new HashMap<>();

    static {
        defaultOptions.put("header", "true");
    }

    @Resource
    private MinioConfig minioConfig;
    @Resource
    private MinioClient minioClient;
    @Resource
    private SparkService sparkService;

    @Override
    public Boolean support(DataFileType type) {
        return type == DataFileType.XLS;
    }

    @Override
    public DataFileMetadata parse(DataFile dataFile) throws Exception {
        String objectName = DatePattern.NORM_DATE_FORMAT.format(dataFile.getCreatedDate()) + "/" + dataFile.getId() + "." + FileNameUtil.extName(dataFile.getName());
        Map<String, String> options = new HashMap<>();
        options.putAll(defaultOptions);
        options.putAll(dataFile.getProps());

        DataFileMetadata metadata = new DataFileMetadata();
        metadata.setDataTables(new ArrayList<>());

        GetObjectArgs getObjectArgs = GetObjectArgs.builder().bucket(minioConfig.getBucketName()).object(objectName).build();
        GetObjectResponse getObjectResponse = minioClient.getObject(getObjectArgs);
        ExcelReader reader = ExcelUtil.getReader(getObjectResponse);
        for (Sheet sheet : reader.getSheets()) {
            String sheetName = sheet.getSheetName();
            Dataset<Row> ds = sparkService.getSparkSession().read().format("com.crealytics.spark.excel")
                    .options(options).load("s3a://" + minioConfig.getBucketName() + "/" + objectName);

            List<DataTableColumn> columns = SchemaUtil.convertToDataTableColumn(ds.schema());
            DataTable dataTable = new DataTable();
            dataTable.setName(sheetName);
            dataTable.setCnName(sheetName);
            dataTable.setColumns(columns);
            metadata.getDataTables().add(dataTable);
        }
        return metadata;
    }
}
