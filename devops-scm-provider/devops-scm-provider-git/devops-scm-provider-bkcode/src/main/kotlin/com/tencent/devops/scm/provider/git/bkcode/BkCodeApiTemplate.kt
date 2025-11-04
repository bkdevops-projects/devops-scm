package com.tencent.devops.scm.provider.git.bkcode

import com.tencent.devops.scm.api.pojo.auth.IScmAuth
import com.tencent.devops.scm.api.pojo.repository.git.GitScmProviderRepository
import com.tencent.devops.scm.provider.common.GitScmApiTemplate
import com.tencent.devops.scm.provider.git.bkcode.auth.BkCodeTokenAuthProviderAdapter
import com.tencent.devops.scm.sdk.bkcode.BkCodeApi
import com.tencent.devops.scm.sdk.bkcode.BkCodeApiException
import com.tencent.devops.scm.sdk.bkcode.BkCodeApiFactory

/**
 * BkCode API请求模板工具类
 */
object BkCodeApiTemplate : GitScmApiTemplate<BkCodeApi, BkCodeApiFactory, BkCodeApiException>() {
    override fun getApi(repository: GitScmProviderRepository, apiFactory: BkCodeApiFactory): BkCodeApi =
        apiFactory.fromAuthProvider(BkCodeTokenAuthProviderAdapter.get(repository.auth!!))

    override fun getApi(auth: IScmAuth, apiFactory: BkCodeApiFactory): BkCodeApi =
        apiFactory.fromAuthProvider(BkCodeTokenAuthProviderAdapter.get(auth))
}
