package com.tencent.devops.scm.api.pojo.webhook.git

import com.tencent.devops.scm.api.constant.WebhookI18Code.GIT_PR_CLOSED_EVENT_DESC
import com.tencent.devops.scm.api.constant.WebhookI18Code.GIT_PR_CREATED_EVENT_DESC
import com.tencent.devops.scm.api.constant.WebhookI18Code.GIT_PR_MERGED_EVENT_DESC
import com.tencent.devops.scm.api.constant.WebhookI18Code.GIT_PR_PUSH_UPDATED_EVENT_DESC
import com.tencent.devops.scm.api.constant.WebhookI18Code.GIT_PR_REOPENED_EVENT_DESC
import com.tencent.devops.scm.api.constant.WebhookI18Code.GIT_PR_UPDATED_EVENT_DESC
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_GIT_MR_NUMBER
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_HOOK_MR_ID
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_BRANCH
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_COMMIT_ID
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_DESCRIPTION
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_ID
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_LAST_COMMIT
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_LAST_COMMIT_MSG
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_MERGE_COMMIT_SHA
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_MERGE_TYPE
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_NUMBER
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_SOURCE_BRANCH
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_SOURCE_URL
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_TARGET_URL
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_URL
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_ACTION
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_BASE_REF
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_BASE_REPO_URL
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_COMMIT_AUTHOR
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_COMMIT_MESSAGE
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_EVENT
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_EVENT_URL
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_HEAD_REPO_URL
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_MR_ACTION
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_MR_DESC
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_MR_URL
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_REPO
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_REPO_GROUP
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_REPO_ID
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_REPO_NAME
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_REPO_URL
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_SHA
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_REPO_NAME
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_START_WEBHOOK_USER_ID
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_WEBHOOK_BRANCH
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_WEBHOOK_COMMIT_MESSAGE
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_WEBHOOK_EVENT_TYPE
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_WEBHOOK_REVISION
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_WEBHOOK_SOURCE_REPO_NAME
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_WEBHOOK_SOURCE_URL
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_WEBHOOK_TARGET_REPO_NAME
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_WEBHOOK_TARGET_URL
import com.tencent.devops.scm.api.constant.WebhookOutputConstants.PR_DESC_MAX_LENGTH
import com.tencent.devops.scm.api.enums.EventAction
import com.tencent.devops.scm.api.pojo.Change
import com.tencent.devops.scm.api.pojo.Commit
import com.tencent.devops.scm.api.pojo.PullRequest
import com.tencent.devops.scm.api.pojo.ScmI18Variable
import com.tencent.devops.scm.api.pojo.User
import com.tencent.devops.scm.api.pojo.repository.git.GitScmServerRepository
import com.tencent.devops.scm.api.pojo.webhook.Webhook
import org.apache.commons.lang3.StringUtils

/**
 * PR/MR 事件
 */
