package com.tencent.devops.scm.provider.gitee.simple

import com.tencent.devops.scm.api.FileService
import com.tencent.devops.scm.api.IssueService
import com.tencent.devops.scm.api.PullRequestService
import com.tencent.devops.scm.api.RefService
import com.tencent.devops.scm.api.RepositoryService
import com.tencent.devops.scm.api.TokenService
import com.tencent.devops.scm.api.UserService
import com.tencent.devops.scm.api.WebhookEnricher
import com.tencent.devops.scm.api.WebhookParser
import com.tencent.devops.scm.provider.git.command.GitScmProvider

open class GiteeScmProvider(private val apiFactory: GiteeApiClientFactory) : GitScmProvider() {

    constructor(apiUrl: String) : this(GiteeApiClientFactory(apiUrl))

    override fun repositories(): RepositoryService {
        return GiteeRepositoryService(apiFactory)
    }

    override fun refs(): RefService {
        return GiteeRefService(apiFactory)
    }

    override fun issues(): IssueService {
        throw UnsupportedOperationException("gitee template not support issue service")
    }

    override fun users(): UserService {
        throw UnsupportedOperationException("gitee template not support user service")
    }

    override fun files(): FileService {
        throw UnsupportedOperationException("gitee template not support file service")
    }

    override fun webhookParser(): WebhookParser {
        throw UnsupportedOperationException("gitee template not support webhook parser")
    }

    override fun webhookEnricher(): WebhookEnricher {
        throw UnsupportedOperationException("gitee template not support webhook enricher")
    }

    override fun pullRequests(): PullRequestService {
        throw UnsupportedOperationException("gitee template not support pull request service")
    }

    override fun token(): TokenService {
        throw UnsupportedOperationException("gitee template not support token service")
    }
}
