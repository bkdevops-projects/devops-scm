package com.tencent.devops.scm.provider.git.tgit

import com.tencent.devops.scm.api.WebhookEnricher
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_SOURCE_BRANCH
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_TARGET_BRANCH
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_PUSH_OPERATION_KIND
import com.tencent.devops.scm.api.constant.WebhookOutputCode.CI_BRANCH
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_BASE_REF
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_COMMIT_AUTHOR
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_COMMIT_MESSAGE
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_HEAD_REF
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_MR_TAPD_ISSUES
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_REF
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_SHA
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_SHA_SHORT
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_TAG_DESC
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_WEBHOOK_BRANCH
import com.tencent.devops.scm.api.pojo.PullRequest
import com.tencent.devops.scm.api.pojo.repository.ScmProviderRepository
import com.tencent.devops.scm.api.pojo.repository.git.GitScmProviderRepository
import com.tencent.devops.scm.api.pojo.repository.git.GitScmServerRepository
import com.tencent.devops.scm.api.pojo.webhook.Webhook
import com.tencent.devops.scm.api.pojo.webhook.git.AbstractCommentHook
import com.tencent.devops.scm.api.pojo.webhook.git.CommitCommentHook
import com.tencent.devops.scm.api.pojo.webhook.git.GitPushHook
import com.tencent.devops.scm.api.pojo.webhook.git.GitTagHook
import com.tencent.devops.scm.api.pojo.webhook.git.IssueCommentHook
import com.tencent.devops.scm.api.pojo.webhook.git.IssueHook
import com.tencent.devops.scm.api.pojo.webhook.git.PullRequestCommentHook
import com.tencent.devops.scm.api.pojo.webhook.git.PullRequestHook
import com.tencent.devops.scm.api.pojo.webhook.git.PullRequestReviewHook
import com.tencent.devops.scm.sdk.tgit.TGitApiException
import com.tencent.devops.scm.sdk.tgit.TGitApiFactory
import com.tencent.devops.scm.sdk.tgit.enums.TGitPushOperationKind.UPDATE_NONFASTFORWORD
import com.tencent.devops.scm.sdk.tgit.enums.TGitTapdWorkType
import com.tencent.devops.scm.sdk.tgit.pojo.TGitCommit
import org.slf4j.LoggerFactory

@SuppressWarnings("TooManyFunctions")
class TGitWebhookEnricher(private val apiFactory: TGitApiFactory) : WebhookEnricher {

    private val eventActions: MutableMap<Class<out Webhook>, (GitScmProviderRepository, Webhook) -> Unit> =
        mutableMapOf()

    companion object {
        private val logger = LoggerFactory.getLogger(TGitApiException::class.java)
    }

    init {
        eventActions[PullRequestHook::class.java] = this::enrichPullRequestHook
        eventActions[GitPushHook::class.java] = this::enrichPushHook
        eventActions[IssueHook::class.java] = this::enrichIssueHook
        eventActions[PullRequestReviewHook::class.java] = this::enrichPullRequestViewHook
        eventActions[CommitCommentHook::class.java] = this::enrichNoteHook
        eventActions[IssueCommentHook::class.java] = this::enrichNoteHook
        eventActions[PullRequestCommentHook::class.java] = this::enrichNoteHook
        eventActions[GitTagHook::class.java] = this::enrichTagHook
    }

    override fun enrich(repository: ScmProviderRepository, webhook: Webhook): Webhook {
        val repo = repository as GitScmProviderRepository
        enrichServerRepository(repo, webhook)
        eventActions[webhook.javaClass]?.invoke(repo, webhook)
        return webhook
    }

    private fun enrichServerRepository(repo: GitScmProviderRepository, webhook: Webhook) {
        TGitApiTemplate.execute(repo, apiFactory) { _, tGitApi ->
            val project = tGitApi.projectApi.getProject(repo.projectIdOrPath)
            val serverRepository = webhook.repository() as GitScmServerRepository
            serverRepository.defaultBranch = project.defaultBranch
            serverRepository.archived = project.archived
        }
    }