data class PullRequestHook(
    val action: EventAction,
    val repo: GitScmServerRepository,
    override val eventType: String,
    val pullRequest: PullRequest,
    // PR 创建者
    val sender: User,
    // PR源分支最后的commit信息
    val commit: Commit,
    // 变更的文件路径
    var changes: List<Change>,
    // 扩展属性,提供者额外补充需要输出的变量
    val extras: MutableMap<String, Any> = mutableMapOf(),
    val skipCi: Boolean = false
) : Webhook {

    companion object {
        const val CLASS_TYPE = "pull_request"
    }

    override fun skipCi(): Boolean = skipCi

    override fun repository() = repo

    override val userName: String
        get() = sender.username

    override val eventDesc: ScmI18Variable
        get() = ScmI18Variable(
            code = getI18Code(),
            params = listOf(
                pullRequest.link,
                pullRequest.number.toString(),
                userName
            )
        )

    override fun outputs(): Map<String, Any> {
        val outputParams = mutableMapOf<String, Any>()

        outputParams[BK_GIT_MR_NUMBER] = pullRequest.number
        outputParams[BK_HOOK_MR_ID] = pullRequest.id
        outputParams[BK_REPO_GIT_WEBHOOK_BRANCH] = if (action == EventAction.MERGE) {
            pullRequest.targetRef.name
        } else {
            pullRequest.sourceRef.name
        }
        outputParams[BK_REPO_GIT_WEBHOOK_COMMIT_ID] = commit.sha
        outputParams[BK_REPO_GIT_WEBHOOK_MR_LAST_COMMIT] = commit.sha
        outputParams[BK_REPO_GIT_WEBHOOK_MR_LAST_COMMIT_MSG] = commit.message
        outputParams[BK_REPO_GIT_WEBHOOK_MR_MERGE_COMMIT_SHA] = pullRequest.mergeCommitSha ?: ""
        outputParams[BK_REPO_GIT_WEBHOOK_MR_MERGE_TYPE] = pullRequest.mergeType ?: ""
        outputParams[BK_REPO_GIT_WEBHOOK_MR_SOURCE_URL] = pullRequest.sourceRepo.httpUrl
        outputParams[BK_REPO_GIT_WEBHOOK_MR_TARGET_URL] = pullRequest.targetRepo.httpUrl
        outputParams[BK_REPO_GIT_WEBHOOK_MR_URL] = pullRequest.link
        outputParams[BK_REPO_GIT_WEBHOOK_MR_ID] = pullRequest.id
        outputParams[BK_REPO_GIT_WEBHOOK_MR_NUMBER] = pullRequest.number
        outputParams[BK_REPO_GIT_WEBHOOK_MR_DESCRIPTION] = StringUtils.substring(
            pullRequest.description ?: "",
            0,
            PR_DESC_MAX_LENGTH
        )
        outputParams[BK_REPO_GIT_WEBHOOK_MR_SOURCE_BRANCH] = pullRequest.sourceRef.name

        outputParams[PIPELINE_GIT_REPO] = repo.fullName
        outputParams[PIPELINE_GIT_REPO_NAME] = repo.name
        outputParams[PIPELINE_GIT_REPO_GROUP] = repo.group
        outputParams[PIPELINE_GIT_ACTION] = action.value
        outputParams[PIPELINE_GIT_BASE_REF] = pullRequest.sourceRef.name
        outputParams[PIPELINE_GIT_BASE_REPO_URL] = pullRequest.sourceRepo.httpUrl
        outputParams[PIPELINE_GIT_COMMIT_AUTHOR] = commit.author?.name ?: ""
        outputParams[PIPELINE_GIT_COMMIT_MESSAGE] = commit.message
        outputParams[PIPELINE_GIT_EVENT] = eventType.lowercase()
        outputParams[PIPELINE_GIT_EVENT_URL] = pullRequest.link
        outputParams[PIPELINE_GIT_HEAD_REPO_URL] = pullRequest.targetRepo.httpUrl
        outputParams[PIPELINE_GIT_MR_ACTION] = action.value
        outputParams[PIPELINE_GIT_MR_DESC] = pullRequest.description ?: ""
        outputParams[PIPELINE_GIT_MR_URL] = pullRequest.link
        outputParams[PIPELINE_GIT_REPO_ID] = repo.id
        outputParams[PIPELINE_GIT_REPO_URL] = repo.httpUrl
        outputParams[PIPELINE_GIT_SHA] = commit.sha
        outputParams[PIPELINE_REPO_NAME] = repo.fullName
        outputParams[PIPELINE_START_WEBHOOK_USER_ID] = sender.username
        outputParams[PIPELINE_WEBHOOK_BRANCH] = pullRequest.targetRef.name
        outputParams[PIPELINE_WEBHOOK_COMMIT_MESSAGE] = pullRequest.title
        outputParams[PIPELINE_WEBHOOK_EVENT_TYPE] = eventType
        outputParams[PIPELINE_WEBHOOK_REVISION] = commit.sha
        outputParams[PIPELINE_WEBHOOK_SOURCE_REPO_NAME] = pullRequest.sourceRepo.fullName
        outputParams[PIPELINE_WEBHOOK_SOURCE_URL] = pullRequest.sourceRepo.httpUrl
        outputParams[PIPELINE_WEBHOOK_TARGET_REPO_NAME] = pullRequest.targetRepo.fullName
        outputParams[PIPELINE_WEBHOOK_TARGET_URL] = pullRequest.targetRepo.httpUrl
        if (extras.isNotEmpty()) {
            outputParams.putAll(extras)
        }
        return outputParams
    }

    private fun getI18Code(): String = when (action) {
        EventAction.OPEN -> GIT_PR_CREATED_EVENT_DESC
        EventAction.CLOSE -> GIT_PR_CLOSED_EVENT_DESC
        EventAction.EDIT -> GIT_PR_UPDATED_EVENT_DESC
        EventAction.PUSH_UPDATE -> GIT_PR_PUSH_UPDATED_EVENT_DESC
        EventAction.REOPEN -> GIT_PR_REOPENED_EVENT_DESC
        EventAction.MERGE -> GIT_PR_MERGED_EVENT_DESC
        else -> ""
    }
}
