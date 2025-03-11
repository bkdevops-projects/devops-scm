package com.tencent.devops.scm.provider.git.tgit.auth;

import com.tencent.devops.scm.api.pojo.auth.IScmAuth;
import com.tencent.devops.scm.sdk.tgit.auth.TGitAuthProvider;

/**
 * tgit 授权提供者适配器
 */
public interface TGitAuthProviderAdapter {

    boolean support(IScmAuth auth);

    TGitAuthProvider get(IScmAuth auth);
}
