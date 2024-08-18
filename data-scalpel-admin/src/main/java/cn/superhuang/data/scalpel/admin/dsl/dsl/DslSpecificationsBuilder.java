package cn.superhuang.data.scalpel.admin.dsl.dsl;


import cn.superhuang.data.scalpel.admin.dsl.specification.SearchOperation;
import cn.superhuang.data.scalpel.admin.dsl.specification.SpecSearchCriteria;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import org.springframework.data.jpa.domain.Specification;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DslSpecificationsBuilder {

    private final List<SpecSearchCriteria> params;

    public DslSpecificationsBuilder() {
        this.params = new ArrayList<>();
    }

    public final DslSpecificationsBuilder with(final String key, final String operation, final Object value, final String prefix, final String suffix) {
        return with(null, key, operation, value, prefix, suffix);
    }

    public final DslSpecificationsBuilder with(final String precedenceIndicator, final String key, final String operation, final Object value, final String prefix, final String suffix) {
        SearchOperation op = SearchOperation.getSimpleOperation(operation.charAt(0));
        if (op != null) {
            if (op == SearchOperation.EQUALITY) // the operation may be complex operation
            {
                final boolean startWithAsterisk = prefix != null && prefix.contains(SearchOperation.ZERO_OR_MORE_REGEX);
                final boolean endWithAsterisk = suffix != null && suffix.contains(SearchOperation.ZERO_OR_MORE_REGEX);

                if (startWithAsterisk && endWithAsterisk) {
                    op = SearchOperation.CONTAINS;
                } else if (startWithAsterisk) {
                    op = SearchOperation.ENDS_WITH;
                } else if (endWithAsterisk) {
                    op = SearchOperation.STARTS_WITH;
                }
            }
            params.add(new SpecSearchCriteria(precedenceIndicator, key, op, value));
        }
        return this;
    }

    public Specification build(Function<SpecSearchCriteria, Specification> converter) {

        if (params.size() == 0) {
            return null;
        }

        final List<Specification> specs = params.stream()
                .map(converter)
                .collect(Collectors.toCollection(ArrayList::new));

        Specification result = specs.get(0);

        for (int idx = 1; idx < specs.size(); idx++) {
            result = params.get(idx)
                    .isOrPredicate()
                    ? Specification.where(result)
                    .or(specs.get(idx))
                    : Specification.where(result)
                    .and(specs.get(idx));
        }

        return result;
    }

    public BooleanExpression build(PathBuilder pathBuilder, Deque<?> postFixedExprStack, Function<SpecSearchCriteria, DslSpecification> converter) {

        Deque<BooleanExpression> specStack = new LinkedList<>();

        Collections.reverse((List<?>) postFixedExprStack);

        while (!postFixedExprStack.isEmpty()) {
            Object mayBeOperand = postFixedExprStack.pop();

            if (!(mayBeOperand instanceof String)) {
                specStack.push(converter.apply((SpecSearchCriteria) mayBeOperand).toPredicate(pathBuilder));
            } else {
                BooleanExpression operand1 = specStack.pop();
                BooleanExpression operand2 = specStack.pop();
                if (mayBeOperand.equals(SearchOperation.AND_OPERATOR))
                    specStack.push(operand1.and(operand2));
                else if (mayBeOperand.equals(SearchOperation.OR_OPERATOR))
                    specStack.push(operand1.or(operand2));
            }

        }
        return specStack.pop();

    }
}
