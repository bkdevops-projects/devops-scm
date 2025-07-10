package com.tencent.devops.scm.provider.svn.tsvn

import com.tencent.devops.scm.api.WebhookParser
import com.tencent.devops.scm.api.pojo.HookRequest
import com.tencent.devops.scm.api.pojo.User
import com.tencent.devops.scm.api.pojo.webhook.Webhook
import com.tencent.devops.scm.api.pojo.webhook.svn.PostCommitHook
import com.tencent.devops.scm.provider.svn.tsvn.enums.TSvnEventType
import com.tencent.devops.scm.sdk.common.util.ScmJsonUtil
import com.tencent.devops.scm.sdk.tsvn.TSvnConstants.HOOK_SOURCE_TYPE
import com.tencent.devops.scm.sdk.tsvn.TSvnConstants.TEST_HOOK_SOURCE_TYPE_VALUE
import com.tencent.devops.scm.sdk.tsvn.pojo.TSvnPostCommitEvent
import org.apache.commons.collections4.CollectionUtils
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class TSvnWebhookParser : WebhookParser {

    companion object {
        private val logger = LoggerFactory.getLogger(TSvnWebhookParser::class.java)
    }

    override fun parse(request: HookRequest): Webhook? {
        val skipCi = skipCi(request)
        return when (request.headers?.get("X-Event")) {
            "Svn Post Commit" -> parsePostCommitHook(request.body, skipCi)
            else -> null
        }
    }

    override fun verify(request: HookRequest, secretToken: String?): Boolean {
        // 没有值不需要校验
        if (secretToken.isNullOrEmpty()) {
            return true
        }
        val token = request.headers?.get("X-Token")
        return secretToken == token
    }

    private fun parsePostCommitHook(body: String, skipCi: Boolean): PostCommitHook {
        val src = ScmJsonUtil.fromJson(body, TSvnPostCommitEvent::class.java)
        return with(src) {
            PostCommitHook(
                repo = TSvnObjectConverter.convertRepository(repository,projectId,repName),
                revision = revision,
                message = message,
                changes = files?.map { TSvnObjectConverter.convertChange(it) }?: listOf(),
                commitTime = commitTime,
                sender = User(
                    id = userId,
                    name = userName,
                    email = userEmail,
                    username = userName
                ),
                eventType = TSvnEventType.POST_COMMIT.name,
                skipCi = skipCi
            )
        }
    }

    /**
     * 测试hook无需触发CI
     */
    private fun skipCi(request: HookRequest): Boolean {
        return TEST_HOOK_SOURCE_TYPE_VALUE == (request.headers?.get(HOOK_SOURCE_TYPE) ?: "")
    }
}