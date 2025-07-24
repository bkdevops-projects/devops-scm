package com.tencent.devops.scm.provider.gitee.simple

import com.gitee.sdk.gitee5j.ApiClient
import com.tencent.devops.scm.api.pojo.repository.git.GitScmProviderRepository
import com.tencent.devops.scm.provider.gitee.simple.auth.GiteeTokenAuthProviderAdapter

class GiteeApiClientFactory(private val apiUrl: String) {

    fun getClient(repository: GitScmProviderRepository): ApiClient {
        // 创建客户端
        val client = ApiClient()
        // 设置基础路径
        client.basePath = apiUrl
        // 绑定授权信息
        GiteeTokenAuthProviderAdapter(client).withAuth(repository.auth!!)
        return client
    }
}
