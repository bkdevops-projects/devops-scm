package com.tencent.devops.scm.provider.git.bkcode

import com.tencent.devops.scm.api.WebhookParser
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_MANUAL_UNLOCK
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_ISSUE_STATE
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_ACTION
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_BEFORE_SHA
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_BEFORE_SHA_SHORT
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_TAG_MESSAGE
import com.tencent.devops.scm.api.enums.EventAction
import com.tencent.devops.scm.api.enums.ReviewState
import com.tencent.devops.scm.api.pojo.Commit
import com.tencent.devops.scm.api.pojo.HookRequest
import com.tencent.devops.scm.api.pojo.Reference
import com.tencent.devops.scm.api.pojo.Review
import com.tencent.devops.scm.api.pojo.repository.git.GitRepositoryUrl
import com.tencent.devops.scm.api.pojo.repository.git.GitScmServerRepository
import com.tencent.devops.scm.api.pojo.webhook.Webhook
import com.tencent.devops.scm.api.pojo.webhook.git.GitPushHook
import com.tencent.devops.scm.api.pojo.webhook.git.GitTagHook
import com.tencent.devops.scm.api.pojo.webhook.git.IssueCommentHook
import com.tencent.devops.scm.api.pojo.webhook.git.IssueHook
import com.tencent.devops.scm.api.pojo.webhook.git.PullRequestCommentHook
import com.tencent.devops.scm.api.pojo.webhook.git.PullRequestHook
import com.tencent.devops.scm.api.pojo.webhook.git.PullRequestReviewHook
import com.tencent.devops.scm.api.util.GitUtils
import com.tencent.devops.scm.sdk.bkcode.BkCodeConstants.SIGNATURE_HEADER
import com.tencent.devops.scm.sdk.bkcode.BkCodeConstants.SIGNATURE_PARAM_PREFIX
import com.tencent.devops.scm.sdk.bkcode.enums.BkCodeEventType
import com.tencent.devops.scm.sdk.bkcode.enums.BkCodeEventType.ISSUES
import com.tencent.devops.scm.sdk.bkcode.enums.BkCodeEventType.MERGE_REQUEST
import com.tencent.devops.scm.sdk.bkcode.enums.BkCodeEventType.NOTE
import com.tencent.devops.scm.sdk.bkcode.enums.BkCodeEventType.PUSH
import com.tencent.devops.scm.sdk.bkcode.enums.BkCodeEventType.TAG_PUSH
import com.tencent.devops.scm.sdk.bkcode.enums.BkCodeEventType.fromValue
import com.tencent.devops.scm.sdk.bkcode.enums.BkCodeMergeRequestStatus.CAN_BE_MERGED
import com.tencent.devops.scm.sdk.bkcode.enums.BkCodeNoteableType
import com.tencent.devops.scm.sdk.bkcode.enums.BkCodeReviewAction
import com.tencent.devops.scm.sdk.bkcode.enums.BkCodeReviewAction.SUBMIT_REVIEW
import com.tencent.devops.scm.sdk.bkcode.enums.BkCodeReviewState.REJECTED
import com.tencent.devops.scm.sdk.bkcode.pojo.webhook.BkCodeEvent
import com.tencent.devops.scm.sdk.bkcode.pojo.webhook.BkCodeIssueEvent
import com.tencent.devops.scm.sdk.bkcode.pojo.webhook.BkCodeMergeRequestEvent
import com.tencent.devops.scm.sdk.bkcode.pojo.webhook.BkCodeNoteEvent
import com.tencent.devops.scm.sdk.bkcode.pojo.webhook.BkCodePushEvent
import com.tencent.devops.scm.sdk.bkcode.pojo.webhook.BkCodeTagPushEvent
import com.tencent.devops.scm.sdk.bkcode.utils.BkCodeHookVerifyUtil
import com.tencent.devops.scm.sdk.common.util.DateUtils
import com.tencent.devops.scm.sdk.common.util.ScmJsonUtil
import org.slf4j.LoggerFactory

