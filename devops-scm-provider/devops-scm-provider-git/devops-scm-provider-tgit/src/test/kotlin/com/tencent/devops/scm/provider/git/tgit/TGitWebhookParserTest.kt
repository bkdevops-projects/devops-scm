package com.tencent.devops.scm.provider.git.tgit

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
import com.tencent.devops.scm.api.pojo.webhook.git.CommitCommentHook
import com.tencent.devops.scm.api.pojo.webhook.git.GitPushHook
import com.tencent.devops.scm.api.pojo.webhook.git.IssueCommentHook
import com.tencent.devops.scm.api.pojo.webhook.git.PullRequestCommentHook
import com.tencent.devops.scm.api.pojo.webhook.git.PullRequestHook
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.io.File

class TGitWebhookParserTest {

    private val webhookParser = TGitWebhookParser()

    @Test
    fun testGitPushHook() {
        val filePath = this::class.java.classLoader.getResource("tgit_push_event.json")?.file
            ?: throw IllegalStateException("资源文件未找到")
        val payload = File(filePath).readText(Charsets.UTF_8)

        val headers = mapOf(
            "X-Event" to "Push Hook",
            "X-Event-Type" to "git_push",
            "X-Source-Type" to "Project"
        )


        val request = HookRequest(
            headers = headers,
            body = payload
        )

        val webhook = webhookParser.parse(request) as GitPushHook
        assertPushOutputs(webhook)

        Assertions.assertEquals(
            "git@git.code.tencent.com:mingshewhe/webhook_test3.git",
            webhook.repository().sshUrl
        )
        Assertions.assertEquals(
            "https://git.code.tencent.com/mingshewhe/webhook_test3",
            webhook.repository().webUrl
        )

        Assertions.assertEquals(
            "b8bb3fccda519efb57d2ffab7e7771983e8ef02f",
            webhook.commit?.sha
        )
        Assertions.assertEquals(
            "提交测试2",
            webhook.commit?.message
        )
        Assertions.assertEquals(
            "https://git.code.tencent.com/mingshewhe/webhook_test3/commit/b8bb3fccda519efb57d2ffab7e7771983e8ef02f",
            webhook.commit?.link
        )
        Assertions.assertEquals(
            "mingshewhe",
            webhook.commit?.author?.name
        )
        Assertions.assertEquals(
            "wx_a56dc86ef0f74feda385d4818e7c5cda@git.code.tencent.com",
            webhook.commit?.author?.email
        )
    }

    private fun assertPushOutputs(webhook: GitPushHook) {
        val outputs = webhook.outputs()
        Assertions.assertEquals("mingshewhe", outputs[BK_REPO_GIT_WEBHOOK_PUSH_USERNAME])
        Assertions.assertEquals(
            "45a21e3370488d9e59215f64654ba96bbb5d1da9",
            outputs[BK_REPO_GIT_WEBHOOK_PUSH_BEFORE_COMMIT]
        )
        Assertions.assertEquals(
            "b8bb3fccda519efb57d2ffab7e7771983e8ef02f",
            outputs[BK_REPO_GIT_WEBHOOK_PUSH_AFTER_COMMIT]
        )
        Assertions.assertEquals("master", outputs[BK_REPO_GIT_WEBHOOK_BRANCH])

        Assertions.assertEquals("130762", outputs[BK_REPO_GIT_WEBHOOK_PUSH_PROJECT_ID].toString())
        Assertions.assertEquals(
            "https://git.code.tencent.com/mingshewhe/webhook_test3.git",
            outputs[PIPELINE_GIT_REPO_URL]
        )
        Assertions.assertEquals(
            "refs/heads/master",
            outputs[PIPELINE_GIT_REF]
        )
        Assertions.assertEquals(
            "45a21e3370488d9e59215f64654ba96bbb5d1da9",
            outputs[PIPELINE_GIT_BEFORE_SHA]
        )
        Assertions.assertEquals(
            "45a21e33",
            outputs[PIPELINE_GIT_BEFORE_SHA_SHORT]
        )
        Assertions.assertEquals(
            "https://git.code.tencent.com/mingshewhe/webhook_test3/" +
                    "commit/b8bb3fccda519efb57d2ffab7e7771983e8ef02f",
            outputs[PIPELINE_GIT_EVENT_URL]
        )
        Assertions.assertEquals(
            "b8bb3fcc",
            outputs[PIPELINE_GIT_SHA_SHORT]
        )
        Assertions.assertEquals(
            "https://git.code.tencent.com/mingshewhe/webhook_test3/" +
                    "commit/b8bb3fccda519efb57d2ffab7e7771983e8ef02f",
            outputs[PIPELINE_GIT_EVENT_URL]
        )
        Assertions.assertEquals(
            "提交测试2",
            outputs[PIPELINE_GIT_COMMIT_MESSAGE]
        )
    }

    @Test
    fun testGitPullRequestHook() {
        val filePath = this::class.java.classLoader.getResource("tgit_mr_event.json")?.file
            ?: throw IllegalStateException("资源文件未找到")
        val payload = File(filePath).readText(Charsets.UTF_8)

        val headers = mapOf(
            "X-Event" to "Merge Request Hook",
            "X-Event-Type" to "merge_request",
            "X-Source-Type" to "Project"
        )

        val request = HookRequest(
            headers = headers,
            body = payload
        )

        val webhook = webhookParser.parse(request) as PullRequestHook
        println(webhook)
    }

    @Test
    fun commitNote() {
        val eventFiles = mapOf(
            "tgit_commit_note_event.json" to CommitCommentHook::class,
            "tgit_review_note_event.json" to PullRequestCommentHook::class,
            "tgit_issue_note_event.json" to IssueCommentHook::class
        )

        val headers = mapOf(
            "X-Event" to "Note Hook",
            "X-Event-Type" to "note",
            "X-Source-Type" to "Project"
        )
        eventFiles.forEach {(fileName, cls) ->
            val filePath = this::class.java.classLoader.getResource(fileName)?.file
                ?: throw IllegalStateException("资源文件[$fileName]未找到")
            val payload = File(filePath).readText(Charsets.UTF_8)

            val request = HookRequest(
                headers = headers,
                body = payload
            )

            val webhook = cls.java.cast(webhookParser.parse(request))
            println(webhook)
            println(webhook.outputs())
        }
    }
}
