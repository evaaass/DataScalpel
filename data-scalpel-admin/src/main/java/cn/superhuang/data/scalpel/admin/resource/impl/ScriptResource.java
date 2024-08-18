package cn.superhuang.data.scalpel.admin.resource.impl;

import cn.superhuang.data.scalpel.model.web.GenericResponse;
import cn.superhuang.data.scalpel.admin.model.web.GenericSearchRequestDTO;
import cn.superhuang.data.scalpel.admin.model.web.vo.ScriptDetailVO;
import cn.superhuang.data.scalpel.admin.model.web.vo.ScriptListItemVO;
import cn.superhuang.data.scalpel.admin.repository.ScriptRepository;
import cn.superhuang.data.scalpel.admin.web.resource.request.ScriptCreateRequestVO;
import cn.superhuang.data.scalpel.admin.web.resource.request.ScriptUpdateRequestVO;
import cn.superhuang.data.scalpel.admin.service.ScriptService;
import cn.superhuang.data.scalpel.admin.web.resource.IScriptResource;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;

@Controller
public class ScriptResource extends BaseResource implements IScriptResource {

    @Resource
    private ScriptRepository scriptRepository;
    @Resource
    private ScriptService scriptService;


    @Override
    public GenericResponse<Page<ScriptListItemVO>> search(GenericSearchRequestDTO searchRequest) {
//        Specification<Script> spec = resolveSpecification(searchRequest.getSearch(), Script.class);
//        PageRequest pageRequest = resolvePageRequest(searchRequest.getLimit(), searchRequest.getSort());
//        Page<Script> page = scriptRepository.findAll(spec, pageRequest);
//        List<ScriptListItemVO> listVo = scriptMapper.toListVo(page.getContent());
//        Page<ScriptListItemVO> result = new PageImpl<>(listVo, page.getPageable(), page.getTotalElements());
        return GenericResponse.ok();
    }

    @Override
    public GenericResponse<ScriptDetailVO> createScript(ScriptCreateRequestVO createRequest) throws Exception {
//        String fileContent = null;
//        if (createRequest.getContentFile() != null) {
//            fileContent = IoUtil.read(createRequest.getContentFile().getInputStream(), UTF_8);
//        }
//        ScriptDTO scriptDTO = scriptMapper.toDto(createRequest);
//        if (fileContent != null) {
//            scriptDTO.setContent(fileContent);
//        }
//        scriptDTO = scriptService.save(scriptDTO);
//        ScriptDetailVO vo = scriptMapper.toVo(scriptDTO);
        return GenericResponse.ok();
    }

    @Override
    public GenericResponse<ScriptDetailVO> updateScript(String id, ScriptUpdateRequestVO updateRequest) throws Exception {
//        String fileContent = null;
//        if (updateRequest.getContentFile() != null) {
//            fileContent = IoUtil.read(updateRequest.getContentFile().getInputStream(), UTF_8);
//        }
//        ScriptUpdateDTO scriptUpdateDTO = scriptMapper.toDto(updateRequest);
//        scriptUpdateDTO.setId(id);
//        if (fileContent != null) {
//            scriptUpdateDTO.setContent(fileContent);
//        }
//        Optional<ScriptDetailVO> result = scriptService.update(scriptUpdateDTO).map(scriptMapper::toVo);
        return GenericResponse.ok();
    }

    @Override
    public GenericResponse<Void> deleteScript(String id) throws Exception {
        scriptService.delete(id);
        return GenericResponse.ok();
    }
}
