package cn.superhuang.data.scalpel.admin.app.model.web.resource;

import cn.hutool.core.bean.BeanUtil;
import cn.superhuang.data.scalpel.admin.app.model.domain.Model;
import cn.superhuang.data.scalpel.admin.app.model.domain.ModelField;

import cn.superhuang.data.scalpel.admin.app.model.model.ModelUpdateDTO;
import cn.superhuang.data.scalpel.admin.app.model.repository.ModelFieldRepository;
import cn.superhuang.data.scalpel.admin.app.model.repository.ModelRepository;
import cn.superhuang.data.scalpel.admin.app.model.service.ModeDataService;
import cn.superhuang.data.scalpel.admin.app.model.service.ModelService;
import cn.superhuang.data.scalpel.admin.app.model.web.resource.request.ModelUpdateRequest;
import cn.superhuang.data.scalpel.admin.resource.impl.BaseResource;
import cn.superhuang.data.scalpel.admin.app.model.web.resource.request.ModelCreateRequest;
import cn.superhuang.data.scalpel.admin.app.model.web.resource.request.ModelFieldUpdateRequest;
import cn.superhuang.data.scalpel.admin.app.model.web.resource.response.ModelDetailVO;
import cn.superhuang.data.scalpel.admin.app.model.web.resource.response.ModelListItemVO;
import cn.superhuang.data.scalpel.admin.model.web.GenericSearchRequestDTO;
import cn.superhuang.data.scalpel.app.model.model.ModelFieldDTO;
import cn.superhuang.data.scalpel.model.web.GenericResponse;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class ModelResource extends BaseResource implements IModelResource {
    @Resource
    private ModelRepository repository;
    @Resource
    private ModelFieldRepository fieldRepository;
    @Resource
    private ModelService modelService;
    @Resource
    private ModeDataService modeDataService;

    @Override
    public GenericResponse<Page<ModelListItemVO>> search(GenericSearchRequestDTO searchRequest) {
        Specification<Model> spec = resolveSpecification(searchRequest.getSearch(), Model.class);
        PageRequest pageRequest = resolvePageRequest(searchRequest.getLimit(), searchRequest.getSort());
        Page<Model> page = repository.findAll(spec, pageRequest);
        List<ModelListItemVO> listVo = BeanUtil.copyToList(page.getContent(), ModelListItemVO.class);
        Page<ModelListItemVO> result = new PageImpl<>(listVo, page.getPageable(), page.getTotalElements());
        return GenericResponse.ok(result);
    }

    @Override
    public GenericResponse<Page<Map<String, Object>>> searchModelData(String id, GenericSearchRequestDTO searchRequest) {
        Page<Map<String, Object>> result = modeDataService.searchData(id, searchRequest.getSearch(), searchRequest.getLimit(), searchRequest.getSort());
        return GenericResponse.ok(result);
    }

    @Override
    public GenericResponse<ModelDetailVO> detail(String id) {
        Optional<ModelDetailVO> detailVo = modelService.detail(id).map(model -> BeanUtil.copyProperties(model, ModelDetailVO.class));
        return GenericResponse.wrapOrNotFound(detailVo);
    }

    @Override
    public GenericResponse<ModelDetailVO> create(ModelCreateRequest createRequest) throws Exception {
        Model model = BeanUtil.copyProperties(createRequest, Model.class);
        model = modelService.create(model, null);
        ModelDetailVO vo = BeanUtil.copyProperties(model, ModelDetailVO.class);
        return GenericResponse.ok(vo);
    }

    @Override
    public GenericResponse<Void> update(String id, ModelUpdateRequest updateRequest) throws Exception {
        ModelUpdateDTO modelUpdateDTO = BeanUtil.copyProperties(updateRequest, ModelUpdateDTO.class);
        modelUpdateDTO.setId(id);
        modelService.update(modelUpdateDTO);
        return GenericResponse.ok();
    }

    @Override
    public GenericResponse<Void> delete(String id) throws Exception {
        modelService.delete(id);
        return GenericResponse.ok();
    }

    @Override
    public GenericResponse<List<ModelFieldDTO>> getFields(String id) throws Exception {
        List<ModelField> fields = fieldRepository.findAllByModelId(id);
        List<ModelFieldDTO> result = BeanUtil.copyToList(fields, ModelFieldDTO.class);
        return GenericResponse.ok(result);
    }

    @Override
    public GenericResponse<Void> updateFields(String id, ModelFieldUpdateRequest updateRequest) throws Exception {
        return GenericResponse.ok();
    }

    @Override
    public GenericResponse<Void> createTable(String id) throws Exception {
        modelService.createTable(id);
        return GenericResponse.ok();
    }

    @Override
    public GenericResponse<Void> forceRecreateTable(String id) throws Exception {
        modelService.recreateTable(id);
        return GenericResponse.ok();
    }

    @Override
    public GenericResponse<Void> online(String id) throws Exception {
        modelService.online(id);
        return GenericResponse.ok();
    }

    @Override
    public GenericResponse<Void> offline(String id) throws Exception {
        modelService.offline(id);
        return GenericResponse.ok();
    }
}
