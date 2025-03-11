package com.tencent.devops.scm.sdk.tgit;

import com.tencent.devops.scm.sdk.common.ScmHttpMethod;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitUser;

/**
 * 用户 接口
 * <pre>接口文档: <a href="https://code.tencent.com/help/api/user">工蜂用户接口文档</a></pre>
 */
public class TGitUserApi extends AbstractTGitApi {
    public static final String USER_URI_PATTERN = "user";

    public TGitUserApi(TGitApi tGitApi) {
        super(tGitApi);
    }

    public TGitUser getCurrentUser() {
        return tGitApi.createRequest()
                .method(ScmHttpMethod.GET)
                .withUrlPath(USER_URI_PATTERN)
                .fetch(TGitUser.class);
    }
}
