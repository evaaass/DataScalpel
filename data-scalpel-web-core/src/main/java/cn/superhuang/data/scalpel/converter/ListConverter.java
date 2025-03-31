package cn.superhuang.data.scalpel.converter;

import cn.superhuang.data.scalpel.admin.util.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.AttributeConverter;

import java.util.List;
import java.util.Map;


public class ListConverter implements AttributeConverter<List<String>, String> {
    @Override
    public String convertToDatabaseColumn(List<String> stringStringMap) {
        try {
            return JsonUtil.objectMapper.writeValueAsString(stringStringMap);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> convertToEntityAttribute(String s) {
        try {
            return JsonUtil.objectMapper.readValue(s, new TypeReference<List<String>>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
