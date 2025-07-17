package com.tencent.devops.scm.provider.svn.tsvn

import com.tencent.devops.scm.api.pojo.auth.IScmAuth
import com.tencent.devops.scm.api.pojo.repository.svn.SvnScmProviderRepository
import com.tencent.devops.scm.provider.common.SvnScmApiTemplate
import com.tencent.devops.scm.provider.svn.tsvn.auth.TSvnAuthProviderAdapter
import com.tencent.devops.scm.sdk.tsvn.TSvnApi
import com.tencent.devops.scm.sdk.tsvn.TSvnApiException
import com.tencent.devops.scm.sdk.tsvn.TSvnApiFactory

object TSvnApiTemplate: SvnScmApiTemplate<TSvnApi, TSvnApiFactory, TSvnApiException>() {
    override fun getApi(repository: SvnScmProviderRepository, apiFactory: TSvnApiFactory): TSvnApi =
        apiFactory.fromAuthProvider(TSvnAuthProviderAdapter.get(repository.auth!!))

    override fun getApi(auth: IScmAuth, apiFactory: TSvnApiFactory): TSvnApi =
        apiFactory.fromAuthProvider(TSvnAuthProviderAdapter.get(auth))
}