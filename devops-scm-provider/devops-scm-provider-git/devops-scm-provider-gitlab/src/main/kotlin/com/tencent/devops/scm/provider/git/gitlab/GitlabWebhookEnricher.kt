package com.tencent.devops.scm.provider.git.gitlab

import com.tencent.devops.scm.api.WebhookEnricher
import com.tencent.devops.scm.api.constant.WebhookOutputCode.CI_BRANCH
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_BASE_REF
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_COMMIT_AUTHOR
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_COMMIT_MESSAGE
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_HEAD_REF
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_REF
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_SHA
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_SHA_SHORT
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_TAG_DESC
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_WEBHOOK_BRANCH
import com.tencent.devops.scm.api.enums.EventAction
import com.tencent.devops.scm.api.pojo.PullRequest
import com.tencent.devops.scm.api.pojo.repository.ScmProviderRepository
import com.tencent.devops.scm.api.pojo.repository.git.GitScmProviderRepository
import com.tencent.devops.scm.api.pojo.repository.git.GitScmServerRepository
import com.tencent.devops.scm.api.pojo.webhook.Webhook
import com.tencent.devops.scm.api.pojo.webhook.git.AbstractCommentHook
import com.tencent.devops.scm.api.pojo.webhook.git.GitPushHook
import com.tencent.devops.scm.api.pojo.webhook.git.GitTagHook
import com.tencent.devops.scm.api.pojo.webhook.git.IssueHook
import com.tencent.devops.scm.api.pojo.webhook.git.PullRequestCommentHook
import com.tencent.devops.scm.api.pojo.webhook.git.PullRequestHook
import com.tencent.devops.scm.sdk.gitlab.GitlabApiFactory

@Suppress("TooManyFunctions")
class GitlabWebhookEnricher(private val apiFactory: GitlabApiFactory) : WebhookEnricher {
    override fun enrich(repository: ScmProviderRepository, webhook: Webhook): Webhook {
        require(repository is GitScmProviderRepository) { "GitLab requires GitScmProviderRepository" }
        enrichRepository(repository, webhook.repository() as GitScmServerRepository)
        return when (webhook) {
            is PullRequestHook -> enrichPullRequest(repository, webhook)
            is GitPushHook -> enrichPush(repository, webhook)
            is IssueHook -> enrichIssue(repository, webhook)
            is AbstractCommentHook -> enrichComment(repository, webhook)
            is GitTagHook -> enrichTag(repository, webhook)
            else -> webhook
        }
    }

    private fun enrichRepository(repository: GitScmProviderRepository, webhookRepo: GitScmServerRepository) {
        if (webhookRepo.defaultBranch != null && webhookRepo.httpUrl.isNotBlank() && webhookRepo.archived != null) {
            return
        }
        GitlabApiTemplate.execute(repository, apiFactory) { repo, api ->
            val project = api.projectsApi.getProject(repo.projectIdOrPath)
            webhookRepo.defaultBranch = project.defaultBranch
            webhookRepo.archived = project.archived
            if (webhookRepo.httpUrl.isBlank()) webhookRepo.httpUrl = project.httpUrlToRepo.orEmpty()
        }
    }

    private fun enrichPullRequest(repository: GitScmProviderRepository, hook: PullRequestHook): PullRequestHook {
        GitlabApiTemplate.execute(repository, apiFactory) { repo, api ->
            val mr = api.mergeRequestsApi.getMergeRequestChanges(
                repo.projectIdOrPath,
                hook.pullRequest.number.toLong()
            )
            check(mr.overflow != true) {
                "GitLab merge request changes overflow; complete changes are unavailable"
            }
            if (hook.changes.isEmpty()) {
                hook.changes = mr.changes.orEmpty().map(GitlabObjectConverter::convertChange)
            }
            hook.pullRequest.title = mr.title.orEmpty().ifBlank { hook.pullRequest.title }
            hook.extras.putAll(GitlabObjectToMapConverter.convertPullRequest(mr, hook.repo))
        }
        return hook
    }

