package com.tencent.devops.scm.provider.git.tgit

import TGitRepositoryService
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
import com.tencent.devops.scm.sdk.tgit.TGitApiFactory
import com.tencent.devops.scm.sdk.tgit.TGitOauth2Api

/**
 * TGit SCM提供者实现类
 * @property apiFactory TGit API工厂
 * @property oauth2Api TGit OAuth2 API
 */
class TGitScmProvider : GitScmProvider {
    private val apiFactory: TGitApiFactory
    private var oauth2Api: TGitOauth2Api? = null

    /**
     * 构造函数
     * @param apiUrl API地址
     * @param connector 连接器
     */
    constructor(apiUrl: String, connector: ScmConnector) {
        this.apiFactory = TGitApiFactory(apiUrl, connector)
    }

    /**
     * 构造函数
     * @param apiUrl API地址
     * @param connector 连接器
     * @param properties OAuth2客户端属性
     */
    constructor(
        apiUrl: String,
        connector: ScmConnector,
        properties: GitOauth2ClientProperties
    ) : this(apiUrl, connector) {
        this.oauth2Api = TGitOauth2Api(properties, connector)
    }

    /**
     * 构造函数
     * @param apiFactory TGit API工厂
     */
    constructor(apiFactory: TGitApiFactory) {
        this.apiFactory = apiFactory
    }

    override fun repositories(): RepositoryService {
        return TGitRepositoryService(apiFactory)
    }

    override fun checkRun(): CheckRunService {
        return TGitCheckRunService(apiFactory)
    }

    override fun refs(): RefService {
        return TGitRefService(apiFactory)
    }

    override fun issues(): IssueService {
        return TGitIssueService(apiFactory)
    }

    override fun users(): UserService {
        return TGitUserService(apiFactory)
    }

    override fun files(): FileService {
        return TGitFileService(apiFactory)
    }

    override fun webhookParser(): WebhookParser {
        return TGitWebhookParser()
    }

    override fun webhookEnricher(): WebhookEnricher {
        return TGitWebhookEnricher(apiFactory)
    }

    override fun pullRequests(): PullRequestService {
        return TGitPullRequestService(apiFactory)
    }

    override fun token(): TokenService {
        return oauth2Api?.let { TGitTokenService(it) } ?: throw IllegalStateException("OAuth2 API not initialized")
    }
}