    private fun enrichPullRequestHook(repository: GitScmProviderRepository, webhook: Webhook) {
        TGitApiTemplate.execute(repository, apiFactory) { _, tGitApi ->
            val pullRequestHook = webhook as PullRequestHook
            val pullRequest = pullRequestHook.pullRequest
            val mergeRequest = tGitApi.mergeRequestApi
                    .getMergeRequestChanges(
                        repository.projectIdOrPath,
                        pullRequest.id
                    )

            mergeRequest?.let {
                pullRequestHook.changes = it.files.map(TGitObjectConverter::convertChange)
            }
            // 根据完整的MR/Review信息填充变量
            pullRequestHook.extras.putAll(
                fillPullRequestReviewVars(
                    repository,
                    pullRequest,
                    webhook.repo
                )
            )
        }
    }

    private fun enrichPushHook(repository: GitScmProviderRepository, webhook: Webhook) {
        TGitApiTemplate.execute(repository, apiFactory) { _, tGitApi ->
            val pushHook = webhook as GitPushHook
            val operationKind = pushHook.extras[BK_REPO_GIT_WEBHOOK_PUSH_OPERATION_KIND] as String?

            if (UPDATE_NONFASTFORWORD.value == operationKind) {
                pushHook.changes = (tGitApi.repositoryFileApi.getFileChange(
                    repository.projectIdOrPath,
                    pushHook.after,
                    pushHook.before,
                    false,
                    false,
                    1,
                    1000
                ) ?: listOf()).map(TGitObjectConverter::convertChange)
            }
        }
    }

    private fun enrichIssueHook(repository: GitScmProviderRepository, webhook: Webhook) {
        val issueHook = webhook as IssueHook
        issueHook.extras.putAll(
            fillDefaultBranchVars(repository, webhook, null)
        )
    }

    private fun enrichNoteHook(repository: GitScmProviderRepository, webhook: Webhook) {
        val commentHook = webhook as AbstractCommentHook

        val extras = commentHook.extras
        val defaultBranchVars = fillDefaultBranchVars(
            repository,
            webhook
        ) { _, commit ->
            extras[PIPELINE_GIT_COMMIT_MESSAGE] = commit.message ?: ""
        }

        extras.putAll(defaultBranchVars)

        if (commentHook is PullRequestCommentHook) {
            commentHook.pullRequest?.let {
                extras.putAll(
                    fillPullRequestReviewVars(
                        repository,
                        it,
                        (webhook as PullRequestCommentHook).repo
                    )
                )
            }

        }
        commentHook.extras = extras
    }

    private fun enrichTagHook(repository: GitScmProviderRepository, webhook: Webhook) {
        val tagHook = webhook as GitTagHook
        tagHook.extras.putAll(
            fillTagVars(
                repository,
                tagHook.ref.name
            )
        )
    }

    private fun enrichPullRequestViewHook(repository: GitScmProviderRepository, webhook: Webhook) {
        val reviewHook = webhook as PullRequestReviewHook
        val pullRequest = reviewHook.pullRequest
        val extras = reviewHook.extras

        if (pullRequest != null) {
            extras.putAll(
                fillPullRequestReviewVars(
                    repository,
                    pullRequest,
                    webhook.repo
                )
            )
        } else {
            TGitApiTemplate.execute(repository, apiFactory) { _, tGitApi ->
                val review = reviewHook.review
                val commitReview = tGitApi.reviewApi.getCommitReview(
                    repository.projectIdOrPath,
                    review.id
                )
                review.title = commitReview.title ?: ""
            }
        }

        val defaultBranchVars = fillDefaultBranchVars(repository, webhook) { _, commit ->
            extras[PIPELINE_GIT_COMMIT_MESSAGE] = commit.message ?: ""
        }

        extras.putAll(defaultBranchVars)
    }

    private fun fillPullRequestVars(
        repository: GitScmProviderRepository,
        pullRequest: PullRequest,
        scmServerRepository: GitScmServerRepository
    ): Map<String, Any> {
        val outputs = mutableMapOf<String, Any>()
        TGitApiTemplate.execute(repository, apiFactory) { _, tGitApi ->
            val mergeRequestInfo = tGitApi.mergeRequestApi.getMergeRequestById(
                repository.projectIdOrPath,
                pullRequest.id
            )

            mergeRequestInfo?.let {
                pullRequest.title = it.title ?: ""
                outputs.putAll(TGitObjectToMapConverter.convertPullRequest(it, scmServerRepository))
            }
        }
        return outputs
    }

