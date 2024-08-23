package cn.superhuang.data.scalpel.admin.app.datafile.service.parser;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.io.file.FileNameUtil;
import cn.superhuang.data.scalpel.admin.app.datafile.domain.DataFile;
import cn.superhuang.data.scalpel.admin.app.datafile.model.DataFileMetadata;
import cn.superhuang.data.scalpel.admin.app.datafile.model.enumeration.DataFileType;
import cn.superhuang.data.scalpel.admin.app.spark.service.SparkService;
import cn.superhuang.data.scalpel.admin.config.MinioConfig;
import cn.superhuang.data.scalpel.admin.util.SchemaUtil;
import cn.superhuang.data.scalpel.model.DataTable;
import cn.superhuang.data.scalpel.model.DataTableColumn;
import com.esri.gdb.FileGDB;
import com.esri.gdb.GDBTable;
import io.minio.MinioClient;
import jakarta.annotation.Resource;
import org.apache.hadoop.conf.Configuration;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.types.StructType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GdbMetadataParser implements DataFileMetadataParser {
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
        return type == DataFileType.GDB;
    }

    @Override
    public DataFileMetadata parse(DataFile dataFile) {
        String objectName = DatePattern.NORM_DATE_FORMAT.format(dataFile.getCreatedDate()) + "/" + dataFile.getId() + "." + FileNameUtil.extName(dataFile.getName());
        String path = "s3a://" + minioConfig.getBucketName() + "/" + objectName;

        String[] tableNames = FileGDB.listTableNames(path, sparkService.getSparkSession().sparkContext().hadoopConfiguration());


        Map<String, String> options = new HashMap<>();
        options.putAll(defaultOptions);
        options.putAll(dataFile.getProps());

        DataFileMetadata metadata = new DataFileMetadata();
        metadata.setDataTables(new ArrayList<>());
        for (String tableName : tableNames) {
            GDBTable gdbTable = GDBTable.apply(sparkService.getSparkSession().sparkContext().hadoopConfiguration(), path, tableName, null);
            StructType schema = gdbTable.schema();

            //TODO 补充空间信息
            List<DataTableColumn> columns = SchemaUtil.convertToDataTableColumn(schema);
            DataTable dataTable = new DataTable();
            dataTable.setName(tableName);
            dataTable.setCnName(tableName);
            dataTable.setColumns(columns);
            metadata.getDataTables().add(dataTable);
        }
        return metadata;
    }
}
