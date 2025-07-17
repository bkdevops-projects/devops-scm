package com.tencent.devops.scm.provider.gitee.simple

import com.gitee.sdk.gitee5j.ApiClient
import com.tencent.devops.scm.api.exception.ScmApiException
import com.tencent.devops.scm.api.pojo.repository.ScmProviderRepository
import com.tencent.devops.scm.api.pojo.repository.git.GitScmProviderRepository
import com.tencent.devops.scm.provider.gitee.simple.utils.GiteeRepoInfoUtils
import java.util.function.BiFunction

/**
 * Gitee Api请求模板类
 */
object GiteeApiTemplate {

    fun <R> execute(
        repository: ScmProviderRepository,
        apiFactory: GiteeApiClientFactory,
        apiFunction: BiFunction<Pair<String, String>, ApiClient, R>
    ): R {
        try {
            val gitScmProviderRepository = repository as GitScmProviderRepository
            // 仓库全名称(owner/repo)
            val repoFullName = GiteeRepoInfoUtils.convertRepoName(
                gitScmProviderRepository.projectIdOrPath
            )
            return apiFunction.apply(repoFullName, apiFactory.getClient(gitScmProviderRepository))
        } catch (t: Throwable) {
            throw ScmApiException(t)
        }
    }
}
