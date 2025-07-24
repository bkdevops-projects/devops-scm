package com.tencent.devops.scm.sdk.tgit.util;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.tencent.devops.scm.sdk.common.util.ScmJsonUtil;
import com.tencent.devops.scm.sdk.common.util.ScmSdkJsonFactory;

public class TGitJsonUtil extends ScmJsonUtil {

    private static final ScmSdkJsonFactory JSON_FACTORY;

    static {
        JSON_FACTORY = new ScmSdkJsonFactory();
        JSON_FACTORY.getObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    }

    public static ScmSdkJsonFactory getJsonFactory() {
        return JSON_FACTORY;
    }
}
