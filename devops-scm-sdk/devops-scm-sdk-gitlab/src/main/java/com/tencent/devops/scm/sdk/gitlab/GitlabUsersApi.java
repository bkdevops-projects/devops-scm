package com.tencent.devops.scm.sdk.gitlab;

import com.tencent.devops.scm.sdk.common.ScmHttpMethod;
import com.tencent.devops.scm.sdk.gitlab.pojo.GitlabUser;

public class GitlabUsersApi extends AbstractGitlabApi {
    GitlabUsersApi(GitlabApi api) {
        super(api);
    }

    public GitlabUser getCurrentUser() {
        return gitlabApi.createRequest().method(ScmHttpMethod.GET).withUrlPath("user").fetch(GitlabUser.class);
    }
}
