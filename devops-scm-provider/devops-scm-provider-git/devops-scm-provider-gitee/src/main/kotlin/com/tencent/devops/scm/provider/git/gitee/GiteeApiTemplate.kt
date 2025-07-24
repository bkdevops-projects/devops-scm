package com.tencent.devops.scm.provider.git.gitee

import com.tencent.devops.scm.api.pojo.auth.IScmAuth
import com.tencent.devops.scm.api.pojo.repository.git.GitScmProviderRepository
import com.tencent.devops.scm.provider.common.GitScmApiTemplate
import com.tencent.devops.scm.provider.common.ScmApiTemplate
import com.tencent.devops.scm.provider.git.gitee.auth.GiteeTokenAuthProviderAdapter
import com.tencent.devops.scm.sdk.gitee.GiteeApi
import com.tencent.devops.scm.sdk.gitee.GiteeApiException
import com.tencent.devops.scm.sdk.gitee.GiteeApiFactory

/**
 * Gitee API请求模板工具类
 */
object GiteeApiTemplate : GitScmApiTemplate<GiteeApi, GiteeApiFactory, GiteeApiException>() {
    override fun getApi(repository: GitScmProviderRepository, apiFactory: GiteeApiFactory): GiteeApi =
        apiFactory.fromAuthProvider(GiteeTokenAuthProviderAdapter.get(repository.auth!!))

    override fun getApi(auth: IScmAuth, apiFactory: GiteeApiFactory): GiteeApi =
        apiFactory.fromAuthProvider(GiteeTokenAuthProviderAdapter.get(auth))
}
