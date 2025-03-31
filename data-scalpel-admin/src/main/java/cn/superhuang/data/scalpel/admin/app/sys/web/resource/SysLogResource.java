package cn.superhuang.data.scalpel.admin.app.sys.web.resource;

import cn.superhuang.data.scalpel.admin.app.sys.domain.SysLog;
import cn.superhuang.data.scalpel.admin.app.sys.repository.SysLogRepository;
import cn.superhuang.data.scalpel.admin.app.sys.service.SysLogService;
import cn.superhuang.data.scalpel.model.web.GenericResponse;
import cn.superhuang.data.scalpel.web.GenericSearchRequestDTO;
import cn.superhuang.data.scalpel.impl.BaseResource;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SysLogResource extends BaseResource implements ISysLogResource {
    @Resource
    private SysLogRepository repository;
    @Resource
    private SysLogService service;

    @Override
    public GenericResponse<Page<SysLog>> search(GenericSearchRequestDTO searchRequest) {
        Specification<SysLog> spec = resolveSpecification(searchRequest.getSearch(), SysLog.class);
        PageRequest pageRequest = resolvePageRequest(searchRequest.getLimit(), searchRequest.getSort());
        Page<SysLog> page = repository.findAll(spec, pageRequest);
        return GenericResponse.ok(page);
    }

//    @Override
//    public GenericResponse<Void> add(SysLogCreateRequest createRequest) {
//        SysLog log = BeanUtil.copyProperties(createRequest, SysLog.class);
//        service.add(log);
//        return GenericResponse.ok();
//    }
}