/**
 * BkCode Webhook 解析器实现类
 */
class BkCodeWebhookParser : WebhookParser {

    override fun parse(request: HookRequest): Webhook? {
        logger.info("try to parse BkCode webhook")
        return when (fromValue(request.headers?.get("x-bkcode-event") ?: "")) {
            PUSH -> parsePushHook(request.body)
            TAG_PUSH -> parseTagHook(request.body)
            MERGE_REQUEST -> parsePullRequestHook(request.body)
            ISSUES -> parseIssueHook(request.body)
            NOTE -> parseCommentHook(request.body)
            else -> null
        }
    }

    override fun verify(request: HookRequest, secretToken: String?): Boolean {
        if (secretToken.isNullOrEmpty()) {
            return true
        }
        val signature = request.headers?.get(SIGNATURE_HEADER)
        if (signature.isNullOrBlank()) {
            logger.warn("BkCode webhook signature is empty")
            return false
        }
        if (!signature.startsWith(SIGNATURE_PARAM_PREFIX)) {
            logger.warn("BkCode webhook signature is not start with $SIGNATURE_PARAM_PREFIX")
            return false
        }
        val verifySignature = BkCodeHookVerifyUtil.verifySignature(
            request.body,
            secretToken,
            signature
        )
        if (!verifySignature) {
            logger.warn("BkCode webhook signature verify failed")
        }
        return verifySignature
    }

    private fun parsePushHook(body: String): GitPushHook {
        val src = ScmJsonUtil.fromJson(body, BkCodePushEvent::class.java)
        return convertPushHook(src)
    }

    private fun parseTagHook(body: String): GitTagHook {
        val src = ScmJsonUtil.fromJson(body, BkCodeTagPushEvent::class.java)
        return with(src) {
            val (action, sha) = if (created) {
                EventAction.CREATE to after
            } else {
                EventAction.DELETE to before
            }
            val repo = BkCodeObjectConverter.convertRepository(repository)

            val refName = GitUtils.trimRef(ref)
            val (commit, linkUrl) = when (action) {
                EventAction.DELETE -> {
                    // 删除标签时展示删除前的tag提交点
                    Commit(sha = before, message = "") to repo.webUrl
                }

                EventAction.CREATE -> {
                    Commit(sha = after, message = "") to "${repo.webUrl}/-/tree/$refName/-/%2F"
                }

                else -> null to ""
            }

            val ref = Reference(
                name = refName,
                sha = sha,
                linkUrl = linkUrl
            )

            GitTagHook(
                ref = ref,
                repo = repo,
                eventType = TAG_PUSH.name,
                action = action,
                sender = getSender(this),
                commit = commit,
                extras = fillTagExtra(src),
                createFrom = GitUtils.getShortSha(sha)
            )
        }
    }

    private fun parsePullRequestHook(body: String): Webhook {
        val src = ScmJsonUtil.fromJson(body, BkCodeMergeRequestEvent::class.java)
        // review 事件动作区分处理
        if (BkCodeReviewAction.values().any { it.value == src.action }) {
            return parsePullRequestReviewHook(src)
        }
        val action = when (src.action) {
            "created" -> EventAction.OPEN
            "closed" -> EventAction.CLOSE
            "reopened" -> EventAction.REOPEN
            "merged" -> EventAction.MERGE
            "synchronize" -> EventAction.PUSH_UPDATE
            else -> EventAction.EDIT
        }

        val extras = BkCodeObjectToMapConverter.convertMergeRequestEvent(src)

        val targetRepository = src.mergeRequest.targetRepository
        val targetRepositoryUrl = GitRepositoryUrl(targetRepository.httpUrl)
        val repo = GitScmServerRepository(
            id = targetRepository.id,
            group = targetRepositoryUrl.group,
            name = targetRepository.name,
            fullName = targetRepositoryUrl.fullName,
            httpUrl = targetRepository.httpUrl,
            sshUrl = targetRepository.sshUrl,
            webUrl = targetRepository.httpUrl
        )
        val user = BkCodeObjectConverter.convertUser(src.sender)
        val pullRequest = BkCodeObjectConverter.convertPullRequest(user, src.mergeRequest)
        // TODO: 完善commit
        // val commit = BkCodeObjectConverter.convertCommit(src.commits.first())
        return PullRequestHook(
            action = action,
            repo = repo,
            eventType = MERGE_REQUEST.name,
            pullRequest = pullRequest,
            sender = getSender(src),
            commit = Commit(sha = "", message = ""),
            extras = extras,
            changes = listOf()
        )
    }

