package com.tencent.devops.scm.api.pojo.webhook.git

import com.tencent.devops.scm.api.constant.WebhookI18Code.GIT_ISSUE_CLOSED_EVENT_DESC
import com.tencent.devops.scm.api.constant.WebhookI18Code.GIT_ISSUE_OPENED_EVENT_DESC
import com.tencent.devops.scm.api.constant.WebhookI18Code.GIT_ISSUE_REOPENED_EVENT_DESC
import com.tencent.devops.scm.api.constant.WebhookI18Code.GIT_ISSUE_UPDATED_EVENT_DESC
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_ISSUE_ACTION
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_ISSUE_DESCRIPTION
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_ISSUE_ID
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_ISSUE_IID
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_ISSUE_MILESTONE_ID
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_ISSUE_OWNER
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_ISSUE_TITLE
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_ISSUE_URL
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_ACTION
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_COMMIT_MESSAGE
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_EVENT
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_EVENT_URL
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_REPO
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_REPO_GROUP
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_REPO_ID
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_REPO_NAME
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_REPO_URL
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_REPO_NAME
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_START_WEBHOOK_USER_ID
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_WEBHOOK_COMMIT_MESSAGE
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_WEBHOOK_EVENT_TYPE
import com.tencent.devops.scm.api.enums.EventAction
import com.tencent.devops.scm.api.pojo.Issue
import com.tencent.devops.scm.api.pojo.ScmI18Variable
import com.tencent.devops.scm.api.pojo.User
import com.tencent.devops.scm.api.pojo.repository.git.GitScmServerRepository
import com.tencent.devops.scm.api.pojo.webhook.Webhook

/**
 * Issue事件
 */
data class IssueHook(
    val action: EventAction,
    val repo: GitScmServerRepository,
    override val eventType: String,
    val issue: Issue,
    val sender: User,
    // 扩展属性,提供者额外补充需要输出的变量
    var extras: MutableMap<String, Any> = mutableMapOf()
) : Webhook {

    companion object {
        const val CLASS_TYPE = "issue"
    }

    override fun repository(): GitScmServerRepository = repo

    override val userName = sender.username

    override val eventDesc = ScmI18Variable(
        code = getI18Code(),
        params = listOf(
            issue.link,
            issue.number.toString(),
            userName
        )
    )

    override fun outputs(): Map<String, Any> {
        val outputParams = mutableMapOf<String, Any>()
        outputParams[BK_REPO_GIT_WEBHOOK_ISSUE_TITLE] = issue.title
        outputParams[BK_REPO_GIT_WEBHOOK_ISSUE_ID] = issue.id.toString()
        outputParams[BK_REPO_GIT_WEBHOOK_ISSUE_IID] = issue.number.toString()
        outputParams[BK_REPO_GIT_WEBHOOK_ISSUE_DESCRIPTION] = issue.body ?: ""
        outputParams[BK_REPO_GIT_WEBHOOK_ISSUE_OWNER] = issue.author.name
        outputParams[BK_REPO_GIT_WEBHOOK_ISSUE_URL] = issue.link
        outputParams[BK_REPO_GIT_WEBHOOK_ISSUE_ACTION] = action.value
        outputParams[PIPELINE_WEBHOOK_EVENT_TYPE] = eventType

        outputParams[PIPELINE_GIT_COMMIT_MESSAGE] = issue.title
        outputParams[PIPELINE_GIT_REPO_URL] = repo.httpUrl
        outputParams[PIPELINE_GIT_REPO] = repo.fullName
        outputParams[PIPELINE_GIT_REPO_NAME] = repo.name
        outputParams[PIPELINE_GIT_REPO_GROUP] = repo.group
        outputParams[PIPELINE_GIT_EVENT_URL] = issue.link
        outputParams[PIPELINE_GIT_ACTION] = action.value
        outputParams[PIPELINE_WEBHOOK_COMMIT_MESSAGE] = issue.title
        outputParams[BK_REPO_GIT_WEBHOOK_ISSUE_MILESTONE_ID] = issue.milestoneId ?: ""
        outputParams[PIPELINE_REPO_NAME] = repo.fullName
        outputParams[PIPELINE_GIT_REPO_ID] = repo.id
        outputParams[PIPELINE_START_WEBHOOK_USER_ID] = sender.username
        outputParams[PIPELINE_GIT_EVENT] = "issue"

        if (extras.isNotEmpty()) {
            outputParams.putAll(extras)
        }
        return outputParams
    }

    private fun getI18Code(): String = when (action) {
        EventAction.CREATE -> GIT_ISSUE_OPENED_EVENT_DESC
        EventAction.CLOSE -> GIT_ISSUE_CLOSED_EVENT_DESC
        EventAction.UPDATE -> GIT_ISSUE_UPDATED_EVENT_DESC
        EventAction.REOPEN -> GIT_ISSUE_REOPENED_EVENT_DESC
        else -> ""
    }
}
