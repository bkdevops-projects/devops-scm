package com.tencent.devops.scm.provider.git.gitlab

import com.tencent.devops.scm.api.pojo.auth.IScmAuth
import com.tencent.devops.scm.api.pojo.repository.git.GitScmProviderRepository
import com.tencent.devops.scm.provider.common.GitScmApiTemplate
import com.tencent.devops.scm.provider.git.gitlab.auth.GitlabAuthProviderFactory
import com.tencent.devops.scm.sdk.gitlab.GitlabApi
import com.tencent.devops.scm.sdk.gitlab.GitlabApiException
import com.tencent.devops.scm.sdk.gitlab.GitlabApiFactory

object GitlabApiTemplate : GitScmApiTemplate<GitlabApi, GitlabApiFactory, GitlabApiException>() {
    override fun getApi(repository: GitScmProviderRepository, apiFactory: GitlabApiFactory): GitlabApi {
        val auth = requireNotNull(repository.auth) { "GitLab repository auth cannot be null" }
        require(repository.projectIdOrPath.toString().isNotBlank()) { "GitLab project id or path cannot be blank" }
        return apiFactory.fromAuthProvider(GitlabAuthProviderFactory.create(auth))
    }

    override fun getApi(auth: IScmAuth, apiFactory: GitlabApiFactory): GitlabApi =
        apiFactory.fromAuthProvider(GitlabAuthProviderFactory.create(auth))
}
