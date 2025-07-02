package com.tencent.devops.scm.provider.git.gitee

import com.tencent.devops.scm.api.WebhookEnricher
import com.tencent.devops.scm.api.enums.EventAction
import com.tencent.devops.scm.api.pojo.PullRequest
import com.tencent.devops.scm.api.pojo.repository.ScmProviderRepository
import com.tencent.devops.scm.api.pojo.repository.git.GitScmProviderRepository
import com.tencent.devops.scm.api.pojo.webhook.Webhook
import com.tencent.devops.scm.api.pojo.webhook.git.GitPushHook
import com.tencent.devops.scm.api.pojo.webhook.git.PullRequestHook
import com.tencent.devops.scm.sdk.gitee.GiteeApi
import com.tencent.devops.scm.sdk.gitee.GiteeApiFactory

class GiteeWebhookEnricher(private val apiFactory: GiteeApiFactory) : WebhookEnricher {

    private val eventActions = mutableMapOf<Class<out Webhook>, (GitScmProviderRepository, Webhook) -> Unit>()

    init {
        eventActions[PullRequestHook::class.java] = this::enrichPullRequestHook
        eventActions[GitPushHook::class.java] = this::enrichPushHook
    }

    private fun enrichPushHook(repository: GitScmProviderRepository, hook: Webhook) {
        val gitPushHook = hook as GitPushHook
        if (gitPushHook.action == EventAction.PUSH_FILE) {
            GiteeApiTemplate.execute(repository, apiFactory) { _, giteeApi ->
                val commitCompare = giteeApi.fileApi.commitCompare(
                    repository.projectIdOrPath,
                    gitPushHook.before,
                    gitPushHook.after,
                    false
                )
                gitPushHook.changes = GiteeObjectConverter.convertCompare(commitCompare)
            }
        }
    }

    private fun enrichPullRequestHook(repository: GitScmProviderRepository, hook: Webhook) {
        val pullRequestHook = hook as PullRequestHook
        val pullRequest = pullRequestHook.pullRequest
        GiteeApiTemplate.execute(repository, apiFactory) { _, giteeApi ->
            // 填充文件变更列表
            val pullRequestDiffs = giteeApi.pullRequestApi.listChanges(
                repository.projectIdOrPath,
                pullRequest.number.toLong()
            )
            pullRequestHook.changes = pullRequestDiffs.map { GiteeObjectConverter.convertChange(it) }

            // 填充最后提交信息
            val requestHookCommit = pullRequestHook.commit
            val commit = giteeApi.commitApi.getCommit(
                repository.projectIdOrPath,
                requestHookCommit.sha
            )
            requestHookCommit.message = commit.commit?.message ?: ""
            requestHookCommit.author = GiteeObjectConverter.convertSignature(commit.author)

            // 根据完整的MR/Review信息填充变量
            pullRequestHook.extras.putAll(
                fillPullRequestReviewVars(giteeApi, repository, pullRequest)
            )
        }
    }

    override fun enrich(repository: ScmProviderRepository, webhook: Webhook): Webhook {
        eventActions[webhook.javaClass]?.invoke(repository as GitScmProviderRepository, webhook)
        return webhook
    }

    private fun fillPullRequestReviewVars(
        giteeApi: GiteeApi,
        repository: GitScmProviderRepository,
        pullRequest: PullRequest
    ): Map<String, Any> {
        return fillPullRequestVars(giteeApi, repository, pullRequest)
    }

    private fun fillPullRequestVars(
        giteeApi: GiteeApi,
        repository: GitScmProviderRepository,
        pullRequest: PullRequest
    ): Map<String, Any> {
        return giteeApi.pullRequestApi.getPullRequest(
            repository.projectIdOrPath,
            pullRequest.number.toLong()
        )?.let {
            pullRequest.title = it.title ?: ""
            GiteeObjectToMapConverter.convertPullRequest(it)
        } ?: mapOf()
    }
}
