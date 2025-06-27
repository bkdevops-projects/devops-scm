package com.tencent.devops.scm.api.pojo.webhook.git

import com.tencent.devops.scm.api.constant.DateFormatConstants
import com.tencent.devops.scm.api.constant.WebhookI18Code.GIT_NOTE_EVENT_DESC
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_NOTE_AUTHOR_ID
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_NOTE_COMMENT
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_NOTE_CREATED_AT
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_NOTE_ID
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_NOTE_NOTEABLE_TYPE
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_NOTE_UPDATED_AT
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_NOTE_URL
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
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_WEBHOOK_NOTE_COMMENT
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_WEBHOOK_NOTE_ID
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_WEBHOOK_REVISION
import com.tencent.devops.scm.api.enums.EventAction
import com.tencent.devops.scm.api.pojo.Comment
import com.tencent.devops.scm.api.pojo.ScmI18Variable
import com.tencent.devops.scm.api.pojo.User
import com.tencent.devops.scm.api.pojo.repository.git.GitScmServerRepository
import com.tencent.devops.scm.api.pojo.webhook.Webhook
import java.text.SimpleDateFormat

/**
 * 抽象评论钩子基类
 */
abstract class AbstractCommentHook(
    open val action: EventAction,
    open val repo: GitScmServerRepository,
    override val eventType: String,
    open val comment: Comment,
    open val sender: User,
    // 扩展属性,提供者额外补充需要输出的变量
    open val extras: Map<String, Any> = emptyMap()
) : Webhook {

    override fun repository(): GitScmServerRepository = repo

    override val userName = sender.name

    override val eventDesc = ScmI18Variable(
        code = GIT_NOTE_EVENT_DESC,
        params = listOf(
            comment.link,
            comment.id.toString(),
            sender.name
        )
    )

    override fun outputs(): Map<String, Any> {
        val outputParams = mutableMapOf<String, Any>()
        outputParams[PIPELINE_WEBHOOK_EVENT_TYPE] = eventType
        outputParams[PIPELINE_WEBHOOK_COMMIT_MESSAGE] = comment.body
        outputParams[PIPELINE_WEBHOOK_NOTE_COMMENT] = comment.body
        outputParams[BK_REPO_GIT_WEBHOOK_NOTE_COMMENT] = comment.body
        outputParams[PIPELINE_REPO_NAME] = repo.fullName
        outputParams[PIPELINE_START_WEBHOOK_USER_ID] = sender.username

        outputParams[BK_REPO_GIT_WEBHOOK_NOTE_AUTHOR_ID] = sender.id
        outputParams[BK_REPO_GIT_WEBHOOK_NOTE_ID] = comment.id
        outputParams[PIPELINE_WEBHOOK_NOTE_ID] = comment.id
        outputParams[BK_REPO_GIT_WEBHOOK_NOTE_NOTEABLE_TYPE] = comment.type
        outputParams[BK_REPO_GIT_WEBHOOK_NOTE_URL] = comment.link
        outputParams[BK_REPO_GIT_WEBHOOK_NOTE_CREATED_AT] =
            SimpleDateFormat(DateFormatConstants.ISO_8601).format(comment.created)
        outputParams[BK_REPO_GIT_WEBHOOK_NOTE_UPDATED_AT] =
            SimpleDateFormat(DateFormatConstants.ISO_8601).format(comment.updated)
        outputParams[PIPELINE_GIT_EVENT] = "note"
        outputParams[PIPELINE_GIT_EVENT_URL] = comment.link
        outputParams[PIPELINE_GIT_REPO_ID] = repo.id
        outputParams[PIPELINE_GIT_REPO] = repo.fullName
        outputParams[PIPELINE_GIT_REPO_NAME] = repo.name
        outputParams[PIPELINE_GIT_REPO_GROUP] = repo.group
        outputParams[PIPELINE_GIT_REPO_URL] = repo.httpUrl
        outputParams[PIPELINE_WEBHOOK_REVISION] = ""

        return outputParams
    }
}
