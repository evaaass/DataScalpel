package cn.superhuang.data.scalpel.admin.app.datafile.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.io.file.FileNameUtil;
import cn.superhuang.data.scalpel.admin.app.datafile.domain.DataFile;
import cn.superhuang.data.scalpel.admin.app.datafile.model.DataFileAddDTO;
import cn.superhuang.data.scalpel.admin.app.datafile.model.DataFileMetadata;
import cn.superhuang.data.scalpel.admin.app.datafile.model.DataFileUpdateDTO;
import cn.superhuang.data.scalpel.admin.app.datafile.repository.DataFileRepository;
import cn.superhuang.data.scalpel.admin.app.datafile.service.parser.DataFileMetadataParser;
import cn.superhuang.data.scalpel.admin.app.sys.repository.CatalogRepository;
import cn.superhuang.data.scalpel.admin.config.MinioConfig;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class DataFileService {
    @Resource
    private DataFileRepository dataFileRepository;
    @Resource
    private CatalogRepository catalogRepository;
    @Resource
    private List<DataFileMetadataParser> metadataParserList;
    @Resource
    public MinioClient minioClient;
    @Resource
    public MinioConfig minioConfig;

    public void add(DataFileAddDTO dataFileAdd) {
        MultipartFile file = dataFileAdd.getFile();
        if (file == null) {
            throw new RuntimeException("文件不能为空");
        }

        DataFile dataFile = BeanUtil.copyProperties(dataFileAdd, DataFile.class);
        dataFile.setName(file.getOriginalFilename());
        dataFileRepository.save(dataFile);

        catalogRepository.findById(dataFile.getCatalogId()).ifPresentOrElse(catalog -> {
            String objectName = DatePattern.NORM_DATE_FORMAT.format(dataFile.getCreatedDate()) + "/" + dataFile.getId() + "." + FileNameUtil.extName(dataFile.getName());
            try {
                PutObjectArgs objectArgs = PutObjectArgs.builder().bucket(minioConfig.getBucketName()).object(objectName)
                        .stream(file.getInputStream(), file.getSize(), -1).contentType(file.getContentType()).build();
                minioClient.putObject(objectArgs);
            } catch (Exception e) {
                throw new RuntimeException("上传文件到minio失败");
            }
        }, () -> {
            throw new RuntimeException("目录不存在");
        });
        //TODO 调用s3上传文件
        //TODO 获取metabase存储
    }

    public void update(DataFileUpdateDTO dataFileUpdate) {
        dataFileRepository.findById(dataFileUpdate.getId()).ifPresent(po -> {
            BeanUtil.copyProperties(dataFileUpdate, po, CopyOptions.create().ignoreNullValue());
            dataFileRepository.save(po);
            if (dataFileUpdate.getFile() != null) {
                //TODO 调用s3上传文件
                //TODO 获取metabase存储
            }
        });
    }

    public void delete(String id) {
        dataFileRepository.findById(id).ifPresent(po -> {
            dataFileRepository.delete(po);
            //TODO 删除S3上的文件
        });
    }


    private DataFileMetadata getMetadata(DataFile dataFile) throws Exception {
        for (DataFileMetadataParser dataFileMetadataParser : metadataParserList) {
            if (dataFileMetadataParser.support(dataFile.getType())) {
                return dataFileMetadataParser.parse(dataFile);
            }
        }
        throw new RuntimeException("不支持的文件类型:" + dataFile.getType());
    }
}