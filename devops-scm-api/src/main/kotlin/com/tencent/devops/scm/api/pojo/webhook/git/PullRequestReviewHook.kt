package com.tencent.devops.scm.api.pojo.webhook.git

import com.tencent.devops.scm.api.constant.WebhookI18Code.GIT_REVIEW_APPROVED_EVENT_DESC
import com.tencent.devops.scm.api.constant.WebhookI18Code.GIT_REVIEW_APPROVING_EVENT_DESC
import com.tencent.devops.scm.api.constant.WebhookI18Code.GIT_REVIEW_CHANGE_DENIED_EVENT_DESC
import com.tencent.devops.scm.api.constant.WebhookI18Code.GIT_REVIEW_CHANGE_REQUIRED_EVENT_DESC
import com.tencent.devops.scm.api.constant.WebhookI18Code.GIT_REVIEW_CLOSED_EVENT_DESC
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_REVIEW_ID
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_REVIEW_IID
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_EVENT
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_EVENT_URL
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_REPO
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_REPO_GROUP
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_REPO_ID
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_REPO_NAME
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_REPO_URL
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_REPO_NAME
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_WEBHOOK_COMMIT_MESSAGE
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_WEBHOOK_EVENT_TYPE
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_WEBHOOK_REVISION
import com.tencent.devops.scm.api.enums.EventAction
import com.tencent.devops.scm.api.enums.ReviewState
import com.tencent.devops.scm.api.pojo.PullRequest
import com.tencent.devops.scm.api.pojo.Review
import com.tencent.devops.scm.api.pojo.ScmI18Variable
import com.tencent.devops.scm.api.pojo.User
import com.tencent.devops.scm.api.pojo.repository.git.GitScmServerRepository
import com.tencent.devops.scm.api.pojo.webhook.Webhook

/**
 * 代码评审事件
 */
data class PullRequestReviewHook(
    var action: EventAction,
    var repo: GitScmServerRepository,
    override val eventType: String,
    var pullRequest: PullRequest? = null,
    var review: Review,
    var sender: User,
    // 扩展属性,提供者额外补充需要输出的变量
    var extras: MutableMap<String, Any> = mutableMapOf()
) : Webhook {

    companion object {
        const val CLASS_TYPE = "pull_request_review"
    }

    override fun repository() = repo

    override val userName = sender.name

    override val eventDesc: ScmI18Variable
        get() {
            val params = listOf(
                review.link,
                review.iid.toString(),
                userName
            )
            return ScmI18Variable(
                code = getI18Code(review.state),
                params = params
            )
        }

    override fun outputs(): Map<String, Any> {
        val outputParams = mutableMapOf<String, Any>()
        outputParams[BK_REPO_GIT_WEBHOOK_REVIEW_ID] = review.id.toString()
        outputParams[BK_REPO_GIT_WEBHOOK_REVIEW_IID] = review.iid.toString()

        outputParams[PIPELINE_WEBHOOK_EVENT_TYPE] = eventType
        outputParams[PIPELINE_GIT_EVENT_URL] = review.link
        outputParams[PIPELINE_GIT_REPO_URL] = repo.httpUrl
        outputParams[PIPELINE_GIT_EVENT] = "review"
        (pullRequest?.title ?: review.title).let {
            if (it.isBlank()) {
                outputParams[PIPELINE_WEBHOOK_COMMIT_MESSAGE] = it
            }
        }
        outputParams[PIPELINE_WEBHOOK_REVISION] = ""
        outputParams[PIPELINE_GIT_REPO_ID] = repo.id
        outputParams[PIPELINE_REPO_NAME] = repo.fullName
        outputParams[PIPELINE_GIT_REPO] = repo.fullName
        outputParams[PIPELINE_GIT_REPO_NAME] = repo.name
        outputParams[PIPELINE_GIT_REPO_GROUP] = repo.group
        if (extras.isNotEmpty()) {
            outputParams.putAll(extras)
        }
        return outputParams
    }

    private fun getI18Code(state: ReviewState?): String = when (state) {
        ReviewState.APPROVING -> GIT_REVIEW_APPROVING_EVENT_DESC
        ReviewState.APPROVED -> GIT_REVIEW_APPROVED_EVENT_DESC
        ReviewState.CHANGE_REQUIRED -> GIT_REVIEW_CHANGE_REQUIRED_EVENT_DESC
        ReviewState.CHANGE_DENIED -> GIT_REVIEW_CHANGE_DENIED_EVENT_DESC
        ReviewState.CLOSED -> GIT_REVIEW_CLOSED_EVENT_DESC
        else -> ""
    }
}
