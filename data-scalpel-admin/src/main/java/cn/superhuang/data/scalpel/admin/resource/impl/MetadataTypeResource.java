package cn.superhuang.data.scalpel.admin.resource.impl;

import cn.superhuang.data.scalpel.model.web.GenericResponse;
import cn.superhuang.data.scalpel.admin.model.web.GenericSearchRequestDTO;
import cn.superhuang.data.scalpel.admin.repository.MetadataTypeRepository;
import cn.superhuang.data.scalpel.admin.app.item.domain.MetadataType;
import cn.superhuang.data.scalpel.admin.web.resource.IMetadataTypeResource;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MetadataTypeResource extends BaseResource implements IMetadataTypeResource {
    @Resource
    private MetadataTypeRepository metadataTypeRepository;

    @Override
    public GenericResponse<Page<MetadataType>> search(GenericSearchRequestDTO searchRequest) {
        Specification<MetadataType> spec = resolveSpecification(searchRequest.getSearch(), MetadataType.class);
        PageRequest pageRequest = resolvePageRequest(searchRequest.getLimit(), searchRequest.getSort());
        Page<MetadataType> result = metadataTypeRepository.findAll(spec, pageRequest);
        return GenericResponse.ok(result);
    }

    @Override
    public GenericResponse<MetadataType> create(@RequestBody MetadataType metadataType) throws Exception {
        metadataType = metadataTypeRepository.save(metadataType);
        return GenericResponse.ok(metadataType);
    }

    @Override
    public GenericResponse<MetadataType> update(String id, @RequestBody MetadataType metadataType) throws Exception {
        metadataType.setId(id);
        metadataType = metadataTypeRepository.save(metadataType);
        return GenericResponse.ok(metadataType);
    }

    @Override
    public GenericResponse<Void> delete(String id) throws Exception {
        metadataTypeRepository.deleteById(id);
        return GenericResponse.ok();
    }
}
