package com.tencent.devops.scm.provider.common

import com.tencent.devops.scm.api.pojo.repository.ScmProviderRepository
import com.tencent.devops.scm.api.pojo.repository.git.GitScmProviderRepository
import com.tencent.devops.scm.api.pojo.repository.svn.SvnScmProviderRepository
import com.tencent.devops.scm.sdk.common.exception.BaseScmApiException

/**
 * SVN API请求模板接口
 * 统一创建API对象并捕获异常
 */
abstract class SvnScmApiTemplate<T, F, E : BaseScmApiException> : ScmApiTemplate<T, F, SvnScmProviderRepository, E> {
    @JvmName("executeSvn")
    fun <R> execute(
        repository: ScmProviderRepository,
        apiFactory: F,
        apiFunction: (SvnScmProviderRepository, T) -> R
    ) = execute(repository as SvnScmProviderRepository, apiFactory, apiFunction)
}
