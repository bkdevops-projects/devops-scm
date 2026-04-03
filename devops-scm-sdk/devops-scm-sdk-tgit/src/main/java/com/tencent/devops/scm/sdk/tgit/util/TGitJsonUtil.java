package com.tencent.devops.scm.sdk.tgit.util;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.tencent.devops.scm.sdk.common.util.ScmJsonUtil;
import com.tencent.devops.scm.sdk.common.util.ScmSdkJsonFactory;
import java.time.ZoneId;

public class TGitJsonUtil extends ScmJsonUtil {

    private static final String FACTORY_NAME = TGitJsonUtil.class.getName();

    static {
        ScmSdkJsonFactory jsonFactory = new ScmSdkJsonFactory(ZoneId.of("UTC"));
        jsonFactory.getObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        JSON_FACTORIES.put(FACTORY_NAME, jsonFactory);
    }

    public static ScmSdkJsonFactory getJsonFactory() {
        return JSON_FACTORIES.get(FACTORY_NAME);
    }

    public static <T> T fromJson(String jsonStr, Class<T> clazz) {
        return ScmJsonUtil.fromJson(FACTORY_NAME, jsonStr, clazz);
    }
}
