package com.tencent.devops.scm.sdk.common.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.tencent.devops.scm.sdk.common.exception.ScmSdkException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ScmJsonUtil {
    // 默认json工厂
    private static ScmSdkJsonFactory JSON_FACTORY = null;

    // json工厂缓存
    public static final Map<String, ScmSdkJsonFactory> JSON_FACTORIES = new HashMap<>();

    static {
        JSON_FACTORY = new ScmSdkJsonFactory();
        JSON_FACTORY.getObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    }

    public static ScmSdkJsonFactory getJsonFactory(String factoryName) {
        return Optional.ofNullable(JSON_FACTORIES.get(factoryName)).orElse(JSON_FACTORY);
    }

    public static ScmSdkJsonFactory getJsonFactory() {
        return JSON_FACTORY;
    }

    public static <T> T fromJson(String factoryName, String jsonStr, Class<T> clazz) {
        try {
            ScmSdkJsonFactory jsonFactory = getJsonFactory(factoryName);
            return jsonFactory.fromJson(jsonStr, clazz);
        } catch (Exception exception) {
            throw new ScmSdkException(exception);
        }
    }

    public static <T> T fromJson(String factoryName, String jsonStr, TypeReference<T> typeReference) {
        try {
            return getJsonFactory(factoryName).fromJson(jsonStr, typeReference);
        } catch (Exception exception) {
            throw new ScmSdkException(exception);
        }
    }

    public static <T> T fromJson(String jsonStr, Class<T> clazz) {
        try {
            return JSON_FACTORY.fromJson(jsonStr, clazz);
        } catch (Exception exception) {
            throw new ScmSdkException(exception);
        }
    }

    public static <T> T fromJson(String jsonStr, TypeReference<T> typeReference) {
        try {
            return JSON_FACTORY.fromJson(jsonStr, typeReference);
        } catch (Exception exception) {
            throw new ScmSdkException(exception);
        }
    }

    public static String toJson(Object object) {
        try {
            return JSON_FACTORY.toJson(object);
        } catch (Exception exception) {
            throw new ScmSdkException(exception);
        }
    }

    public static String toJson(String factoryName, Object object) {
        try {
            return getJsonFactory(factoryName).toJson(object);
        } catch (Exception exception) {
            throw new ScmSdkException(exception);
        }
    }
}
