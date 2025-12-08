package com.tencent.devops.scm.provider.git.gitee

import com.tencent.devops.scm.api.CheckRunService
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
import com.tencent.devops.scm.sdk.gitee.GiteeOauth2Api

class GiteeScmProvider : GitScmProvider {
    private val apiFactory: GiteeApiFactory
    private var oauth2Api: GiteeOauth2Api? = null

    constructor(apiUrl: String, connector: ScmConnector) : this(GiteeApiFactory(apiUrl, connector))

    constructor(
        apiUrl: String,
        connector: ScmConnector,
        properties: GitOauth2ClientProperties
    ) : this(apiUrl, connector) {
        this.oauth2Api = GiteeOauth2Api(properties, connector)
    }

    constructor(apiFactory: GiteeApiFactory) {
        this.apiFactory = apiFactory
    }

    override fun repositories(): RepositoryService {
        return GiteeRepositoryService(apiFactory)
    }

    override fun checkRun(): CheckRunService {
        return GiteeCheckRunService(apiFactory)
    }

    override fun refs(): RefService {
        return GiteeRefService(apiFactory)
    }

    override fun issues(): IssueService {
        throw UnsupportedOperationException("gitee not support issue service")
    }

    override fun users() : UserService {
        return GiteeUserService(apiFactory)
    }

    override fun files(): FileService {
        return GiteeFileService(apiFactory)
    }

    override fun webhookParser(): WebhookParser {
        return GiteeWebhookParser()
    }

    override fun webhookEnricher(): WebhookEnricher {
        return GiteeWebhookEnricher(apiFactory)
    }

    override fun pullRequests(): PullRequestService {
        throw UnsupportedOperationException("gitee not support pull requests service")
    }

    override fun token(): TokenService {
        return oauth2Api?.let { GiteeTokenService(it) } ?: throw IllegalStateException("OAuth2 API not initialized")
    }
}
