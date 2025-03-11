package com.tencent.devops.scm.sdk.tgit;

import com.tencent.devops.scm.sdk.common.ScmHttpMethod;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitSession;

public class TGitSessionApi extends AbstractTGitApi {
    private static final String SESSION_URI_PATTERN = "session";

    public TGitSessionApi(TGitApi tGitApi) {
        super(tGitApi);
    }

    public TGitSession getSession(String login, String password) {
        return tGitApi.createRequest()
                .method(ScmHttpMethod.POST)
                .withUrlPath(SESSION_URI_PATTERN)
                .with("login", login)
                .with("password", password)
                .fetch(TGitSession.class);
    }
}
