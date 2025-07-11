package com.tencent.devops.scm.api.pojo.webhook.svn

import com.tencent.devops.scm.api.constant.WebhookI18Code
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_SVN_WEBHOOK_COMMIT_TIME
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_SVN_WEBHOOK_REVERSION
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_SVN_WEBHOOK_USERNAME
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_WEBHOOK_COMMIT_MESSAGE
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_WEBHOOK_EVENT_TYPE
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_WEBHOOK_REVISION
import com.tencent.devops.scm.api.pojo.Change
import com.tencent.devops.scm.api.pojo.ScmI18Variable
import com.tencent.devops.scm.api.pojo.User
import com.tencent.devops.scm.api.pojo.repository.svn.SvnScmServerRepository
import com.tencent.devops.scm.api.pojo.webhook.Webhook

/**
 * SVN提交后钩子
 */
data class PostCommitHook(
    val repo: SvnScmServerRepository,
    val changes: List<Change>,
    val message: String,
    val sender: User,
    override val eventType: String,
    val extras: MutableMap<String, Any> = mutableMapOf(),
    val revision: Long,
    val commitTime: Long,
    val skipCi: Boolean = false
) : Webhook {

    companion object {
        const val CLASS_TYPE = "post_commit"
    }

    override fun repository() = repo

    override val userName = sender.name

    override val eventDesc = ScmI18Variable(
        code = WebhookI18Code.SVN_POST_COMMIT,
        params = listOf(revision.toString(), sender.name)
    )

    override fun outputs(): Map<String, Any> {
        val outputParams = mutableMapOf<String, Any>()
        outputParams[BK_REPO_SVN_WEBHOOK_REVERSION] = revision
        outputParams[BK_REPO_SVN_WEBHOOK_USERNAME] = sender.name
        outputParams[BK_REPO_SVN_WEBHOOK_COMMIT_TIME] = commitTime
        outputParams[PIPELINE_WEBHOOK_COMMIT_MESSAGE] = message
        outputParams[PIPELINE_WEBHOOK_REVISION] = revision
        outputParams[PIPELINE_WEBHOOK_EVENT_TYPE] = eventType
        if (extras.isNotEmpty()) {
            outputParams.putAll(extras)
        }
        return outputParams
    }

    override fun skipCi(): Boolean = skipCi
}
