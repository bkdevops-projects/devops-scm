package com.tencent.devops.scm.sdk.common.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

import java.lang.reflect.Type;

@Getter
public class ScmSdkJsonFactory {

    private final ObjectMapper objectMapper;

    public ScmSdkJsonFactory() {
        this.objectMapper = new ObjectMapper();

        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // 设置输入时忽略在JSON字符串中存在但Java对象实际没有的属性
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
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
