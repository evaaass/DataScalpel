package cn.superhuang.data.scalpel.admin.repository.converter;

import cn.superhuang.data.scalpel.admin.util.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.AttributeConverter;

import java.util.HashMap;
import java.util.Map;


public class MapConverter implements AttributeConverter<Map<String, String>, String> {
    @Override
    public String convertToDatabaseColumn(Map<String, String> stringStringMap) {
        try {
            return JsonUtil.objectMapper.writeValueAsString(stringStringMap);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<String, String> convertToEntityAttribute(String s) {
        try {
            if (s == null || s.isEmpty()) {
                return new HashMap<>();
            }
            return JsonUtil.objectMapper.readValue(s, new TypeReference<Map<String, String>>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
