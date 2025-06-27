package com.tencent.devops.scm.provider.gitee.simple;

import com.gitee.sdk.gitee5j.ApiClient;
import com.tencent.devops.scm.api.exception.ScmApiException;
import com.tencent.devops.scm.api.pojo.repository.ScmProviderRepository;
import com.tencent.devops.scm.api.pojo.repository.git.GitScmProviderRepository;
import com.tencent.devops.scm.provider.gitee.simple.utils.GiteeRepoInfoUtils;
import java.util.function.BiFunction;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Gitee Api请求模板类
 */
public class GiteeApiTemplate {

    public static <R> R execute(
            ScmProviderRepository repository,
            GiteeApiClientFactory apiFactory,
            BiFunction<Pair<String, String>, ApiClient, R> apiFunction
    ) {
        try {
            GitScmProviderRepository gitScmProviderRepository = (GitScmProviderRepository) repository;
            // 仓库全名称(owner/repo)
            Pair<String, String> repoFullName = GiteeRepoInfoUtils.convertRepoName(
                    gitScmProviderRepository.getProjectIdOrPath()
            );
            return apiFunction.apply(repoFullName, apiFactory.getClient(gitScmProviderRepository));
        } catch (Throwable t) {
            throw new ScmApiException(t);
        }
    }
}
