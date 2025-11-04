package com.tencent.devops.scm.provider.git.bkcode

import com.fasterxml.jackson.core.type.TypeReference
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_BRANCH
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_PUSH_AFTER_COMMIT
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_PUSH_BEFORE_COMMIT
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_PUSH_PROJECT_ID
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_PUSH_USERNAME
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_BEFORE_SHA
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_BEFORE_SHA_SHORT
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_COMMIT_MESSAGE
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_EVENT_URL
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_REF
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_REPO_URL
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_SHA_SHORT
import com.tencent.devops.scm.api.pojo.HookRequest
import com.tencent.devops.scm.api.pojo.webhook.git.GitPushHook
import com.tencent.devops.scm.api.pojo.webhook.git.GitTagHook
import com.tencent.devops.scm.api.pojo.webhook.git.IssueCommentHook
import com.tencent.devops.scm.api.pojo.webhook.git.IssueHook
import com.tencent.devops.scm.api.pojo.webhook.git.PullRequestCommentHook
import com.tencent.devops.scm.api.pojo.webhook.git.PullRequestHook
import com.tencent.devops.scm.sdk.bkcode.enums.BkCodeEventType
import com.tencent.devops.scm.sdk.common.util.ScmJsonUtil
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class BkCodeWebhookParserTest : AbstractBkCodeWebhookParserTest() {
    @Test
    fun testGitPushHook() {
        val webhook = parseWebhook(
            "bkcode_push_event.json",
            BkCodeEventType.PUSH,
            GitPushHook::class.java
        )
        assertPushOutputs(webhook)
    }

    private fun assertPushOutputs(webhook: GitPushHook) {
        val outputs = webhook.outputs()
        Assertions.assertEquals("zhangsan", outputs[BK_REPO_GIT_WEBHOOK_PUSH_USERNAME])
        Assertions.assertEquals(
            "0000000000000000000000000000000000000000",
            outputs[BK_REPO_GIT_WEBHOOK_PUSH_BEFORE_COMMIT]
        )
        Assertions.assertEquals(
            "7eaa451cdfd155338f113f1bb2d57527f34fc657",
            outputs[BK_REPO_GIT_WEBHOOK_PUSH_AFTER_COMMIT]
        )
        Assertions.assertEquals("dev", outputs[BK_REPO_GIT_WEBHOOK_BRANCH])

        Assertions.assertEquals("11", outputs[BK_REPO_GIT_WEBHOOK_PUSH_PROJECT_ID].toString())
        Assertions.assertEquals(
            "https://bkcode.template.com/hejieehe/devops_trigger.git",
            outputs[PIPELINE_GIT_REPO_URL]
        )
        Assertions.assertEquals(
            "refs/heads/dev",
            outputs[PIPELINE_GIT_REF]
        )
        Assertions.assertEquals(
            "0000000000000000000000000000000000000000",
            outputs[PIPELINE_GIT_BEFORE_SHA]
        )
        Assertions.assertEquals(
            "00000000",
            outputs[PIPELINE_GIT_BEFORE_SHA_SHORT]
        )
        Assertions.assertEquals(
            "https://bkcode.template.com/hejieehe/devops_trigger" +
                    "/-/commit/7eaa451cdfd155338f113f1bb2d57527f34fc657",
            outputs[PIPELINE_GIT_EVENT_URL]
        )
        Assertions.assertEquals(
            "7eaa451c",
            outputs[PIPELINE_GIT_SHA_SHORT]
        )
        Assertions.assertEquals(
            "",
            outputs[PIPELINE_GIT_COMMIT_MESSAGE]
        )
    }

    @Test
    fun testGitPullRequestHook() {
        val webhook = parseWebhook(
            "bkcode_mr_event.json",
            BkCodeEventType.MERGE_REQUEST,
            PullRequestHook::class.java
        )
        println(webhook)
    }

    @Test
    fun commitNote() {
        val eventFiles = mapOf(
            "bkcode_issues_note_event.json" to IssueCommentHook::class,
            "bkcode_mr_note_event.json" to PullRequestCommentHook::class
        )
        eventFiles.forEach {(fileName, cls) ->
            println("========================================================================")
            val webhook = parseWebhook(
                fileName,
                BkCodeEventType.NOTE,
                cls.java
            )
            webhook.outputs().forEach { (t, u) ->
                println("${cls.java.simpleName}|$t=$u")
            }
        }
    }

    @Test
    fun tagEvent() {
        val webhook = parseWebhook(
            "bkcode_tag_event.json",
            BkCodeEventType.TAG_PUSH,
            GitTagHook::class.java
        )
        println("webhook = ${webhook}")
    }

    @Test
    fun issueEvent() {
        val webhook = parseWebhook(
            "bkcode_issue_event.json",
            BkCodeEventType.ISSUES,
            IssueHook::class.java
        )
        println("webhook = ${webhook}")
    }

    @Test
    fun verify() {
        val hookData = ScmJsonUtil.fromJson(
            readFile(path = "bkcode_verify_hook.json"),
            object : TypeReference<Map<String, Any>>() {}
        )
        val verify = webhookParser.verify(
            request = HookRequest(
                headers = hookData["headers"] as Map<String, String>,
                body = ScmJsonUtil.getJsonFactory().toJson(hookData["body"] as Map<*, *>),
                queryParams = hookData["queryParams"] as Map<String, String>?
            ),
            secretToken = "123456"
        )
        Assertions.assertTrue(verify)
    }
}
