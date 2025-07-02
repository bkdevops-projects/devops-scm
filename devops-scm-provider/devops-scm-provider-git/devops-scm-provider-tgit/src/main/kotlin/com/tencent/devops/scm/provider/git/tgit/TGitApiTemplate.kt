package com.tencent.devops.scm.provider.git.tgit

import com.tencent.devops.scm.api.pojo.auth.IScmAuth
import com.tencent.devops.scm.api.pojo.repository.git.GitScmProviderRepository
import com.tencent.devops.scm.provider.common.GitScmApiTemplate
import com.tencent.devops.scm.provider.common.ScmApiTemplate
import com.tencent.devops.scm.provider.git.tgit.auth.TGitAuthProviderFactory
import com.tencent.devops.scm.sdk.tgit.TGitApi
import com.tencent.devops.scm.sdk.tgit.TGitApiException
import com.tencent.devops.scm.sdk.tgit.TGitApiFactory

/**
 * TGit API请求模板工具类
 */
object TGitApiTemplate : GitScmApiTemplate<TGitApi, TGitApiFactory, TGitApiException>() {
    override fun getApi(repository: GitScmProviderRepository, apiFactory: TGitApiFactory): TGitApi =
        apiFactory.fromAuthProvider(TGitAuthProviderFactory.create(repository.auth!!))

    override fun getApi(auth: IScmAuth, apiFactory: TGitApiFactory): TGitApi =
        apiFactory.fromAuthProvider(TGitAuthProviderFactory.create(auth))
}
