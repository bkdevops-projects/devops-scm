package com.tencent.devops.scm.provider.git.gitee

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
import com.tencent.devops.scm.sdk.common.GitOauth2ClientProperties
import com.tencent.devops.scm.sdk.common.connector.ScmConnector
import com.tencent.devops.scm.sdk.gitee.GiteeApiFactory

class GiteeScmProvider : GitScmProvider {
    private val apiFactory: GiteeApiFactory

    constructor(apiUrl: String, connector: ScmConnector) : this(GiteeApiFactory(apiUrl, connector))

    constructor(
        apiUrl: String,
        connector: ScmConnector,
        properties: GitOauth2ClientProperties
    ) : this(apiUrl, connector)

    constructor(apiFactory: GiteeApiFactory) {
        this.apiFactory = apiFactory
    }

    override fun repositories(): RepositoryService {
        return GiteeRepositoryService(apiFactory)
    }

    override fun refs(): RefService {
        return GiteeRefService(apiFactory)
    }

    override fun issues(): IssueService {
        TODO("Not yet implemented")
    }

    override fun users() : UserService {
        TODO("Not yet implemented")
    }

    override fun files(): FileService {
        TODO("Not yet implemented")
    }

    override fun webhookParser(): WebhookParser {
        return GiteeWebhookParser()
    }

    override fun webhookEnricher(): WebhookEnricher {
        return GiteeWebhookEnricher(apiFactory)
    }

    override fun pullRequests(): PullRequestService {
        TODO("Not yet implemented")
    }

    override fun token(): TokenService {
        TODO("Not yet implemented")
    }
}
