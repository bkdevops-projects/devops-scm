package com.tencent.devops.scm.api.pojo.webhook.git

import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_REVIEW_ID
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_REVIEW_IID
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_REVIEW_SOURCE_BRANCH
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_REVIEW_SOURCE_COMMIT
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_REVIEW_SOURCE_PROJECT_ID
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_REVIEW_STATE
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_REVIEW_TARGET_BRANCH
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_REVIEW_TARGET_COMMIT
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_REVIEW_TARGET_PROJECT_ID
import com.tencent.devops.scm.api.enums.EventAction
import com.tencent.devops.scm.api.pojo.Comment
import com.tencent.devops.scm.api.pojo.PullRequest
import com.tencent.devops.scm.api.pojo.Review
import com.tencent.devops.scm.api.pojo.User
import com.tencent.devops.scm.api.pojo.repository.git.GitScmServerRepository

data class PullRequestCommentHook(
    override val action: EventAction,
    override val repo: GitScmServerRepository,
    override val eventType: String,
    override val comment: Comment,
    override val sender: User,
    override var extras: MutableMap<String, Any> = mutableMapOf(),
    val pullRequest: PullRequest? = null,
    val review: Review? = null
) : AbstractCommentHook(
    action = action,
    repo = repo,
    eventType = eventType,
    comment = comment,
    sender = sender,
    extras = extras
) {
    companion object {
        const val CLASS_TYPE = "issue_comment"
    }

    override val userName = sender.name

    override fun outputs(): Map<String, Any> {
        val outputs = super.outputs().toMutableMap()

        // 处理review信息
        review?.let {
            outputs[BK_REPO_GIT_WEBHOOK_REVIEW_STATE] = it.state?.value ?: ""
            outputs[BK_REPO_GIT_WEBHOOK_REVIEW_ID] = it.id
            outputs[BK_REPO_GIT_WEBHOOK_REVIEW_IID] = it.iid
            outputs[BK_REPO_GIT_WEBHOOK_REVIEW_SOURCE_BRANCH] = it.sourceBranch ?: ""
            outputs[BK_REPO_GIT_WEBHOOK_REVIEW_SOURCE_PROJECT_ID] = it.sourceProjectId ?: ""
            outputs[BK_REPO_GIT_WEBHOOK_REVIEW_SOURCE_COMMIT] = it.sourceCommit ?: ""
            outputs[BK_REPO_GIT_WEBHOOK_REVIEW_TARGET_COMMIT] = it.targetCommit ?: ""
            outputs[BK_REPO_GIT_WEBHOOK_REVIEW_TARGET_BRANCH] = it.targetBranch ?: ""
            outputs[BK_REPO_GIT_WEBHOOK_REVIEW_TARGET_PROJECT_ID] = it.targetProjectId ?: ""
        }

        if (extras.isNotEmpty()) {
            outputs.putAll(extras)
        }
        return outputs
    }
}
