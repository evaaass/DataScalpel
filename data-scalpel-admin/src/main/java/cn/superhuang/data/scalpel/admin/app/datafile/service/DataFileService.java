package cn.superhuang.data.scalpel.admin.app.datafile.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.compress.ZipReader;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import cn.superhuang.data.scalpel.admin.app.datafile.domain.DataFile;
import cn.superhuang.data.scalpel.admin.app.datafile.model.DataFileAddDTO;
import cn.superhuang.data.scalpel.admin.app.datafile.model.DataFileMetadata;
import cn.superhuang.data.scalpel.admin.app.datafile.model.DataFileUpdateDTO;
import cn.superhuang.data.scalpel.admin.app.datafile.model.enumeration.DataFileState;
import cn.superhuang.data.scalpel.admin.app.datafile.model.enumeration.DataFileType;
import cn.superhuang.data.scalpel.admin.app.datafile.repository.DataFileRepository;
import cn.superhuang.data.scalpel.admin.app.datafile.service.parser.DataFileMetadataParser;
import cn.superhuang.data.scalpel.admin.config.MinioConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.minio.*;
import io.minio.messages.Item;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

//TODO 要考虑GDB和SHP解压缩的问题。。。。。。。。。。。。。。。

@Service
public class DataFileService {
    @Resource
    private DataFileRepository dataFileRepository;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private List<DataFileMetadataParser> metadataParserList;
    @Resource
    public MinioClient minioClient;
    @Resource
    public MinioConfig minioConfig;

    public DataFile add(DataFileAddDTO dataFileAdd) {
        MultipartFile file = dataFileAdd.getFile();
        if (file == null) {
            throw new RuntimeException("文件不能为空");
        }

        DataFile dataFile = BeanUtil.copyProperties(dataFileAdd, DataFile.class);
        dataFile.setName(file.getOriginalFilename());
        dataFile.setState(DataFileState.PARSING);
        dataFileRepository.save(dataFile);
        uploadAndParse(dataFile, file);
        return dataFile;
    }

    public void update(DataFileUpdateDTO dataFileUpdate) {
        dataFileRepository.findById(dataFileUpdate.getId()).ifPresent(po -> {
            Map<String, String> newProps = dataFileUpdate.getProps();
            Map<String, String> oldProps = po.getProps();
            Boolean propsChanged = false;
            if (!newProps.equals(oldProps)) {
                propsChanged = true;
            }
            BeanUtil.copyProperties(dataFileUpdate, po, CopyOptions.create().ignoreNullValue());
            dataFileRepository.save(po);
            try {
                if (dataFileUpdate.getFile() != null) {
                    uploadAndParse(po, dataFileUpdate.getFile());
                } else if (dataFileUpdate.getFile() == null && propsChanged) {
                    DataFileMetadata dataFileMetadata = getMetadata(po);
                    String metadataJson = objectMapper.writeValueAsString(dataFileMetadata);
                    po.setMetadata(metadataJson);
                    dataFileRepository.save(po);
                }
            } catch (Exception e) {
                throw new RuntimeException("更新元数据失败:" + e.getMessage(), e);
            }

        });
    }

    private void uploadAndParse(DataFile dataFile, MultipartFile file) {
        try {
            try {
                //如果是GDB或者SHP，则要解压zip后上传
                if (dataFile.getType() == DataFileType.SHP || dataFile.getType() == DataFileType.GDB) {
                    String objectName = DatePattern.NORM_DATE_FORMAT.format(dataFile.getCreatedDate()) + "/" + dataFile.getId();
                    if (dataFile.getType() == DataFileType.GDB) {
                        objectName = objectName + "." + dataFile.getType().getExtName();
                    }

                    //删除原有的先.....
                    Iterable<Result<Item>> results = minioClient.listObjects(
                            ListObjectsArgs.builder().bucket(minioConfig.getBucketName()).prefix(objectName).recursive(true).build()
                    );
                    for (Result<Item> result : results) {
                        Item item = result.get();
                        String deleteObjectName = item.objectName();
                        minioClient.removeObject(
                                RemoveObjectArgs.builder().bucket(minioConfig.getBucketName()).object(deleteObjectName).build()
                        );
                    }
                    //TODO 测试zip上传
                    String tempUnzipPath = FileUtil.getTmpDirPath() + "/" + StrUtil.uuid();
                    File tempUnzipFolder = new File(tempUnzipPath);
                    try (final ZipReader reader = new ZipReader(file.getInputStream(), StandardCharsets.UTF_8).setMaxSizeDiff(10000)) {
                        reader.readTo(tempUnzipFolder);
                    }
//                    ZipUtil.unzip(file.getInputStream(), tempUnzipFolder, StandardCharsets.UTF_8);
                    List<String> files = FileUtil.listFileNames(tempUnzipPath);
                    for (String filePath : files) {
                        String tempFilePath = tempUnzipPath + "/" + filePath;
                        String subObjectName = objectName + "/" + filePath;
                        ObjectWriteResponse uploadRes = minioClient.uploadObject(UploadObjectArgs.builder().bucket(minioConfig.getBucketName()).object(subObjectName).filename(tempFilePath).build());
                    }
                    FileUtil.del(tempUnzipFolder);
                } else {
                    String objectName = DatePattern.NORM_DATE_FORMAT.format(dataFile.getCreatedDate()) + "/" + dataFile.getId() + "." + dataFile.getType().getExtName();
                    //单文件会直接覆盖，不用先删除了
                    PutObjectArgs objectArgs = PutObjectArgs.builder().bucket(minioConfig.getBucketName()).object(objectName).stream(file.getInputStream(), file.getSize(), -1).contentType(file.getContentType()).build();
                    minioClient.putObject(objectArgs);
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("上传文件到minio失败:" + e.getMessage(), e);
            }
            DataFileMetadata dataFileMetadata = getMetadata(dataFile);
            String metadataJson = objectMapper.writeValueAsString(dataFileMetadata);
            dataFile.setMetadata(metadataJson);
            dataFile.setState(DataFileState.OK);
            dataFileRepository.save(dataFile);
        } catch (Exception e) {
            e.printStackTrace();
            dataFile.setState(DataFileState.ERROR);
            dataFileRepository.save(dataFile);
        }
    }

    public void delete(String id) {
        dataFileRepository.findById(id).ifPresent(dataFile -> {
            dataFileRepository.delete(dataFile);
            String objectName = DatePattern.NORM_DATE_FORMAT.format(dataFile.getCreatedDate()) + "/" + dataFile.getId() + "." + FileNameUtil.extName(dataFile.getName());
            try {
                //如果是GDB或者SHP，则要删整个文件夹
                RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder().bucket(minioConfig.getBucketName()).object(objectName).build();
                minioClient.removeObject(removeObjectArgs);
            } catch (Exception e) {
                throw new RuntimeException("删除文件失败");
            }
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