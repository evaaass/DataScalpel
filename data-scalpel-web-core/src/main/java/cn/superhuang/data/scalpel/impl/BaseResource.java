package cn.superhuang.data.scalpel.impl;

import cn.superhuang.data.scalpel.util.GenericSearchUtil;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = "*", allowedHeaders = "*")
public class BaseResource {

    protected <T> Specification<T> resolveSpecification(String searchParameters, Class<T> tClass) {
        return GenericSearchUtil.resolveSpecification(searchParameters, tClass);
    }

    protected PageRequest resolvePageRequest(String limit, String sort) {
        return GenericSearchUtil.resolvePageRequest(limit, sort);

    }
}
