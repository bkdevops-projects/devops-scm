package com.tencent.devops.scm.sample

import com.tencent.devops.scm.api.enums.ScmProviderCodes
import com.tencent.devops.scm.api.pojo.auth.IScmAuth
import com.tencent.devops.scm.api.pojo.auth.PersonalAccessTokenScmAuth
import com.tencent.devops.scm.api.pojo.repository.git.GitScmProviderRepository
import com.tencent.devops.scm.spring.manager.ScmProviderManager
import com.tencent.devops.scm.spring.properties.HttpClientProperties
import com.tencent.devops.scm.spring.properties.ScmProviderProperties
import org.springframework.beans.factory.annotation.Autowired

/**
 * git类型的源码管理使用示例
 */
class SampleGitScmProvider {

    @Autowired
    private lateinit var scmProviderManager: ScmProviderManager

    private fun initProviderProperties(): ScmProviderProperties {
        val httpClientProperties = HttpClientProperties(
            apiUrl = "https://api.github.com"
        )
        return ScmProviderProperties(
            providerCode = ScmProviderCodes.GITHUB.name,
            httpClientProperties = httpClientProperties
        )
    }

    private fun initProviderRepository(): GitScmProviderRepository {
        return GitScmProviderRepository(
            auth = PersonalAccessTokenScmAuth("YOUR_PERSONAL_ACCESS_TOKEN"),
            url = "https://github.com/bkdevops-projects/devops-scm.git"
        )
    }

    /**
     * 获取仓库信息
     */
    fun getRepository() {
        val providerProperties = initProviderProperties()
        val providerRepository = initProviderRepository()
        scmProviderManager.repositories(providerProperties).find(providerRepository)
    }

    /**
     * 获取分支信息
     */
    fun getBranch() {
        val providerProperties = initProviderProperties()
        val providerRepository = initProviderRepository()
        scmProviderManager.refs(providerProperties).findBranch(providerRepository, "master")
    }

    /**
     * 获取tag信息
     */
    fun getTag() {
        val providerProperties = initProviderProperties()
        val providerRepository = initProviderRepository()
        scmProviderManager.refs(providerProperties).findTag(providerRepository, "v1.0.0")
    }

    /**
     * 获取issue信息
     */
    fun getIssue() {
        val providerProperties = initProviderProperties()
        val providerRepository = initProviderRepository()
        scmProviderManager.issues(providerProperties).find(providerRepository, 1)
    }

    /**
     * 获取pr信息
     */
    fun getPullRequest() {
        val providerProperties = initProviderProperties()
        val providerRepository = initProviderRepository()
        scmProviderManager.pullRequests(providerProperties).find(providerRepository, 1)
    }

    /**
     * 获取用户信息
     */
    fun getUser() {
        val providerProperties = initProviderProperties()
        val auth: IScmAuth = PersonalAccessTokenScmAuth("YOUR_PERSONAL_ACCESS_TOKEN")
        scmProviderManager.users(providerProperties).find(auth)
    }
}