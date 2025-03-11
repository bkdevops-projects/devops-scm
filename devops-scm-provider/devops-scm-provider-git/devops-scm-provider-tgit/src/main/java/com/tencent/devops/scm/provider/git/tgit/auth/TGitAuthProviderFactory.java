package com.tencent.devops.scm.provider.git.tgit.auth;

import com.tencent.devops.scm.api.pojo.auth.IScmAuth;
import com.tencent.devops.scm.sdk.tgit.auth.TGitAuthProvider;
import java.util.Arrays;
import java.util.List;

/**
 * tgit 授权提供者创建者
 */
public class TGitAuthProviderFactory {

    private static final List<TGitAuthProviderAdapter> adapters = Arrays.asList(
            new TGitUserPassAuthProviderAdapter(),
            new TGitTokenAuthProviderAdapter()
    );

    public static TGitAuthProvider create(IScmAuth gitAuth) {
        for (TGitAuthProviderAdapter adapter : adapters) {
            if (adapter.support(gitAuth)) {
                return adapter.get(gitAuth);
            }
        }
        throw new UnsupportedOperationException(String.format("gitAuth(%s) is not support", gitAuth));
    }
}
