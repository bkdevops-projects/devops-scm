package com.tencent.devops.scm.api.pojo.webhook.git

import com.tencent.devops.scm.api.constant.WebhookI18Code.GIT_TAG_DELETE_EVENT_DESC
import com.tencent.devops.scm.api.constant.WebhookI18Code.GIT_TAG_PUSH_EVENT_DESC
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_BRANCH
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_COMMIT_ID
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_TAG_NAME
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_TAG_USERNAME
import com.tencent.devops.scm.api.constant.WebhookOutputCode.CI_BRANCH
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_ACTION
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_EVENT
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_EVENT_URL
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_REF
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
import com.tencent.devops.scm.api.enums.EventAction
import com.tencent.devops.scm.api.pojo.Commit
import com.tencent.devops.scm.api.pojo.Reference
import com.tencent.devops.scm.api.pojo.ScmI18Variable
import com.tencent.devops.scm.api.pojo.User
import com.tencent.devops.scm.api.pojo.repository.git.GitScmServerRepository
import com.tencent.devops.scm.api.pojo.webhook.Webhook

/**
 * Git标签事件
 */
data class GitTagHook(
    val ref: Reference,
    val repo: GitScmServerRepository,
    override var eventType: String,
    val action: EventAction,
    val sender: User,
    val commit: Commit?,
    val createFrom: String? = null,
    val extras: MutableMap<String, Any> = mutableMapOf()
) : Webhook {

    companion object {
        const val CLASS_TYPE = "git_tag"
    }

    override fun repository(): GitScmServerRepository = repo

    override val userName = sender.name

    override val eventDesc = ScmI18Variable(
        code = if (action == EventAction.DELETE) {
            GIT_TAG_DELETE_EVENT_DESC
        } else {
            GIT_TAG_PUSH_EVENT_DESC
        },
        params = listOf(
            "$createFrom", ref.linkUrl, ref.name, sender.name
        )
    )

    override fun outputs(): Map<String, Any> {
        val outputParams = mutableMapOf<String, Any>()
        // 通用变量
        val tagVersion = commit?.sha ?: ""
        outputParams[PIPELINE_WEBHOOK_REVISION] = tagVersion
        outputParams[PIPELINE_REPO_NAME] = repo.fullName
        outputParams[PIPELINE_START_WEBHOOK_USER_ID] = sender.username
        outputParams[PIPELINE_WEBHOOK_EVENT_TYPE] = eventType
        outputParams[PIPELINE_WEBHOOK_COMMIT_MESSAGE] = ref.name
        outputParams[PIPELINE_WEBHOOK_BRANCH] = ref.name

        outputParams[BK_REPO_GIT_WEBHOOK_TAG_NAME] = ref.name
        outputParams[BK_REPO_GIT_WEBHOOK_TAG_USERNAME] = sender.name
        outputParams[BK_REPO_GIT_WEBHOOK_BRANCH] = ref.name
        outputParams[BK_REPO_GIT_WEBHOOK_COMMIT_ID] = tagVersion

        outputParams[PIPELINE_GIT_REPO_ID] = repo.id
        outputParams[PIPELINE_GIT_REPO_URL] = repo.httpUrl
        outputParams[PIPELINE_GIT_REPO] = repo.fullName
        outputParams[PIPELINE_GIT_REPO_NAME] = repo.name
        outputParams[PIPELINE_GIT_REPO_GROUP] = repo.group
        outputParams[PIPELINE_GIT_REF] = "refs/tags/${ref.name}"
        outputParams[PIPELINE_GIT_SHA] = tagVersion
        outputParams[CI_BRANCH] = ref.name
        outputParams[PIPELINE_GIT_EVENT] = if (action == EventAction.DELETE) "delete" else "tag_push"
        outputParams[PIPELINE_GIT_EVENT_URL] = ref.linkUrl
        outputParams[PIPELINE_GIT_ACTION] = action.value
        outputParams[PIPELINE_START_WEBHOOK_USER_ID] = sender.name

        if (extras.isNotEmpty()) {
            outputParams.putAll(extras)
        }
        return outputParams
    }
}
