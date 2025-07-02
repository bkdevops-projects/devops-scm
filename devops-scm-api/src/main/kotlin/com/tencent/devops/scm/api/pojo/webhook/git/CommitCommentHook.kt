package com.tencent.devops.scm.api.pojo.webhook.git

import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_COMMIT_AUTHOR
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_COMMIT_MESSAGE
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_SHA
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_SHA_SHORT
import com.tencent.devops.scm.api.enums.EventAction
import com.tencent.devops.scm.api.pojo.Comment
import com.tencent.devops.scm.api.pojo.Commit
import com.tencent.devops.scm.api.pojo.User
import com.tencent.devops.scm.api.pojo.repository.git.GitScmServerRepository
import com.tencent.devops.scm.api.util.GitUtils

data class CommitCommentHook(
    override val action: EventAction,
    override val repo: GitScmServerRepository,
    override val eventType: String,
    override val comment: Comment,
    override val sender: User,
    override var extras: MutableMap<String, Any> = mutableMapOf(),
    val commit: Commit
) : AbstractCommentHook(
    action = action,
    repo = repo,
    eventType = eventType,
    comment = comment,
    sender = sender,
    extras = extras
) {
    companion object {
        const val CLASS_TYPE = "commit_comment"
    }

    override fun outputs(): Map<String, Any> {
        val outputs = super.outputs().toMutableMap()
        outputs[PIPELINE_GIT_COMMIT_AUTHOR] = commit.committer?.name ?: ""
        outputs[PIPELINE_GIT_SHA] = commit.sha
        outputs[PIPELINE_GIT_SHA_SHORT] = GitUtils.getShortSha(commit.sha)
        outputs[PIPELINE_GIT_COMMIT_MESSAGE] = commit.message
        if (extras.isNotEmpty()) {
            outputs.putAll(extras)
        }
        return outputs
    }
}
