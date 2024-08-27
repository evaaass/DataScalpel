package cn.superhuang.data.scalpel.admin.app.datafile.web.resource;

import cn.hutool.core.util.StrUtil;
import cn.superhuang.data.scalpel.admin.app.datafile.model.DataFileUpdateDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;

import cn.superhuang.data.scalpel.admin.app.datafile.domain.DataFile;
import cn.superhuang.data.scalpel.admin.app.datafile.model.DataFileAddDTO;
import cn.superhuang.data.scalpel.admin.app.datafile.model.enumeration.DataFileType;
import cn.superhuang.data.scalpel.admin.app.datafile.repository.DataFileRepository;
import cn.superhuang.data.scalpel.admin.app.datafile.service.DataFileService;
import cn.superhuang.data.scalpel.admin.model.web.GenericSearchRequestDTO;
import cn.superhuang.data.scalpel.admin.resource.impl.BaseResource;
import cn.superhuang.data.scalpel.model.web.GenericResponse;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Controller
public class DataFileResource extends BaseResource implements IDataFileResource {

    @Resource
    private DataFileRepository dataFileRepository;
    @Resource

    private DataFileService dataFileService;
    @Resource
    private ObjectMapper objectMapper;

    @Override
    public GenericResponse<Page<DataFile>> search(GenericSearchRequestDTO searchRequest) {
        Specification<DataFile> spec = resolveSpecification(searchRequest.getSearch(), DataFile.class);
        PageRequest pageRequest = resolvePageRequest(searchRequest.getLimit(), searchRequest.getSort());
        Page<DataFile> page = dataFileRepository.findAll(spec, pageRequest);
        return GenericResponse.ok(page);
    }

    @Override
    public GenericResponse<DataFile> createFile(String alias, String catalogId, DataFileType type, String description, String options, MultipartFile file) throws JsonProcessingException {
        DataFileAddDTO dataFileAddDTO = new DataFileAddDTO();
        dataFileAddDTO.setCatalogId(catalogId);
        dataFileAddDTO.setAlias(alias);
        dataFileAddDTO.setDescription(description);
        dataFileAddDTO.setType(type);
        dataFileAddDTO.setFile(file);

        if (StrUtil.isNotBlank(options)) {
            dataFileAddDTO.setProps(objectMapper.readValue(options, new TypeReference<Map<String, String>>() {
            }));
        } else {
            dataFileAddDTO.setProps(Maps.newHashMap());
        }
        DataFile dataFile = dataFileService.add(dataFileAddDTO);
        return GenericResponse.ok(dataFile);
    }

    @Override
    public GenericResponse<Void> updateFile(String id, String alias, String catalogId, String description, String options, MultipartFile file) throws JsonProcessingException {
        DataFileUpdateDTO dataFileUpdateDTO = new DataFileUpdateDTO();
        dataFileUpdateDTO.setId(id);
        dataFileUpdateDTO.setCatalogId(catalogId);
        dataFileUpdateDTO.setAlias(alias);
        dataFileUpdateDTO.setDescription(description);
        if (StrUtil.isNotBlank(options)) {
            dataFileUpdateDTO.setProps(objectMapper.readValue(options, new TypeReference<Map<String, String>>() {
            }));
        }

        dataFileUpdateDTO.setFile(file);
        dataFileService.update(dataFileUpdateDTO);
        return GenericResponse.ok();
    }

    @Override
    public GenericResponse<Void> delete(String id) {
        dataFileService.delete(id);
        return GenericResponse.ok();
    }
}
