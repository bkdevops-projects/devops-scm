package com.tencent.devops.scm.sdk.gitee;

import com.tencent.devops.scm.sdk.common.ScmHttpMethod;
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeUserInfo;

public class GiteeUserApi extends AbstractGiteeApi {
    private static final String USER_URI_PATTERN = "user";

    public GiteeUserApi(GiteeApi tGitApi) {
        super(tGitApi);
    }

    /**
     * 获取当前用户信息
     */
    public GiteeUserInfo getCurrentUser() {
        return giteeApi.createRequest()
                .method(ScmHttpMethod.GET)
                .withUrlPath(USER_URI_PATTERN)
                .fetch(GiteeUserInfo.class);
    }
}