    private fun fillPullRequestReviewVars(
        repository: GitScmProviderRepository,
        pullRequest: PullRequest,
        scmServerRepository: GitScmServerRepository
    ): Map<String, Any> {
        val vars = mutableMapOf<String, Any>().apply {
            putAll(
                fillPullRequestVars(
                    repository,
                    pullRequest,
                    scmServerRepository
                )
            )
        }

        TGitApiTemplate.execute(repository, apiFactory) { _, tGitApi ->
            val tGitMergeRequestReviewInfo = tGitApi.reviewApi.getMergeRequestReview(
                repository.projectIdOrPath,
                pullRequest.id
            )

            tGitMergeRequestReviewInfo?.let {
                vars.putAll(TGitObjectToMapConverter.convertReview(it))
            }
        }
        return vars
    }

    private fun fillDefaultBranchVars(
        repository: GitScmProviderRepository,
        webhook: Webhook,
        extAction: ((String, TGitCommit) -> Unit)?
    ): Map<String, String> {
        val extra = mutableMapOf<String, String>()
        enrichDefaultBranchAndCommitInfo(
            repository,
            webhook
        ) { defaultBranch, commit ->
            if (defaultBranch != null && commit != null) {
                extra[PIPELINE_GIT_REF] = defaultBranch
                extra[CI_BRANCH] = defaultBranch
                extra[PIPELINE_WEBHOOK_BRANCH] = defaultBranch
                extra[PIPELINE_GIT_COMMIT_AUTHOR] = commit.authorName
                extra[PIPELINE_GIT_SHA] = commit.id
                extra[PIPELINE_GIT_SHA_SHORT] = commit.shortId
                // 额外字段，不同事件默认分支参数不同有差异，例如：Note 和 Issue
                extAction?.invoke(defaultBranch, commit)
            }
        }
        return extra
    }

    private fun enrichDefaultBranchAndCommitInfo(
        repository: GitScmProviderRepository,
        webhook: Webhook,
        action: (String?, TGitCommit?) -> Unit
    ) {
        TGitApiTemplate.execute(repository, apiFactory) { _, tGitApi ->
            val serverRepository = webhook.repository() as GitScmServerRepository
            val defaultBranch = serverRepository.defaultBranch
            var commit: TGitCommit? = null
            if (defaultBranch != null) {
                commit = tGitApi.commitsApi.getCommit(repository.projectIdOrPath, defaultBranch)
            }
            action.invoke(defaultBranch, commit)
        }
    }

    private fun fillTapdWorkItemsVar(
        repository: GitScmProviderRepository,
        type: TGitTapdWorkType,
        iid: Long
    ): Map<String, String> {
        val extra = mutableMapOf<String, String>()
        TGitApiTemplate.execute(repository, apiFactory) { _, tGitApi ->
            val tapdWorkItems = tGitApi.projectApi.getTapdWrorkItems(
                repository.projectIdOrPath,
                type,
                iid,
                1,
                100
            )
            val tapdIds = tapdWorkItems?.joinToString(separator = ",") { it.tapdId.toString() }

            if (!tapdIds.isNullOrBlank() && type == TGitTapdWorkType.MR) {
                extra[PIPELINE_GIT_MR_TAPD_ISSUES] = tapdIds
            }
        }
        return extra
    }

    private fun fillTagVars(repository: GitScmProviderRepository, tagName: String): Map<String, String> {
        val extra = mutableMapOf<String, String>()
        TGitApiTemplate.execute(repository, apiFactory) { _, tGitApi ->
            val tag = tGitApi.tagsApi.getTag(
                repository.projectIdOrPath,
                tagName
            )
            tag?.let {
                extra[PIPELINE_GIT_TAG_DESC] = it.description
            }
        }
        return extra
    }
}
