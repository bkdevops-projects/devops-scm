package com.tencent.devops.scm.provider.svn.tsvn

import com.tencent.devops.scm.api.pojo.Reference
import com.tencent.devops.scm.api.pojo.repository.ScmProviderRepository
import com.tencent.devops.scm.provider.svn.common.SvnRefService
import com.tencent.devops.scm.sdk.tsvn.TSvnApiException
import com.tencent.devops.scm.sdk.tsvn.TSvnApiFactory
import com.tencent.devops.scm.sdk.tsvn.TSvnCommitApi

class TSvnRefService(private val apiFactory: TSvnApiFactory) : SvnRefService() {

    override fun findBranch(repository: ScmProviderRepository, name: String): Reference {
        return TSvnApiTemplate.execute(repository, apiFactory) { repo, tSvnApi ->
            // 构建请求接口类
            val commitApi: TSvnCommitApi = tSvnApi.commitApi
            val projectApiHooks = commitApi.getCommit(
                repo.projectIdOrPath,
                name,
                1
            )
            projectApiHooks.firstOrNull()?.let { TSvnObjectConverter.convertReference(it) }
                ?: throw TSvnApiException("branch not found")
        }
    }
}