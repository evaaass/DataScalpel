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
import com.esri.gdb.FileGDB;
import io.minio.MinioClient;
import jakarta.annotation.Resource;
import org.apache.spark.sql.types.StructType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
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
        String path = "s3a://" + minioConfig.getBucketName() + "/" + DatePattern.NORM_DATE_FORMAT.format(dataFile.getCreatedDate()) + "/" + dataFile.getId() + "." + dataFile.getType().getExtName();

        String[] tableNames = FileGDB.listTableNames(path, sparkService.getSparkSession().sparkContext().hadoopConfiguration());


        Map<String, String> options = new HashMap<>();
        options.putAll(defaultOptions);
        options.putAll(dataFile.getProps());

        DataFileMetadata metadata = new DataFileMetadata();
        metadata.setDataTables(new ArrayList<>());
        for (String tableName : tableNames) {
            if (tableName.startsWith("GDB_")) {
                continue;
            }
            //TODO 这里做个单元测试
            FileGDB fileGDB = FileGDB.apply(path, tableName, sparkService.getSparkSession().sparkContext().hadoopConfiguration()).get();
            StructType schema = fileGDB.schema();
            //TODO 补充空间信息
            List<DataTableColumn> columns = SchemaUtil.convertToDataTableColumn(schema);
            DataTable dataTable = new DataTable();
            dataTable.setName(tableName);
            dataTable.setAlias(tableName);
            dataTable.setColumns(columns);
            metadata.getDataTables().add(dataTable);
        }
        return metadata;
    }
}
