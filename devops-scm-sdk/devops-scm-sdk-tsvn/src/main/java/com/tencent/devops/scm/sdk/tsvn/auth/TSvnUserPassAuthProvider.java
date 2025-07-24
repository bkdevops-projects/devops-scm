package com.tencent.devops.scm.sdk.tsvn.auth;

import com.tencent.devops.scm.sdk.common.ScmRequest;
import com.tencent.devops.scm.sdk.tsvn.TSvnApi;
import com.tencent.devops.scm.sdk.tsvn.TSvnSessionApi;
import com.tencent.devops.scm.sdk.tsvn.TSvnConstants;
import com.tencent.devops.scm.sdk.tsvn.pojo.TSvnSession;

/**
 * 通过用户名密码授权
 */
public class TSvnUserPassAuthProvider implements TSvnAuthProvider {

    private final String username;
    private final String password;
    //
    private TSvnApi tSvnApi;

    public TSvnUserPassAuthProvider(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void bind(TSvnApi tSvnApi) {
        this.tSvnApi = tSvnApi;
    }

    @Override
    public void authorization(ScmRequest.Builder<?> builder) {
        TSvnSessionApi sessionApi = new TSvnSessionApi(tSvnApi);
        TSvnSession session = sessionApi.getSession(username, password);
        builder.setHeader(TSvnConstants.PRIVATE_TOKEN_HEADER, session.getPrivateToken());
    }
}
