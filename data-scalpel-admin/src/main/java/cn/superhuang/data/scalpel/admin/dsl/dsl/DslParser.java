package cn.superhuang.data.scalpel.admin.dsl.dsl;

import cn.superhuang.data.scalpel.admin.dsl.specification.SearchOperation;
import cn.superhuang.data.scalpel.admin.dsl.specification.SpecSearchCriteria;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DslParser {

    private static Map<String, Operator> ops;

    private static Pattern SpecCriteraRegex = Pattern.compile("^([[\\w][.]]+?)(" + Arrays.stream(SearchOperation.SIMPLE_OPERATION_SET).collect(Collectors.joining("|")) + ")(\\p{Punct}?)([[^\\p{Punct}][/_-]]+)(\\p{Punct}?)$");

    private enum Operator {
        OR(1), AND(2);
        final int precedence;

        Operator(int p) {
            precedence = p;
        }
    }

    static {
        Map<String, Operator> tempMap = new HashMap<>();
        tempMap.put("AND", Operator.AND);
        tempMap.put("OR", Operator.OR);
        tempMap.put("or", Operator.OR);
        tempMap.put("and", Operator.AND);

        ops = Collections.unmodifiableMap(tempMap);
    }

    private static boolean isHigerPrecedenceOperator(String currOp, String prevOp) {
        return (ops.containsKey(prevOp) && ops.get(prevOp).precedence >= ops.get(currOp).precedence);
    }

    public Deque<?> parse(String searchParam) {
        Deque<Object> output = new LinkedList<>();
        Deque<String> stack = new LinkedList<>();

        Arrays.stream(searchParam.split("\\s+")).forEach(token -> {
            if (ops.containsKey(token)) {
                while (!stack.isEmpty() && isHigerPrecedenceOperator(token, stack.peek()))
                    output.push(stack.pop()
                            .equalsIgnoreCase(SearchOperation.OR_OPERATOR) ? SearchOperation.OR_OPERATOR : SearchOperation.AND_OPERATOR);
                stack.push(token.equalsIgnoreCase(SearchOperation.OR_OPERATOR) ? SearchOperation.OR_OPERATOR : SearchOperation.AND_OPERATOR);
            } else if (token.equals(SearchOperation.LEFT_PARANTHESIS)) {
                stack.push(SearchOperation.LEFT_PARANTHESIS);
            } else if (token.equals(SearchOperation.RIGHT_PARANTHESIS)) {
                while (!stack.peek()
                        .equals(SearchOperation.LEFT_PARANTHESIS))
                    output.push(stack.pop());
                stack.pop();
            } else {
                Matcher matcher = SpecCriteraRegex.matcher(token);
                while (matcher.find()) {
                    String fieldName = matcher.group(1);
                    String fieldValue = matcher.group(4);
                    Object finalFieldValue = fieldValue;
                    output.push(new SpecSearchCriteria(fieldName, matcher.group(2), matcher.group(3), finalFieldValue, matcher.group(5)));
                }
            }
        });

        while (!stack.isEmpty())
            output.push(stack.pop());

        return output;
    }

}