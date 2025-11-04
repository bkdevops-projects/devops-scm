package com.tencent.devops.scm.provider.git.bkcode

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
import com.tencent.devops.scm.sdk.bkcode.BkCodeApiFactory
import com.tencent.devops.scm.sdk.bkcode.BkCodeOauth2Api
import com.tencent.devops.scm.sdk.common.GitOauth2ClientProperties
import com.tencent.devops.scm.sdk.common.connector.ScmConnector

/**
 * BkCode SCM提供者实现类
 * @property apiFactory BkCode API工厂
 * @property oauth2Api BkCode OAuth2 API
 */
class BkCodeScmProvider : GitScmProvider {
    private val apiFactory: BkCodeApiFactory
    private var oauth2Api: BkCodeOauth2Api? = null

    constructor(
        apiUrl: String,
        connector: ScmConnector,
        properties: GitOauth2ClientProperties
    ) : this(apiUrl, connector) {
        this.oauth2Api = BkCodeOauth2Api(properties, connector)
    }

    /**
     * 构造函数
     * @param apiUrl API地址
     * @param connector 连接器
     */
    constructor(apiUrl: String, connector: ScmConnector) {
        this.apiFactory = BkCodeApiFactory(apiUrl, connector)
    }

    /**
     * 构造函数
     * @param apiFactory BkCode API工厂
     */
    constructor(apiFactory: BkCodeApiFactory) {
        this.apiFactory = apiFactory
    }

    override fun repositories(): RepositoryService {
        return BkCodeRepositoryService(apiFactory)
    }

    override fun checkRun(): CheckRunService {
        return BkCodeCheckRunService(apiFactory)
    }

    override fun refs(): RefService {
        return BkCodeRefService(apiFactory)
    }

    override fun issues(): IssueService {
        throw UnsupportedOperationException("BkCode does not support issues service")
    }

    override fun users(): UserService {
        return BkCodeUserService(apiFactory)
    }

    override fun files(): FileService {
        throw UnsupportedOperationException("BkCode does not support files service")
    }

    override fun webhookParser(): WebhookParser {
        return BkCodeWebhookParser()
    }

    override fun webhookEnricher(): WebhookEnricher {
        return BkCodeWebhookEnricher(apiFactory)
    }

    override fun pullRequests(): PullRequestService {
        return BkCodeMergeRequestService(apiFactory)
    }

    override fun token(): TokenService {
        return oauth2Api?.let { BkCodeTokenService(it) } ?: throw IllegalStateException("OAuth2 API not initialized")
    }
}
