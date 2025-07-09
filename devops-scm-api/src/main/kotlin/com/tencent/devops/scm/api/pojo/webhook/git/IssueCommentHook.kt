package com.tencent.devops.scm.api.pojo.webhook.git

import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_ISSUE_DESCRIPTION
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_ISSUE_ID
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_ISSUE_IID
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_ISSUE_MILESTONE_ID
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_ISSUE_OWNER
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_ISSUE_STATE
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_ISSUE_TITLE
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_ISSUE_URL
import com.tencent.devops.scm.api.enums.EventAction
import com.tencent.devops.scm.api.pojo.Comment
import com.tencent.devops.scm.api.pojo.Issue
import com.tencent.devops.scm.api.pojo.User
import com.tencent.devops.scm.api.pojo.repository.git.GitScmServerRepository

data class IssueCommentHook(
    override val action: EventAction,
    override val repo: GitScmServerRepository,
    override val eventType: String,
    override val comment: Comment,
    override val sender: User,
    override var extras: MutableMap<String, Any> = mutableMapOf(),
    val issue: Issue
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
        outputs[BK_REPO_GIT_WEBHOOK_ISSUE_TITLE] = issue.title
        outputs[BK_REPO_GIT_WEBHOOK_ISSUE_ID] = issue.id
        outputs[BK_REPO_GIT_WEBHOOK_ISSUE_IID] = issue.number
        outputs[BK_REPO_GIT_WEBHOOK_ISSUE_DESCRIPTION] = issue.body ?: ""
        outputs[BK_REPO_GIT_WEBHOOK_ISSUE_STATE] = issue.state ?: ""
        outputs[BK_REPO_GIT_WEBHOOK_ISSUE_OWNER] = sender.name
        outputs[BK_REPO_GIT_WEBHOOK_ISSUE_URL] = issue.link
        outputs[BK_REPO_GIT_WEBHOOK_ISSUE_MILESTONE_ID] = issue.milestoneId ?: ""
        if (extras.isNotEmpty()) {
            outputs.putAll(extras)
        }
        return outputs
    }
}
