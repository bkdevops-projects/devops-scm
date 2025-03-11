package com.tencent.devops.scm.provider.git.tgit;

import com.tencent.devops.scm.api.UserService;
import com.tencent.devops.scm.api.pojo.User;
import com.tencent.devops.scm.api.pojo.auth.IScmAuth;
import com.tencent.devops.scm.provider.git.tgit.auth.TGitAuthProviderFactory;
import com.tencent.devops.scm.sdk.tgit.TGitApi;
import com.tencent.devops.scm.sdk.tgit.TGitApiFactory;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitUser;

public class TGitUserService implements UserService {

    private final TGitApiFactory apiFactory;

    public TGitUserService(TGitApiFactory apiFactory) {
        this.apiFactory = apiFactory;
    }

    @Override
    public User find(IScmAuth auth) {
        TGitApi tGitApi = apiFactory.fromAuthProvider(TGitAuthProviderFactory.create(auth));
        TGitUser tGitUser = tGitApi.getUserApi().getCurrentUser();
        return TGitObjectConverter.convertUser(tGitUser);
    }
}
