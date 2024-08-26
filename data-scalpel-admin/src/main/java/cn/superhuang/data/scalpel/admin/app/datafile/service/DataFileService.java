package cn.superhuang.data.scalpel.admin.app.datafile.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
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
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

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
            BeanUtil.copyProperties(dataFileUpdate, po, CopyOptions.create().ignoreNullValue());
            dataFileRepository.save(po);
            if (dataFileUpdate.getFile() != null) {
                uploadAndParse(po, dataFileUpdate.getFile());
            }
        });
    }

    private void uploadAndParse(DataFile dataFile, MultipartFile file) {
        try {
            String objectName = DatePattern.NORM_DATE_FORMAT.format(dataFile.getCreatedDate()) + "/" + dataFile.getId() + "." + FileNameUtil.extName(dataFile.getName());
            try {
                //如果是GDB或者SHP，则要解压zip后上传
                if (dataFile.getType() == DataFileType.SHP || dataFile.getType() == DataFileType.GDB) {
                    //TODO 测试zip上传
                    String tempUnzipPath = FileUtil.getTmpDirPath() + "/" + StrUtil.uuid();
                    File tempUnzipFolder = new File(tempUnzipPath);
                    ZipUtil.unzip(file.getInputStream(), tempUnzipFolder, StandardCharsets.UTF_8);
                    List<String> files = FileUtil.listFileNames(tempUnzipPath);
                    for (String filePath : files) {
                        String tempFilePath = tempUnzipPath + "/" + filePath;
                        String subObjectName = objectName + "/" + filePath;
                        ObjectWriteResponse uploadRes = minioClient.uploadObject(UploadObjectArgs.builder().bucket(minioConfig.getBucketName()).object(subObjectName).filename(tempFilePath).build());
                        System.out.println(uploadRes);
                    }
                    FileUtil.del(tempUnzipFolder);
                } else {
                    PutObjectArgs objectArgs = PutObjectArgs.builder().bucket(minioConfig.getBucketName()).object(objectName).stream(file.getInputStream(), file.getSize(), -1).contentType(file.getContentType()).build();
                    minioClient.putObject(objectArgs);
                }
            } catch (Exception e) {
                throw new RuntimeException("上传文件到minio失败");
            }
            DataFileMetadata dataFileMetadata = getMetadata(dataFile);
            String metadataJson = objectMapper.writeValueAsString(dataFileMetadata);
            dataFile.setMetadata(metadataJson);
            dataFile.setState(DataFileState.OK);
            dataFileRepository.save(dataFile);
        } catch (Exception e) {
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