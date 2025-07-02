package com.tencent.devops.scm.provider.git.gitee

import com.tencent.devops.scm.api.WebhookParser
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_MR_ACTION
import com.tencent.devops.scm.api.enums.EventAction
import com.tencent.devops.scm.api.enums.EventAction.OPEN
import com.tencent.devops.scm.api.pojo.Commit
import com.tencent.devops.scm.api.pojo.HookRequest
import com.tencent.devops.scm.api.pojo.PullRequest
import com.tencent.devops.scm.api.pojo.webhook.Webhook
import com.tencent.devops.scm.api.pojo.webhook.git.GitPushHook
import com.tencent.devops.scm.api.pojo.webhook.git.PullRequestHook
import com.tencent.devops.scm.api.util.GitUtils
import com.tencent.devops.scm.provider.git.gitee.enums.GiteeEventType
import com.tencent.devops.scm.sdk.common.util.DateUtils
import com.tencent.devops.scm.sdk.common.util.ScmJsonUtil
import com.tencent.devops.scm.sdk.gitee.pojo.webhook.GiteePullRequestHook
import com.tencent.devops.scm.sdk.gitee.pojo.webhook.GiteePushHook
import kotlin.Pair

class GiteeWebhookParser : WebhookParser {

    companion object {
        // 忽略的pull request动作, Gitee 新建PR时会瞬间发送三个webhook动作，对无效的webhook进行过滤
        private val IGNORED_PULL_REQUEST_ACTION = listOf(
            Pair(OPEN.value, "test"),
            Pair(OPEN.value, "assign")
        )
    }

    override fun verify(request: HookRequest, secretToken: String?) = true

    override fun parse(request: HookRequest): Webhook? {
        val eventType = request.headers?.get("X-Gitee-Event")
        return when (eventType) {
            "Push Hook" -> parsePushHook(request.body)
            "Merge Request Hook" -> parsePullRequestHook(request.body)
            else -> null
        }
    }

    private fun parsePushHook(body: String): GitPushHook {
        val giteePushHook = ScmJsonUtil.fromJson(body, GiteePushHook::class.java)
        val repository = GiteeObjectConverter.convertRepository(giteePushHook.repository)
        val action = when {
            giteePushHook.created -> EventAction.NEW_BRANCH
            giteePushHook.deleted -> EventAction.DELETE
            else -> EventAction.PUSH_FILE
        }

        val headCommit = giteePushHook.headCommit
        var commit = headCommit?.let { GiteeObjectConverter.convertCommit(it) }
        if (action == EventAction.DELETE && commit == null) {
            commit = Commit(
                sha = giteePushHook.before,
                message = ""
            )
        }

        val ref = GitUtils.trimRef(giteePushHook.ref)
        val link = when (action) {
            EventAction.NEW_BRANCH -> "${repository.webUrl}/tree/$ref"
            EventAction.DELETE -> repository.webUrl
            else -> commit?.link ?: ""
        }

        return GitPushHook(
            action = action,
            ref = ref,
            repo = repository,
            eventType = GiteeEventType.PUSH.name,
            before = giteePushHook.before,
            after = giteePushHook.after,
            commit = commit,
            link = link,
            sender = GiteeObjectConverter.convertUser(giteePushHook.sender),
            commits = giteePushHook.commits?.map { GiteeObjectConverter.convertCommit(it) } ?: listOf(),
            totalCommitsCount = giteePushHook.totalCommitsCount?.toInt() ?: 0,
            extras = mutableMapOf()
        )
    }

    private fun parsePullRequestHook(body: String): PullRequestHook {
        val giteePullRequestHook = ScmJsonUtil.fromJson(body, GiteePullRequestHook::class.java)
        val repository = GiteeObjectConverter.convertRepository(giteePullRequestHook.repository)
        val sourcePullRequest = giteePullRequestHook.pullRequest
        val head = sourcePullRequest.head

        val pullRequest = PullRequest(
            id = sourcePullRequest.id,
            number = sourcePullRequest.number.toInt(),
            sourceRepo = GiteeObjectConverter.convertRepository(giteePullRequestHook.sourceRepo.repository),
            sourceRef = GiteeObjectConverter.convertRef(head),
            targetRef = GiteeObjectConverter.convertRef(sourcePullRequest.base),
            targetRepo = repository,
            title = sourcePullRequest.title,
            body = sourcePullRequest.body,
            description = sourcePullRequest.body,
            link = sourcePullRequest.htmlUrl,
            mergeCommitSha = sourcePullRequest.mergeCommitSha,
            merged = sourcePullRequest.merged,
            author = GiteeObjectConverter.convertAuthor(sourcePullRequest.user),
            created = DateUtils.convertDateToLocalDateTime(sourcePullRequest.createdAt),
            updated = DateUtils.convertDateToLocalDateTime(sourcePullRequest.updatedAt),
            labels = sourcePullRequest.labels.map { it.name },
            milestone = GiteeObjectConverter.convertMilestone(sourcePullRequest.milestone),
            assignee = sourcePullRequest.assignees.map { GiteeObjectConverter.convertAuthor(it) },
            baseCommit = sourcePullRequest.base.sha
        )

        val hookAction = giteePullRequestHook.action
        val hookActionDesc = giteePullRequestHook.actionDesc
        val skipCi = isIgnorePullRequestAction(hookAction, hookActionDesc)
        val extras = mutableMapOf<String, Any>()
        var action = GiteeObjectConverter.convertAction(hookAction, hookActionDesc)
        
        // 汉化需要, [更新] → [编辑]
        if (action == EventAction.UPDATE) {
            action = EventAction.EDIT
        }
        extras[PIPELINE_GIT_MR_ACTION] = action

        return PullRequestHook(
            repo = repository,
            action = action,
            eventType = GiteeEventType.MERGE_REQUEST.name,
            pullRequest = pullRequest,
            sender = GiteeObjectConverter.convertUser(giteePullRequestHook.sender),
            commit = Commit(sha = head.sha, message = ""),
            extras = extras,
            skipCi = skipCi,
            changes = listOf()
        )
    }

    private fun isIgnorePullRequestAction(action: String, actionDesc: String): Boolean {
        return IGNORED_PULL_REQUEST_ACTION.any { it.first == action && it.second == actionDesc }
    }
}
