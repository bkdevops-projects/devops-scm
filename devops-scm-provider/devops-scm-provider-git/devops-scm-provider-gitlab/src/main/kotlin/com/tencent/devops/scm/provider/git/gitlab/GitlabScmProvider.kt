package com.tencent.devops.scm.provider.git.gitlab

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
import com.tencent.devops.scm.sdk.gitlab.GitlabApiFactory
import com.tencent.devops.scm.sdk.gitlab.GitlabOauth2Api

class GitlabScmProvider private constructor(
    private val apiFactory: GitlabApiFactory,
    private val oauth2Api: GitlabOauth2Api?
) : GitScmProvider() {
    constructor(apiUrl: String, connector: ScmConnector) : this(GitlabApiFactory(apiUrl, connector), null)

    constructor(apiUrl: String, connector: ScmConnector, properties: GitOauth2ClientProperties) :
        this(GitlabApiFactory(apiUrl, connector), GitlabOauth2Api(properties, connector))

    constructor(apiFactory: GitlabApiFactory) : this(apiFactory, null)

    override fun repositories(): RepositoryService = GitlabRepositoryService(apiFactory)
    override fun refs(): RefService = GitlabRefService(apiFactory)
    override fun issues(): IssueService = GitlabIssueService(apiFactory)
    override fun users(): UserService = GitlabUserService(apiFactory)
    override fun files(): FileService = GitlabFileService(apiFactory)
    override fun webhookParser(): WebhookParser = GitlabWebhookParser()
    override fun webhookEnricher(): WebhookEnricher = GitlabWebhookEnricher(apiFactory)
    override fun pullRequests(): PullRequestService = GitlabPullRequestService(apiFactory)
    override fun checkRun(): CheckRunService = GitlabCheckRunService(apiFactory)
    override fun token(): TokenService = oauth2Api?.let(::GitlabTokenService)
        ?: throw IllegalStateException("OAuth2 API not initialized")
}
