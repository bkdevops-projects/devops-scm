package com.tencent.devops.scm.provider.common

import com.tencent.devops.scm.api.pojo.repository.ScmProviderRepository
import com.tencent.devops.scm.api.pojo.repository.git.GitScmProviderRepository
import com.tencent.devops.scm.sdk.common.exception.BaseScmApiException

/**
 * GIT API请求模板接口
 * 统一创建API对象并捕获异常
 */
abstract class GitScmApiTemplate<T, F, E : BaseScmApiException> : ScmApiTemplate<T, F, GitScmProviderRepository, E> {
    @JvmName("executeGit")
    fun <R> execute(
        repository: ScmProviderRepository,
        apiFactory: F,
        apiFunction: (GitScmProviderRepository, T) -> R
    ) = execute(repository as GitScmProviderRepository, apiFactory, apiFunction)
}
