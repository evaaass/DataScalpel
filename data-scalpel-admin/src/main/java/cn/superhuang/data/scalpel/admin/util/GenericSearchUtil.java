package cn.superhuang.data.scalpel.admin.util;

import cn.superhuang.data.scalpel.admin.dsl.specification.CriteriaParser;
import cn.superhuang.data.scalpel.admin.dsl.specification.GenericSpecification;
import cn.superhuang.data.scalpel.admin.dsl.specification.GenericSpecificationsBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

public class GenericSearchUtil {


    public static <T> Specification<T> resolveSpecification(String searchParameters, Class<T> tClass) {
        if (StringUtils.isEmpty(searchParameters)) {
            return Specification.where(null);
        }
        return new GenericSpecificationsBuilder<T>().build(new CriteriaParser<T>().parse(searchParameters, tClass), GenericSpecification<T>::new);
    }

    public static PageRequest resolvePageRequest(String limit, String sort) {
        // 解析 limit 字符串
        int offset = 0;
        int size = 20;
        if (limit != null && !limit.isEmpty()) {
            String[] limitParts = limit.split(",");
            if (limitParts.length == 1) {
                size = Integer.parseInt(limitParts[0]);
            } else if (limitParts.length == 2) {
                offset = Integer.parseInt(limitParts[0]);
                size = Integer.parseInt(limitParts[1]);
            }
        }
        Sort.Order[] orders = null;
        // 解析 sort 字符串
        if (sort != null && !sort.isEmpty()) {
            String[] sortParts = sort.split(",");
            orders = new Sort.Order[sortParts.length];
            for (int i = 0; i < sortParts.length; i++) {
                String property = sortParts[i].startsWith("-") ? sortParts[i].substring(1) : sortParts[i];
                Sort.Direction direction = sortParts[i].startsWith("-") ? Sort.Direction.DESC : Sort.Direction.ASC;
                orders[i] = new Sort.Order(direction, property);
            }
        }
        // 创建 PageRequest 对象
        PageRequest pageRequest = PageRequest.of(offset, size);
        if (orders != null && orders.length > 0) {
            pageRequest.withSort(Sort.by(orders));
        }
        return pageRequest;
    }

}
