package com.tencent.devops.scm.provider.svn.tsvn

import com.tencent.devops.scm.api.RefService
import com.tencent.devops.scm.api.RepositoryService
import com.tencent.devops.scm.api.WebhookParser
import com.tencent.devops.scm.provider.svn.common.AbstractSvnScmProvider
import com.tencent.devops.scm.sdk.common.connector.ScmConnector
import com.tencent.devops.scm.sdk.tsvn.TSvnApiFactory

class TSvnScmProvider : AbstractSvnScmProvider {
    private val apiFactory: TSvnApiFactory

    constructor(apiUrl: String, connector: ScmConnector) : this(TSvnApiFactory(apiUrl, connector))

    constructor(apiFactory: TSvnApiFactory) {
        this.apiFactory = apiFactory
    }

    override fun repositories(): RepositoryService = TSvnRepositoryService(apiFactory)

    override fun refs(): RefService = TSvnRefService(apiFactory)

    override fun webhookParser(): WebhookParser = TSvnWebhookParser()
}