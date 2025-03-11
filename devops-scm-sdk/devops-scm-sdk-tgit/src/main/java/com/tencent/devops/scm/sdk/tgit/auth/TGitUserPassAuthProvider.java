package com.tencent.devops.scm.sdk.tgit.auth;

import com.tencent.devops.scm.sdk.common.ScmRequest;
import com.tencent.devops.scm.sdk.tgit.TGitSessionApi;
import com.tencent.devops.scm.sdk.tgit.TGitApi;
import com.tencent.devops.scm.sdk.tgit.TGitConstants;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitSession;

/**
 * 通过用户名密码授权
 */
public class TGitUserPassAuthProvider implements TGitAuthProvider {

    private final String username;
    private final String password;
    //
    private TGitApi tGitApi;

    public TGitUserPassAuthProvider(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void bind(TGitApi tGitApi) {
        this.tGitApi = tGitApi;
    }

    @Override
    public void authorization(ScmRequest.Builder<?> builder) {
        TGitSessionApi sessionApi = new TGitSessionApi(tGitApi);
        TGitSession session = sessionApi.getSession(username, password);
        builder.setHeader(TGitConstants.PRIVATE_TOKEN_HEADER, session.getPrivateToken());
    }
}
