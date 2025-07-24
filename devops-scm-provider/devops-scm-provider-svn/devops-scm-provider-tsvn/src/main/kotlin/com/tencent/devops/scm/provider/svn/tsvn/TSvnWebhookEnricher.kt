package com.tencent.devops.scm.provider.svn.tsvn

import com.tencent.devops.scm.api.pojo.repository.ScmProviderRepository
import com.tencent.devops.scm.api.pojo.repository.svn.SvnScmProviderRepository
import com.tencent.devops.scm.api.pojo.webhook.Webhook
import com.tencent.devops.scm.api.pojo.webhook.svn.PostCommitHook
import com.tencent.devops.scm.provider.svn.common.SvnWebhookEnricher
import com.tencent.devops.scm.sdk.tsvn.TSvnApiFactory
import java.util.*

class TSvnWebhookEnricher(private val apiFactory: TSvnApiFactory) : SvnWebhookEnricher() {

    private val eventActions: MutableMap<Class<out Webhook>, (SvnScmProviderRepository, Webhook)->Unit> = HashMap()

    init {
        eventActions[PostCommitHook::class.java] = this::enrichPostCommitHook
    }

    private fun enrichPostCommitHook(
        repository: SvnScmProviderRepository,
        hook: Webhook
    ) {
        println("enrich post commit hook")
    }

    override fun enrich(repository: ScmProviderRepository, webhook: Webhook): Webhook {
        eventActions[webhook.javaClass]?.invoke(repository as SvnScmProviderRepository, webhook)
        return webhook
    }
}