package cn.superhuang.data.scalpel.dsl.specification;

import cn.hutool.core.util.ReflectUtil;
import cn.superhuang.data.scalpel.exception.BaseException;
import jakarta.persistence.Embedded;

import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CriteriaParser<T> {

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

    public Deque<?> parse(String searchParam, Class<T> t) {
        //START 反射机制获取entity对象的field信息，为了适配枚举
        Map<String, Field> classFieldMap = Arrays.stream(ReflectUtil.getFields(t)).filter(f -> !f.getName().contains("hibernate")).collect(Collectors.toMap(Field::getName, Field -> Field));
        Map<String, Field> subClassFieldMap = classFieldMap.values().stream()
                .filter(f -> f.isAnnotationPresent(Embedded.class))
                .map(f -> Arrays.stream(ReflectUtil.getFields(f.getType())).filter(subField -> !subField.getName().contains("hibernate")).collect(Collectors.toMap(subField -> f.getName() + "." + subField.getName(), subField -> subField)))
                .flatMap(map -> map.entrySet().stream())
                .collect(HashMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), HashMap::putAll);
        classFieldMap.putAll(subClassFieldMap);
        //END 反射机制获取entity对象的field信息，为了适配枚举

        for (Field field : classFieldMap.values()) {
            if (field.isAnnotationPresent(Embedded.class)) {
                classFieldMap.putAll(subClassFieldMap);
            }
        }


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
                    if (!classFieldMap.containsKey(fieldName)) {
                        throw new BaseException("不存在属性:" + fieldName);
                    }
                    Field field = classFieldMap.get(fieldName);
                    if (field.getType().isEnum()) {
                        finalFieldValue = Enum.valueOf((Class<? extends Enum>) field.getType(), fieldValue);
                    }

                    output.push(new SpecSearchCriteria(fieldName, matcher.group(2), matcher.group(3), finalFieldValue, matcher.group(5)));
                }
            }
        });

        while (!stack.isEmpty())
            output.push(stack.pop());

        return output;
    }

}