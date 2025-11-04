package com.tencent.devops.scm.provider.git.bkcode

import com.tencent.devops.scm.api.WebhookEnricher
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_SOURCE_BRANCH
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_TARGET_BRANCH
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
import com.tencent.devops.scm.api.pojo.webhook.git.IssueCommentHook
import com.tencent.devops.scm.api.pojo.webhook.git.IssueHook
import com.tencent.devops.scm.api.pojo.webhook.git.PullRequestCommentHook
import com.tencent.devops.scm.api.pojo.webhook.git.PullRequestHook
import com.tencent.devops.scm.api.pojo.webhook.git.PullRequestReviewHook
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeCommit
import com.tencent.devops.scm.sdk.bkcode.BkCodeApiException
import com.tencent.devops.scm.sdk.bkcode.BkCodeApiFactory
import com.tencent.devops.scm.sdk.bkcode.enums.BkCodeDiffFileRevRange
import org.slf4j.LoggerFactory

@SuppressWarnings("TooManyFunctions")
class BkCodeWebhookEnricher(private val apiFactory: BkCodeApiFactory) : WebhookEnricher {

    private val eventActions: MutableMap<Class<out Webhook>, (GitScmProviderRepository, Webhook) -> Unit> =
        mutableMapOf()

    companion object {
        private val logger = LoggerFactory.getLogger(BkCodeApiException::class.java)
    }

    init {
        eventActions[PullRequestHook::class.java] = this::enrichPullRequestHook
        eventActions[GitPushHook::class.java] = this::enrichPushHook
        eventActions[IssueHook::class.java] = this::enrichIssueHook
        eventActions[PullRequestReviewHook::class.java] = this::enrichPullRequestViewHook
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
        BkCodeApiTemplate.execute(repo, apiFactory) { _, bkCodeApi ->
            val project = bkCodeApi.projectApi.getProject(repo.projectIdOrPath)
            val serverRepository = webhook.repository() as GitScmServerRepository
            serverRepository.defaultBranch = project.defaultBranch
        }
    }

    private fun enrichPullRequestHook(repository: GitScmProviderRepository, webhook: Webhook) {
        BkCodeApiTemplate.execute(repository, apiFactory) { _, bkCodeApi ->
            val pullRequestHook = webhook as PullRequestHook
            val pullRequest = pullRequestHook.pullRequest
            val mergeRequest = bkCodeApi.mergeRequestApi.getMergeRequestByNumber(
                repository.projectIdOrPath,
                pullRequest.number
            )
            // 对比MR变更信息
            val compare = bkCodeApi.commitApi.compare(
                repository.projectIdOrPath,
                mergeRequest.baseCommitId,
                mergeRequest.headCommitId,
                BkCodeDiffFileRevRange.DOUBLE_DOT,
                false
            )
            compare.diffFiles?.map {
                BkCodeObjectConverter.convertChange(it)
            }?.let {
                pullRequestHook.changes = it
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
        BkCodeApiTemplate.execute(repository, apiFactory) { _, bkCodeApi ->
            val pushHook = webhook as GitPushHook
            if (pushHook.action == EventAction.PUSH_FILE) {
                pushHook.changes = (bkCodeApi.commitApi.compare(
                    repository.projectIdOrPath,
                    pushHook.before,
                    pushHook.after,
                    BkCodeDiffFileRevRange.DOUBLE_DOT,
                    false
                ).diffFiles ?: listOf()).map(BkCodeObjectConverter::convertChange)
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
                // 历史遗留变量问题，同一风格，谨慎修改
                extras[PIPELINE_GIT_HEAD_REF] = commentHook.pullRequest?.sourceRef?.name ?: ""
                extras[PIPELINE_GIT_BASE_REF] = commentHook.pullRequest?.targetRef?.name ?: ""
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
            // 历史遗留问题, Review事件HEAD_REF和BASE_REF分别代表[源分支]和[目标分支], 谨慎修改！！！
            extras[PIPELINE_GIT_HEAD_REF] = extras[BK_REPO_GIT_WEBHOOK_MR_SOURCE_BRANCH] ?: ""
            extras[PIPELINE_GIT_BASE_REF] = extras[BK_REPO_GIT_WEBHOOK_MR_TARGET_BRANCH] ?: ""
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
        BkCodeApiTemplate.execute(repository, apiFactory) { _, bkCodeApi ->
            val mergeRequestInfo = bkCodeApi.mergeRequestApi.getMergeRequestById(
                repository.projectIdOrPath,
                pullRequest.id
            )

            // :TODO 完善参数
            mergeRequestInfo?.let {
                pullRequest.title = it.title ?: ""
                outputs.putAll(BkCodeObjectToMapConverter.convertPullRequest(it, scmServerRepository))
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
        return vars
    }

    private fun fillDefaultBranchVars(
        repository: GitScmProviderRepository,
        webhook: Webhook,
        extAction: ((String, BkCodeCommit) -> Unit)?
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
                extra[PIPELINE_GIT_COMMIT_AUTHOR] = commit.author.name
                extra[PIPELINE_GIT_SHA] = commit.id
                extra[PIPELINE_GIT_SHA_SHORT] = commit.id.substring(0, 8)
                // 额外字段，不同事件默认分支参数不同有差异，例如：Note 和 Issue
                extAction?.invoke(defaultBranch, commit)
            }
        }
        return extra
    }

    private fun enrichDefaultBranchAndCommitInfo(
        repository: GitScmProviderRepository,
        webhook: Webhook,
        action: (String?, BkCodeCommit?) -> Unit
    ) {
        BkCodeApiTemplate.execute(repository, apiFactory) { _, bkCodeApi ->
            val serverRepository = webhook.repository() as GitScmServerRepository
            val defaultBranch = serverRepository.defaultBranch
            var commit: BkCodeCommit? = null
            if (defaultBranch != null) {
                commit = bkCodeApi.commitApi.getCommit(repository.projectIdOrPath, defaultBranch)?.commitInfo
            }
            action.invoke(defaultBranch, commit)
        }
    }


    private fun fillTagVars(repository: GitScmProviderRepository, tagName: String): Map<String, String> {
        val extra = mutableMapOf<String, String>()
        BkCodeApiTemplate.execute(repository, apiFactory) { _, bkCodeApi ->
            val tag = bkCodeApi.tagApi.getTag(
                repository.projectIdOrPath,
                tagName
            )
            tag?.let {
                extra[PIPELINE_GIT_TAG_DESC] = it.desc ?: ""
            }
        }
        return extra
    }
}
