package cn.superhuang.data.scalpel.dsl.specification;

import jakarta.persistence.criteria.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenericSpecification<T> implements Specification<T> {
    private SpecSearchCriteria criteria;

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

        Path path = null;

        for (String key : criteria.getKey().split("\\.")) {
            if (path == null) {
                path = root.get(key);
            } else {
                path = path.get(key);
            }
        }

        switch (criteria.getOperation()) {
            case EQUALITY:
                return builder.equal(path, criteria.getValue());
            case NEGATION:
                return builder.notEqual(path, criteria.getValue());
            case GREATER_THAN:
                return builder.greaterThan(path, criteria.getValue().toString());
            case LESS_THAN:
                return builder.lessThan(path, criteria.getValue().toString());
            case LIKE:
                return builder.like(path, criteria.getValue().toString());
            case STARTS_WITH:
                return builder.like(path, criteria.getValue() + "%");
            case ENDS_WITH:
                return builder.like(path, "%" + criteria.getValue());
            case CONTAINS:
                return builder.like(path, "%" + criteria.getValue() + "%");
            default:
                return null;
        }
    }
}
