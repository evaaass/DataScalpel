package cn.superhuang.data.scalpel.admin.resource.impl;

import cn.superhuang.data.scalpel.admin.dsl.specification.CriteriaParser;
import cn.superhuang.data.scalpel.admin.dsl.specification.GenericSpecification;
import cn.superhuang.data.scalpel.admin.dsl.specification.GenericSpecificationsBuilder;
import cn.superhuang.data.scalpel.admin.util.GenericSearchUtil;
import freemarker.core.AliasTemplateDateFormatFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
