package com.tencent.devops.scm.sdk.bkcode;

import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeUser;
import com.tencent.devops.scm.sdk.common.ScmHttpMethod;

public class BkCodeUserApi extends AbstractBkCodeApi {
    private static final String USER_URI_PATTERN = "user";

    public BkCodeUserApi(BkCodeApi tGitApi) {
        super(tGitApi);
    }

    /**
     * 获取当前用户信息
     */
    public BkCodeUser getCurrentUser() {
        return bkCodeApi.createRequest()
                .method(ScmHttpMethod.GET)
                .withUrlPath(USER_URI_PATTERN)
                .fetch(BkCodeUser.class);
    }
}
