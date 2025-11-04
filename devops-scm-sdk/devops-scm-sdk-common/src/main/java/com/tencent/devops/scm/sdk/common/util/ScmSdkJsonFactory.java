package com.tencent.devops.scm.sdk.common.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

import java.lang.reflect.Type;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
public class ScmSdkJsonFactory {

    private final ObjectMapper objectMapper;
    private static final Logger logger = LoggerFactory.getLogger(ScmSdkJsonFactory.class);

    public ScmSdkJsonFactory() {
        this.objectMapper = new ObjectMapper();

        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // 设置输入时忽略在JSON字符串中存在但Java对象实际没有的属性
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        // 创建自定义日期反序列化器
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Date.class, new CustomDateDeserializer());
        objectMapper.registerModule(module);
    }

    // 自定义日期反序列化器，支持多种格式
    static class CustomDateDeserializer extends JsonDeserializer<Date> {
        // 定义支持的日期格式列表
        private static final Map<String, DateTimeFormatter> DATE_FORMATS = new HashMap<>();
        // 支持的日期格式
        private static final String[] DATE_FORMATS_PATTERNS = new String[]{
                "yyyy-MM-dd",
                "yyyy-MM-dd'T'HH:mm:ss", // 带T和0位毫秒
                "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS", // 带T和9位毫秒
                "yyyy-MM-dd HH:mm:ss",
                "yyyy-MM-dd'T'HH:mm:ssZ",
                "yyyy-MM-dd'T'HH:mm:ssXXX"
        };

        private static final String TIME_PATTERN = "HH:mm:ss";


        static {
            Arrays.stream(DATE_FORMATS_PATTERNS).forEach(pattern -> {
                        DATE_FORMATS.put(
                                pattern,
                                DateTimeFormatter.ofPattern(pattern)
                        );
                    }
            );
        }

        @Override
        public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String dateStr = p.getText();
            if (StringUtils.isBlank(dateStr) || StringUtils.equals("null", dateStr)) {
                return null;
            }
            for (Map.Entry<String, DateTimeFormatter> entry : DATE_FORMATS.entrySet()) {
                String pattern = entry.getKey();
                DateTimeFormatter formatter = entry.getValue();
                try {
                    if (pattern.contains(TIME_PATTERN)) {
                        // 1. 解析字符串为 LocalDateTime（无时区信息）
                        LocalDateTime localDateTime = LocalDateTime.parse(dateStr, formatter);

                        // 2. 绑定中国时区（Asia/Shanghai，UTC+8）
                        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());

                        // 3. 转换为 Date 对象（Date 基于 UTC 时间戳，通过 Instant 转换）
                        return Date.from(zonedDateTime.toInstant());
                    } else {
                        // 只有日期的格式，使用 LocalDate
                        java.time.LocalDate localDate = java.time.LocalDate.parse(dateStr, formatter);
                        ZonedDateTime zonedDateTime = localDate.atStartOfDay(ZoneId.systemDefault());
                        return Date.from(zonedDateTime.toInstant());
                    }
                } catch (Exception e) {
                    continue;
                }
            }

            // 所有格式都解析失败则抛出异常
            throw new UnsupportedOperationException(
                    String.format(
                            "Date string [%s] does not match any supported formats[%s]",
                            dateStr,
                            DATE_FORMATS_PATTERNS
                    )
            );
        }
    }

    public <T> T fromJson(String jsonStr, Class<T> clazz) throws JsonProcessingException {
        return objectMapper.readValue(jsonStr, clazz);
    }

    public <T> T fromJson(String jsonStr, TypeReference<T> typeReference) throws JsonProcessingException {
        return objectMapper.readValue(jsonStr, typeReference);
    }

    public <T> T fromJson(String jsonStr, Type type) throws JsonProcessingException {
        JavaType javaType = objectMapper.getTypeFactory().constructType(type);
        return objectMapper.readValue(jsonStr, javaType);
    }

    public String toJson(Object obj) throws JsonProcessingException {
        return objectMapper.writeValueAsString(obj);
    }

    public JsonNode toJsonNode(String jsonStr) throws JsonProcessingException {
        return objectMapper.readTree(jsonStr);
    }

    public byte[] writeValueAsBytes(Object value) throws JsonProcessingException {
        return objectMapper.writeValueAsBytes(value);
    }
}
