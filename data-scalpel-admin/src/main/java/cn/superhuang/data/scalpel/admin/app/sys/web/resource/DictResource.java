package cn.superhuang.data.scalpel.admin.app.sys.web.resource;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.superhuang.data.scalpel.exception.BaseException;
import cn.superhuang.data.scalpel.admin.app.sys.domain.Dict;
import cn.superhuang.data.scalpel.admin.app.sys.repository.DictRepository;
import cn.superhuang.data.scalpel.admin.app.sys.web.resource.request.DictCreateRequestVO;
import cn.superhuang.data.scalpel.admin.app.sys.web.resource.request.DictUpdateRequestVO;
import cn.superhuang.data.scalpel.web.GenericSearchRequestDTO;
import cn.superhuang.data.scalpel.impl.BaseResource;
import cn.superhuang.data.scalpel.model.web.GenericResponse;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.RestController;

import java.net.URISyntaxException;

@RestController
public class DictResource extends BaseResource implements IDictResource, InitializingBean {

    @Resource
    private DictRepository dictRepository;


    @Override
    public GenericResponse<Page<Dict>> search(GenericSearchRequestDTO searchRequest) {
        Specification<Dict> spec = resolveSpecification(searchRequest.getSearch(), Dict.class);
        PageRequest pageRequest = resolvePageRequest(searchRequest.getLimit(), searchRequest.getSort());
        Page<Dict> page = dictRepository.findAll(spec, pageRequest);
        return GenericResponse.ok(page);
    }

    @Override
    public GenericResponse<Dict> create(DictCreateRequestVO createRequest) throws Exception {
        Dict dict = BeanUtil.copyProperties(createRequest, Dict.class);
        dict = dictRepository.save(dict);
        return GenericResponse.ok(dict);
    }

    @Override
    public GenericResponse<Void> update(@NotNull String id, DictUpdateRequestVO updateRequest) throws URISyntaxException {
        dictRepository.findById(id).ifPresent(po->{
            BeanUtil.copyProperties(updateRequest, po, CopyOptions.create().ignoreNullValue());
            dictRepository.save(po);
        });
        return GenericResponse.ok();
    }

    @Override
    public GenericResponse<Void> delete(@NotNull String id) {
        dictRepository.findById(id).ifPresent(dict -> {
            Long childrenCount = dictRepository.countAllByTypeAndParentId(dict.getType(), dict.getId());
            if (childrenCount > 0) {
                throw new BaseException("包含子节点，无法删除");
            }
            dictRepository.delete(dict);
        });
        return GenericResponse.ok();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("初始化完毕");
    }
}