    private fun enrichPush(repository: GitScmProviderRepository, hook: GitPushHook): GitPushHook {
        if (hook.action == EventAction.DELETE || hook.after == ZERO_SHA) return hook

        val needsCommit = hook.commit?.message?.isNotBlank() != true
        // Commit entries are a useful fallback, but GitLab may truncate them. Compare the full range when authorized.
        val needsChanges = hook.action == EventAction.PUSH_FILE && hook.before != ZERO_SHA
        if (!needsCommit && !needsChanges) return hook

        return GitlabApiTemplate.execute(repository, apiFactory) { repo, api ->
            val changes = if (needsChanges) {
                val compare = api.commitsApi.compare(repo.projectIdOrPath, hook.before, hook.after, true)
                check(!compare.isCompareTimeout) { "GitLab commit comparison timed out; complete changes are unavailable" }
                compare.diffs.orEmpty().map(GitlabObjectConverter::convertChange)
            } else {
                hook.changes
            }

            val commit = if (needsCommit) {
                GitlabObjectConverter.convertCommit(api.commitsApi.getCommit(repo.projectIdOrPath, hook.after))
            } else {
                hook.commit
            }
            hook.copy(commit = commit, link = if (needsCommit) commit?.link.orEmpty() else hook.link, changes = changes)
        }
    }

    private fun enrichIssue(repository: GitScmProviderRepository, hook: IssueHook): IssueHook {
        hook.extras.putAll(fillDefaultBranchVars(repository, hook, false))
        return hook
    }

    private fun enrichComment(repository: GitScmProviderRepository, hook: AbstractCommentHook): AbstractCommentHook {
        hook.extras.putAll(fillDefaultBranchVars(repository, hook, true))
        if (hook is PullRequestCommentHook) {
            hook.pullRequest?.let { pullRequest ->
                hook.extras.putAll(fillPullRequestVars(repository, pullRequest, hook.repo))
                // These legacy variables intentionally mean source and target branch for comment events.
                hook.extras[PIPELINE_GIT_HEAD_REF] = pullRequest.sourceRef.name
                hook.extras[PIPELINE_GIT_BASE_REF] = pullRequest.targetRef.name
            }
        }
        return hook
    }

    private fun enrichTag(repository: GitScmProviderRepository, hook: GitTagHook): GitTagHook {
        if (hook.action == EventAction.DELETE) return hook
        GitlabApiTemplate.execute(repository, apiFactory) { repo, api ->
            val tag = api.tagsApi.getTag(repo.projectIdOrPath, hook.ref.name)
            hook.extras[PIPELINE_GIT_TAG_DESC] = tag.message.orEmpty()
        }
        return hook
    }

    private fun fillPullRequestVars(
        repository: GitScmProviderRepository,
        pullRequest: PullRequest,
        webhookRepo: GitScmServerRepository
    ): Map<String, Any> {
        if (pullRequest.number <= 0) return emptyMap()
        return GitlabApiTemplate.execute(repository, apiFactory) { repo, api ->
            val mr = api.mergeRequestsApi.getMergeRequest(repo.projectIdOrPath, pullRequest.number.toLong())
            pullRequest.title = mr.title.orEmpty().ifBlank { pullRequest.title }
            GitlabObjectToMapConverter.convertPullRequest(mr, webhookRepo)
        }
    }

    private fun fillDefaultBranchVars(
        repository: GitScmProviderRepository,
        webhook: Webhook,
        includeCommitMessage: Boolean
    ): Map<String, String> {
        val defaultBranch = (webhook.repository() as GitScmServerRepository).defaultBranch ?: return emptyMap()
        return GitlabApiTemplate.execute(repository, apiFactory) { repo, api ->
            val commit = api.commitsApi.getCommit(repo.projectIdOrPath, defaultBranch)
            mutableMapOf(
                PIPELINE_GIT_REF to defaultBranch,
                CI_BRANCH to defaultBranch,
                PIPELINE_WEBHOOK_BRANCH to defaultBranch,
                PIPELINE_GIT_COMMIT_AUTHOR to commit.authorName.orEmpty(),
                PIPELINE_GIT_SHA to commit.id.orEmpty(),
                PIPELINE_GIT_SHA_SHORT to commit.shortId.orEmpty().ifBlank { commit.id.orEmpty().take(8) }
            ).apply {
                if (includeCommitMessage) put(PIPELINE_GIT_COMMIT_MESSAGE, commit.message.orEmpty())
            }
        }
    }

    companion object {
        private const val ZERO_SHA = "0000000000000000000000000000000000000000"
    }
}
