package com.tencent.devops.scm.provider.git.gitee

import com.tencent.devops.scm.api.enums.EventAction
import com.tencent.devops.scm.api.pojo.HookRequest
import com.tencent.devops.scm.api.pojo.webhook.git.GitPushHook
import com.tencent.devops.scm.api.pojo.webhook.git.PullRequestHook
import java.io.File
import java.io.IOException
import java.util.HashMap
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

@Disabled
class GiteeWebhookParserTest : AbstractGiteeServiceTest() {

    private val webhookParser = GiteeWebhookParser()
    private val webhookEnricher = GiteeWebhookEnricher(giteeApiFactory)

    @Test
    fun testGitPullRequestHook() {
        val filePath = this::class.java.classLoader
                .getResource("pull_request_webhook.json")
                ?.file ?: throw IOException("File not found")
        val payload = File(filePath).readText(Charsets.UTF_8)

        val headers = HashMap<String, String>().apply {
            put("X-Gitee-Event", "Merge Request Hook")
        }

        val request = HookRequest(
            headers = headers,
            body = payload
        )

        val webhook = webhookParser.parse(request) as PullRequestHook
        Assertions.assertEquals(1, webhook.pullRequest.number)
        Assertions.assertEquals(EventAction.OPEN, webhook.action)
    }


    @Test
    fun testGitPushHook() {
        val filePath = this::class.java.classLoader
                .getResource("push_webhook.json")
                ?.file ?: throw IOException("push_webhook.json")
        val payload = File(filePath).readText(Charsets.UTF_8)

        val headers = HashMap<String, String>().apply {
            put("X-Gitee-Event", "Push Hook")
        }

        val request = HookRequest(
            headers = headers,
            body = payload
        )

        val webhook = webhookParser.parse(request) as GitPushHook
        webhook.outputs().forEach { t, u ->
            println("${GitPushHook::class.simpleName}|$t=$u")
        }
    }
}