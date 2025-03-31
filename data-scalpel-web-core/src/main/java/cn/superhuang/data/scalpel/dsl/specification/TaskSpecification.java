package cn.superhuang.data.scalpel.dsl.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskSpecification<Task> implements Specification<Task> {
    private SpecSearchCriteria criteria;

    @Override
    public Predicate toPredicate(
            Root<Task> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

        switch (criteria.getOperation()) {
            case EQUALITY:
                return builder.equal(root.get(criteria.getKey()), criteria.getValue());
            case NEGATION:
                return builder.notEqual(root.get(criteria.getKey()), criteria.getValue());
            case GREATER_THAN:
                return builder.greaterThan(root.<String>get(
                    criteria.getKey()), criteria.getValue().toString());
            case LESS_THAN:
                return builder.lessThan(root.<String>get(
                    criteria.getKey()), criteria.getValue().toString());
            case LIKE:
                return builder.like(root.<String>get(
                    criteria.getKey()), criteria.getValue().toString());
            case STARTS_WITH:
                return builder.like(root.<String>get(criteria.getKey()), criteria.getValue() + "%");
            case ENDS_WITH:
                return builder.like(root.<String>get(criteria.getKey()), "%" + criteria.getValue());
            case CONTAINS:
                return builder.like(root.<String>get(
                    criteria.getKey()), "%" + criteria.getValue() + "%");
            default:
                return null;
        }
    }
}
