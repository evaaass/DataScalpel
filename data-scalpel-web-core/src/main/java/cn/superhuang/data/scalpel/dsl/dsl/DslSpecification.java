package cn.superhuang.data.scalpel.dsl.dsl;

import cn.superhuang.data.scalpel.dsl.specification.SpecSearchCriteria;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class DslSpecification {
    private SpecSearchCriteria criteria;

    public BooleanExpression toPredicate(PathBuilder pathBuilder) {
        switch (criteria.getOperation()) {
            case EQUALITY:
                return pathBuilder.get(pathBuilder.get(criteria.getKey())).eq(criteria.getValue());
            case NEGATION:
                return pathBuilder.get(pathBuilder.get(criteria.getKey())).ne(criteria.getValue());
            case GREATER_THAN:
                return pathBuilder.get(pathBuilder.getNumber(criteria.getKey(), Long.class)).gt((Number) criteria.getValue());
            case LESS_THAN:
                return pathBuilder.get(pathBuilder.getNumber(criteria.getKey(), Long.class)).loe((Number) criteria.getValue());
            case LIKE:
                return pathBuilder.get(pathBuilder.getString(criteria.getKey())).like(criteria.getValue().toString());
            case STARTS_WITH:
                return pathBuilder.get(pathBuilder.getString(criteria.getKey())).like(criteria.getValue() + "%");
            case ENDS_WITH:
                return pathBuilder.get(pathBuilder.getString(criteria.getKey())).like("%" + criteria.getValue());
            case CONTAINS:
                return pathBuilder.get(pathBuilder.getString(criteria.getKey())).like("%" + criteria.getValue() + "%");
            default:
                return null;
        }
    }

}
