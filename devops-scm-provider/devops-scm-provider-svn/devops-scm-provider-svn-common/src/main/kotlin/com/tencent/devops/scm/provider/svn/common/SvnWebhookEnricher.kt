package com.tencent.devops.scm.provider.svn.common

import com.tencent.devops.scm.api.WebhookEnricher
import com.tencent.devops.scm.api.pojo.repository.ScmProviderRepository
import com.tencent.devops.scm.api.pojo.webhook.Webhook

open class SvnWebhookEnricher : WebhookEnricher {

    override fun enrich(repository: ScmProviderRepository, webhook: Webhook) = webhook
}