    private fun parseIssueHook(body: String): IssueHook {
        val src = ScmJsonUtil.fromJson(body, BkCodeIssueEvent::class.java)
        val eventIssue = src.issue
        val action = when (src.action) {
            "created" -> EventAction.OPEN
            "reopened" -> EventAction.REOPEN
            "edited" -> EventAction.UPDATE
            "closed" -> EventAction.CLOSE
            else -> EventAction.OPEN
        }
        return IssueHook(
            repo = BkCodeObjectConverter.convertRepository(src.repository),
            eventType = ISSUES.name,
            action = action,
            issue = BkCodeObjectConverter.convertIssue(eventIssue),
            sender = getSender(src),
            extras = HashMap<String, Any>().apply {
                put(BK_REPO_GIT_WEBHOOK_ISSUE_STATE, eventIssue.state)
                put(BK_REPO_GIT_MANUAL_UNLOCK, false)
            }
        )
    }

    /**
     * 解析 pull request review
     */
    private fun parsePullRequestReviewHook(src: BkCodeMergeRequestEvent): PullRequestReviewHook {
        logger.info("parse pull request review")
        val sender = BkCodeObjectConverter.convertUser(src.sender)
        val mergeRequest = src.mergeRequest
        val pullRequest = BkCodeObjectConverter.convertPullRequest(sender, mergeRequest)
        val mergeStatus = src.mergeRequest.mergeStatus
        val state = when {
            // 提交评审状态，并且合并状态为can_be_merged
            mergeStatus == CAN_BE_MERGED.value -> ReviewState.APPROVED

            // 代码要求修改
            src.action == SUBMIT_REVIEW.value -> if (src.review.state == REJECTED.name) {
                ReviewState.CHANGE_REQUIRED
            } else {
                ReviewState.APPROVING
            }

            else -> ReviewState.APPROVING
        }
        return PullRequestReviewHook(
            action = EventAction.CREATE,
            repo = BkCodeObjectConverter.convertRepository(src.repository),
            eventType = BkCodeEventType.REVIEW.name,
            pullRequest = pullRequest,
            sender = sender,
            extras = mutableMapOf(),
            review = Review(
                id = mergeRequest.id,
                iid = mergeRequest.number,
                state = state,
                link = pullRequest.link,
                title = pullRequest.title,
                reviewers = mergeRequest.reviewers?.map { BkCodeObjectConverter.convertUser(it) },
                sourceBranch = pullRequest.sourceRef.name,
                targetBranch = pullRequest.targetRef.name,
                sourceProjectId = pullRequest.sourceRepo.id.toString(),
                targetProjectId = pullRequest.targetRepo.id.toString()
            )
        )
    }

