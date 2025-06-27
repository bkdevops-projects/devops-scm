package com.tencent.devops.scm.api.pojo.webhook.git

import com.tencent.devops.scm.api.constant.WebhookI18Code.GIT_PUSH_CREATE_EVENT_DESC
import com.tencent.devops.scm.api.constant.WebhookI18Code.GIT_PUSH_DELETE_EVENT_DESC
import com.tencent.devops.scm.api.constant.WebhookI18Code.GIT_PUSH_EVENT_DESC
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_BRANCH
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_COMMIT_ID
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_PUSH_AFTER_COMMIT
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_PUSH_BEFORE_COMMIT
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_PUSH_PROJECT_ID
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_PUSH_TOTAL_COMMIT
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_PUSH_USERNAME
import com.tencent.devops.scm.api.constant.WebhookOutputCode.CI_BRANCH
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_ACTION
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_BEFORE_SHA
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_BEFORE_SHA_SHORT
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_COMMIT_AUTHOR
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_COMMIT_MESSAGE
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_EVENT
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_EVENT_URL
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_REF
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_REPO
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_REPO_GROUP
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_REPO_ID
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_REPO_NAME
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_REPO_URL
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_SHA
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_SHA_SHORT
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_REPO_NAME
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_START_WEBHOOK_USER_ID
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_WEBHOOK_BRANCH
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_WEBHOOK_COMMIT_MESSAGE
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_WEBHOOK_EVENT_TYPE
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_WEBHOOK_REVISION
import com.tencent.devops.scm.api.enums.EventAction
import com.tencent.devops.scm.api.pojo.Change
import com.tencent.devops.scm.api.pojo.Commit
import com.tencent.devops.scm.api.pojo.ScmI18Variable
import com.tencent.devops.scm.api.pojo.User
import com.tencent.devops.scm.api.pojo.repository.git.GitScmServerRepository
import com.tencent.devops.scm.api.pojo.webhook.Webhook
import com.tencent.devops.scm.api.util.GitUtils

/**
 * Git推送事件
 */
data class GitPushHook(
    val action: EventAction,
    val ref: String,
    val baseRef: String? = null,
    val repo: GitScmServerRepository,
    override val eventType: String,
    val before: String,
    val after: String,
    val commit: Commit,
    val link: String,
    val sender: User,
    val commits: List<Commit> = emptyList(),
    val changes: List<Change> = emptyList(),
    val totalCommitsCount: Int? = null,
    val extras: Map<String, Any>? = null,
    val outputCommitIndexVar: Boolean? = null,
    val skipCi: Boolean = false
) : Webhook {

    companion object {
        const val CLASS_TYPE = "git_push"
    }

    override fun repository() = repo

    override val userName = sender.name

    override val eventDesc = ScmI18Variable(
        code = getI18Code(),
        params = listOf(
            GitUtils.trimRef(ref),
            link,
            GitUtils.getShortSha(commit.sha),
            sender.name
        )
    )

    override fun outputs(): Map<String, Any> {
        val outputParams = mutableMapOf<String, Any>()
        // 通用变量
        outputParams[PIPELINE_WEBHOOK_REVISION] = commit.sha
        outputParams[PIPELINE_REPO_NAME] = repo.fullName
        outputParams[PIPELINE_START_WEBHOOK_USER_ID] = sender.name
        outputParams[PIPELINE_WEBHOOK_EVENT_TYPE] = eventType
        outputParams[PIPELINE_WEBHOOK_COMMIT_MESSAGE] = commit.message
        outputParams[PIPELINE_WEBHOOK_BRANCH] = ref

        // 传统变量
        outputParams[BK_REPO_GIT_WEBHOOK_PUSH_USERNAME] = sender.name
        outputParams[BK_REPO_GIT_WEBHOOK_PUSH_BEFORE_COMMIT] = before
        outputParams[BK_REPO_GIT_WEBHOOK_PUSH_AFTER_COMMIT] = after
        outputParams[BK_REPO_GIT_WEBHOOK_PUSH_TOTAL_COMMIT] = totalCommitsCount ?: commits.size
        outputParams[BK_REPO_GIT_WEBHOOK_PUSH_PROJECT_ID] = repo.id
        outputParams[BK_REPO_GIT_WEBHOOK_BRANCH] = ref
        outputParams[BK_REPO_GIT_WEBHOOK_COMMIT_ID] = commit.sha

        // CI上下文变量
        outputParams[PIPELINE_GIT_REPO_ID] = repo.id
        outputParams[PIPELINE_GIT_REPO] = repo.fullName
        outputParams[PIPELINE_GIT_REPO_NAME] = repo.name
        outputParams[PIPELINE_GIT_REPO_GROUP] = repo.group
        outputParams[PIPELINE_GIT_REPO_URL] = repo.httpUrl
        outputParams[PIPELINE_GIT_REF] = "refs/heads/$ref"
        outputParams[CI_BRANCH] = ref
        outputParams[PIPELINE_GIT_EVENT] = if (action == EventAction.DELETE) "delete" else "push"

        commits.firstOrNull { it.sha == after }?.author?.let {
            outputParams[PIPELINE_GIT_COMMIT_AUTHOR] = it.name
        }

        outputParams[PIPELINE_GIT_BEFORE_SHA] = before
        outputParams[PIPELINE_GIT_BEFORE_SHA_SHORT] = GitUtils.getShortSha(before)
        outputParams[PIPELINE_GIT_ACTION] = action.value
        outputParams[PIPELINE_GIT_EVENT_URL] = commit.link
        outputParams[PIPELINE_GIT_COMMIT_MESSAGE] = commit.message
        outputParams[PIPELINE_GIT_SHA_SHORT] = GitUtils.getShortSha(commit.sha)
        outputParams[PIPELINE_GIT_SHA] = commit.sha

        outputCommitIndexVar?.takeIf { it }?.let {
            outputParams.putAll(GitUtils.getOutputCommitIndexVar(commits))
        }

        return outputParams
    }

    override fun skipCi(): Boolean = skipCi

    private fun getI18Code(): String = when (action) {
        EventAction.NEW_BRANCH -> GIT_PUSH_CREATE_EVENT_DESC
        EventAction.DELETE -> GIT_PUSH_DELETE_EVENT_DESC
        else -> GIT_PUSH_EVENT_DESC
    }
}
