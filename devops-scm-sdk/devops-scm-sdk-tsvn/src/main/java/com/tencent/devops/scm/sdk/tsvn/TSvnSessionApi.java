package com.tencent.devops.scm.sdk.tsvn;

import com.tencent.devops.scm.sdk.common.ScmHttpMethod;
import com.tencent.devops.scm.sdk.tsvn.pojo.TSvnSession;

public class TSvnSessionApi extends AbstractTSvnApi {
    private static final String SESSION_URI_PATTERN = "session";

    public TSvnSessionApi(TSvnApi tSvnApi) {
        super(tSvnApi);
    }

    public TSvnSession getSession(String login, String password) {
        return tSvnApi.createRequest()
                .method(ScmHttpMethod.POST)
                .withUrlPath(SESSION_URI_PATTERN)
                .with("login", login)
                .with("password", password)
                .fetch(TSvnSession.class);
    }
}