    @SuppressWarnings("LongMethod")
    private fun parseCommentHook(body: String): Webhook? {
        val src = ScmJsonUtil.fromJson(body, BkCodeNoteEvent::class.java)
        val repository = src.repository
        val httpUrl = repository.httpUrl
        val repositoryUrl = GitRepositoryUrl(httpUrl)
        // BkCode note repository返回的url是ssh协议
        val repo = GitScmServerRepository(
            id = repository.id,
            group = repositoryUrl.group,
            name = repositoryUrl.name,
            fullName = repositoryUrl.fullName,
            httpUrl = httpUrl,
            webUrl = repository.httpUrl,
            sshUrl = repository.sshUrl
        )

        val sender = getSender(src)
        val comment = BkCodeObjectConverter.convertComment(src.comment, BkCodeNoteableType.ISSUE)
        val extra = BkCodeObjectToMapConverter.convertNoteEvent(src)
        return when {
            src.issue != null -> {
                val issue = BkCodeObjectConverter.convertIssue(src.issue)
                IssueCommentHook(
                    eventType = NOTE.name,
                    issue = issue,
                    action = EventAction.CREATE,
                    comment = comment,
                    repo = repo,
                    sender = sender,
                    extras = extra
                )
            }

            src.mergeRequest != null -> {
                val pullRequest = BkCodeObjectConverter.convertPullRequest(sender, src.mergeRequest)
                extra.putAll(
                    BkCodeObjectToMapConverter.convertMergeRequestEvent(repo, src.mergeRequest)
                )
                PullRequestCommentHook(
                    eventType = NOTE.name,
                    pullRequest = pullRequest,
                    action = EventAction.CREATE,
                    comment = comment,
                    repo = repo,
                    sender = sender,
                    extras = extra
                )
            }

            else -> null
        }
    }

    @SuppressWarnings("LongMethod", "CyclomaticComplexMethod")
    private fun convertPushHook(src: BkCodePushEvent): GitPushHook {
        val action = when {
            src.created -> EventAction.NEW_BRANCH
            src.deleted -> EventAction.DELETE
            else -> EventAction.PUSH_FILE
        }
        val repository = BkCodeObjectConverter.convertRepository(src.repository)
        val commit = src.headCommit?.let {
            BkCodeObjectConverter.convertCommit(it).copy(
                commitTime = DateUtils.convertDateToLocalDateTime(it.timestamp)
            )
        } ?: src.after?.let {
            Commit(
                sha = it,
                message = "",
                link = getCommitLink(it, repository)
            )
        }
        val extras = mutableMapOf<String, Any>()
        extras[BK_REPO_GIT_MANUAL_UNLOCK] = false
        extras[PIPELINE_GIT_ACTION] = action.value
        val ref = GitUtils.trimRef(src.ref)
        val link = when (action) {
            EventAction.NEW_BRANCH -> "${repository.webUrl}/tree/$ref"
            EventAction.DELETE -> repository.webUrl
            else -> getCommitLink(commit?.sha ?: src.after, repository)
        }
        return GitPushHook(
            action = action,
            ref = ref,
            eventType = PUSH.name,
            repo = repository,
            before = src.before,
            after = src.after,
            commit = commit,
            link = link,
            sender = getSender(src),
            commits = src.commits.map(BkCodeObjectConverter::convertCommit),
            changes = emptyList(),
            totalCommitsCount = src.commits.size,
            extras = extras,
            outputCommitIndexVar = true,
            skipCi = false
        )
    }

    private fun getSender(event: BkCodeEvent) = with(event) {
        BkCodeObjectConverter.convertUser(sender)
    }

    private fun fillTagExtra(src: BkCodeTagPushEvent): MutableMap<String, Any> {
        val params = mutableMapOf<String, Any>()
        params[PIPELINE_GIT_BEFORE_SHA] = src.before
        params[PIPELINE_GIT_BEFORE_SHA_SHORT] = GitUtils.getShortSha(src.before)
        src.tag?.let {
            params[PIPELINE_GIT_TAG_MESSAGE] = it.message ?: ""
        }
        return params
    }

    private fun getCommitLink(commitSha: String, repository: GitScmServerRepository) =
        "${repository.webUrl}/-/commit/$commitSha"

    companion object {
        private val logger = LoggerFactory.getLogger(BkCodeWebhookParser::class.java)
    }
}
