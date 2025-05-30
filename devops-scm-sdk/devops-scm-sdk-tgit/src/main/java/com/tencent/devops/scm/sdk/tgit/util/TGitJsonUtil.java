package com.tencent.devops.scm.sdk.tgit.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.tencent.devops.scm.sdk.common.util.ScmSdkJsonFactory;
import com.tencent.devops.scm.sdk.tgit.TGitApiException;

public class TGitJsonUtil {

    private static final ScmSdkJsonFactory JSON_FACTORY;

    static {
        JSON_FACTORY = new ScmSdkJsonFactory();
        JSON_FACTORY.getObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    }

    public static ScmSdkJsonFactory getJsonFactory() {
        return JSON_FACTORY;
    }

    public static <T> T fromJson(String jsonStr, Class<T> clazz) {
        try {
            return JSON_FACTORY.fromJson(jsonStr, clazz);
        } catch (Exception exception) {
            throw new TGitApiException(exception);
        }
    }

    public static <T> T fromJson(String jsonStr, TypeReference<T> typeReference) {
        try {
            return JSON_FACTORY.fromJson(jsonStr, typeReference);
        } catch (Exception exception) {
            throw new TGitApiException(exception);
        }
    }
}
