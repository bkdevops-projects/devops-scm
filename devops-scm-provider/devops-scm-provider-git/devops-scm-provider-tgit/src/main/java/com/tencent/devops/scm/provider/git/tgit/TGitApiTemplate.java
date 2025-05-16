package com.tencent.devops.scm.provider.git.tgit;

import com.tencent.devops.scm.api.exception.NotFoundScmApiException;
import com.tencent.devops.scm.api.exception.ScmApiException;
import com.tencent.devops.scm.api.pojo.auth.IScmAuth;
import com.tencent.devops.scm.api.pojo.repository.ScmProviderRepository;
import com.tencent.devops.scm.api.pojo.repository.git.GitScmProviderRepository;
import com.tencent.devops.scm.provider.git.tgit.auth.TGitAuthProviderFactory;
import com.tencent.devops.scm.sdk.tgit.TGitApi;
import com.tencent.devops.scm.sdk.tgit.TGitApiException;
import com.tencent.devops.scm.sdk.tgit.TGitApiFactory;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * TGit Api请求模板类
 */
public class TGitApiTemplate {

    public static <R> R execute(ScmProviderRepository repository, TGitApiFactory apiFactory,
            BiFunction<GitScmProviderRepository, TGitApi, R> apiFunction) {
        try {
            GitScmProviderRepository repo = (GitScmProviderRepository) repository;
            TGitApi tGitApi = apiFactory.fromAuthProvider(TGitAuthProviderFactory.create(repo.getAuth()));
            return apiFunction.apply(repo, tGitApi);
        } catch (Throwable t) {
            throw handleThrowable(t);
        }
    }

    public static <R> R execute(IScmAuth auth, TGitApiFactory apiFactory, Function<TGitApi, R> apiFunction) {
        try {
            TGitApi tGitApi = apiFactory.fromAuthProvider(TGitAuthProviderFactory.create(auth));
            return apiFunction.apply(tGitApi);
        } catch (Throwable t) {
            throw handleThrowable(t);
        }
    }

    public static void execute(ScmProviderRepository repository, TGitApiFactory apiFactory,
            BiConsumer<GitScmProviderRepository, TGitApi> apiConsumer) {
        try {
            GitScmProviderRepository repo = (GitScmProviderRepository) repository;
            TGitApi tGitApi = apiFactory.fromAuthProvider(TGitAuthProviderFactory.create(repo.getAuth()));
            apiConsumer.accept(repo, tGitApi);
        } catch (Throwable t) {
            throw handleThrowable(t);
        }
    }

    private static ScmApiException translateException(TGitApiException e) {
        switch (e.getStatusCode()) {
            case 404:
                return new NotFoundScmApiException(e.getMessage());
            default:
                return new ScmApiException(e.getMessage(), e.getStatusCode());

        }
    }

    private static ScmApiException handleThrowable(Throwable t) {
        if (t instanceof TGitApiException) {
            return translateException((TGitApiException) t);
        } else {
            return new ScmApiException(t);
        }
    }
}
