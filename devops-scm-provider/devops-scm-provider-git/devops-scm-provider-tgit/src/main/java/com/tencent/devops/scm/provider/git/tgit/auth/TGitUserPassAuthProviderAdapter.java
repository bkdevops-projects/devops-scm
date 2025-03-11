package com.tencent.devops.scm.provider.git.tgit.auth;

import com.tencent.devops.scm.api.pojo.auth.IScmAuth;
import com.tencent.devops.scm.api.pojo.auth.UserPassScmAuth;
import com.tencent.devops.scm.sdk.tgit.auth.TGitAuthProvider;
import com.tencent.devops.scm.sdk.tgit.auth.TGitUserPassAuthProvider;

public class TGitUserPassAuthProviderAdapter implements TGitAuthProviderAdapter {

    @Override
    public boolean support(IScmAuth auth) {
        return auth instanceof UserPassScmAuth;
    }

    @Override
    public TGitAuthProvider get(IScmAuth auth) {
        UserPassScmAuth userPassGitAuth = (UserPassScmAuth) auth;
        return new TGitUserPassAuthProvider(userPassGitAuth.getUsername(), userPassGitAuth.getPassword());
    }
}
