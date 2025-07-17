package com.tencent.devops.scm.provider.svn.tsvn

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
import com.tencent.devops.scm.api.pojo.webhook.svn.PostCommitHook
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.File

class TSvnWebhookParserTest {

    private val webhookParser = TSvnWebhookParser()

    @Test
    fun commitHook() {
        val eventFiles = mapOf(
            "tsvn_post_commit_event.json" to PostCommitHook::class
        )

        val headers = mapOf(
            "X-Event" to "Svn Post Commit",
            "X-Event-Type" to "svn_post_commit",
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
            println("========================================================================")
            val webhook = cls.java.cast(webhookParser.parse(request))
            webhook.outputs().forEach { t, u ->
                println("${cls.java.simpleName}|$t=$u")
            }
        }
    }
}
