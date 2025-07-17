package com.tencent.devops.scm.api

import com.tencent.devops.scm.api.exception.UnAuthorizedScmApiException
import com.tencent.devops.scm.api.pojo.repository.ScmProviderRepository
import com.tencent.devops.scm.api.pojo.webhook.Webhook

/**
 * Webhook增强器接口
 */
interface WebhookEnricher {
    /**
     * 通过调用API接口补充webhook信息
     * @param repository 代码库信息
     * @param webhook WebhookService.parse解析的对象
     * @return 增强后的webhook信息
     * @throws UnAuthorizedScmApiException 当授权信息失效或没有权限时抛出
     */
    @Throws(UnAuthorizedScmApiException::class)
    fun enrich(repository: ScmProviderRepository, webhook: Webhook): Webhook
}
