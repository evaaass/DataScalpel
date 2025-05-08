package cn.superhuang.data.scalpel.apiserver.config;

import cn.hutool.core.io.IoUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import dm.jdbc.driver.DmdbNClob;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.io.IOException;
import java.sql.SQLException;

/**
 * jackson全局配置 LocalDateTime序列化
 */
@Configuration
public class JacksonCustomizerConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        SimpleModule module = new SimpleModule();
        module.addSerializer(DmdbNClob.class, new DmdbNClobSerializer());
        objectMapper.registerModule(module);
        return objectMapper;
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer customJackson() {
        return builder -> {
            SimpleModule module = new SimpleModule();
            module.addSerializer(DmdbNClob.class, new DmdbNClobSerializer());
            builder.modules(module);
        };
    }

    public static class DmdbNClobSerializer extends JsonSerializer<DmdbNClob> {

        @Override
        public void serialize(DmdbNClob dmdbNClob, JsonGenerator jsonGenerator,
                              SerializerProvider serializerProvider) throws IOException {
            try {
                jsonGenerator.writeString(IoUtil.read(dmdbNClob.do_getCharacterStream(), true));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
