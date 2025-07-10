package com.tencent.devops.scm.provider.git.tgit

import com.tencent.devops.scm.api.WebhookParser
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_HOOK_MR_ID
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_MANUAL_UNLOCK
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_ISSUE_STATE
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_PUSH_ACTION_KIND
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_PUSH_OPERATION_KIND
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_PUSH_TOTAL_COMMIT
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_REVIEW_APPROVED_REVIEWERS
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_REVIEW_APPROVING_REVIEWERS
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_REVIEW_OWNER
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_REVIEW_REVIEWABLE_ID
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_REVIEW_REVIEWABLE_TYPE
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_REVIEW_REVIEWERS
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_REVIEW_STATE
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_TAG_CREATE_FROM
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_TAG_OPERATION
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_ACTION
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_BEFORE_SHA
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_BEFORE_SHA_SHORT
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_COMMIT_AUTHOR
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_COMMIT_MESSAGE
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_MR_ACTION
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_MR_URL
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_TAG_FROM
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_TAG_MESSAGE
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_START_WEBHOOK_USER_ID
import com.tencent.devops.scm.api.enums.EventAction
import com.tencent.devops.scm.api.enums.ReviewState
import com.tencent.devops.scm.api.pojo.Change
import com.tencent.devops.scm.api.pojo.Commit
import com.tencent.devops.scm.api.pojo.HookRequest
import com.tencent.devops.scm.api.pojo.PullRequest
import com.tencent.devops.scm.api.pojo.Reference
import com.tencent.devops.scm.api.pojo.Review
import com.tencent.devops.scm.api.pojo.Signature
import com.tencent.devops.scm.api.pojo.User
import com.tencent.devops.scm.api.pojo.repository.git.GitRepositoryUrl
import com.tencent.devops.scm.api.pojo.repository.git.GitScmServerRepository
import com.tencent.devops.scm.api.pojo.webhook.Webhook
import com.tencent.devops.scm.api.pojo.webhook.git.CommitCommentHook
import com.tencent.devops.scm.api.pojo.webhook.git.GitPushHook
import com.tencent.devops.scm.api.pojo.webhook.git.GitTagHook
import com.tencent.devops.scm.api.pojo.webhook.git.IssueCommentHook
import com.tencent.devops.scm.api.pojo.webhook.git.IssueHook
import com.tencent.devops.scm.api.pojo.webhook.git.PullRequestCommentHook
import com.tencent.devops.scm.api.pojo.webhook.git.PullRequestHook
import com.tencent.devops.scm.api.pojo.webhook.git.PullRequestReviewHook
import com.tencent.devops.scm.api.util.GitUtils
import com.tencent.devops.scm.provider.git.tgit.enums.TGitEventType
import com.tencent.devops.scm.sdk.common.util.DateUtils
import com.tencent.devops.scm.sdk.common.util.UrlConverter
import com.tencent.devops.scm.sdk.tgit.enums.TGitNoteableType
import com.tencent.devops.scm.sdk.tgit.enums.TGitPushOperationKind
import com.tencent.devops.scm.sdk.tgit.enums.TGitReviewableType
import com.tencent.devops.scm.sdk.tgit.pojo.webhook.TGitIssueEvent
import com.tencent.devops.scm.sdk.tgit.pojo.webhook.TGitMergeRequestEvent
import com.tencent.devops.scm.sdk.tgit.pojo.webhook.TGitNoteEvent
import com.tencent.devops.scm.sdk.tgit.pojo.webhook.TGitPushEvent
import com.tencent.devops.scm.sdk.tgit.pojo.webhook.TGitReviewEvent
import com.tencent.devops.scm.sdk.tgit.pojo.webhook.TGitTagPushEvent
import com.tencent.devops.scm.sdk.tgit.util.TGitJsonUtil
import org.slf4j.LoggerFactory
import java.util.*

/**
 * TGit Webhook 解析器实现类
 */
class TGitWebhookParser : WebhookParser {

    override fun parse(request: HookRequest): Webhook? {
        logger.info("try to parse tgit webhook")
        return when (request.headers?.get("X-Event")) {
            "Push Hook" -> parsePushHook(request.body)
            "Tag Push Hook" -> parseTagHook(request.body)
            "Merge Request Hook" -> parsePullRequestHook(request.body)
            "Review Hook" -> parsePullRequestReviewHook(request.body)
            "Issue Hook" -> parseIssueHook(request.body)
            "Note Hook" -> parseCommentHook(request.body)
            else -> null
        }
    }

    override fun verify(request: HookRequest, secretToken: String?): Boolean {
        return secretToken.isNullOrEmpty() || secretToken == request.headers?.get("X-Token")
    }

    private fun parsePushHook(body: String): GitPushHook {
        val src = TGitJsonUtil.fromJson(body, TGitPushEvent::class.java)
        return convertPushHook(src)
    }

    private fun parseTagHook(body: String): GitTagHook {
        val src = TGitJsonUtil.fromJson(body, TGitTagPushEvent::class.java)
        return with(src) {
            val action = if (TGitPushOperationKind.DELETE.value == operationKind) {
                EventAction.DELETE
            } else {
                EventAction.CREATE
            }
            val sha = if (action == EventAction.DELETE) before else after
            val createFrom = when {
                action == EventAction.DELETE -> GitUtils.getShortSha(before)
                createFrom.isNullOrBlank() || checkoutSha.isNotBlank() -> GitUtils.getShortSha(checkoutSha)
                else -> createFrom
            }

            val user = User(
                id = userId,
                name = userName,
                email = userEmail,
                username = ""
            )

            val repo = TGitObjectConverter.convertRepository(projectId, repository)

            val refName = GitUtils.trimRef(ref)
            val (commit, linkUrl) = when (action) {
                EventAction.DELETE -> {
                    // 删除标签时展示删除前的tag提交点
                    Commit(sha = before, message = "") to repo.webUrl
                }

                EventAction.CREATE -> {
                    commits.firstOrNull()
                            ?.let {
                                TGitObjectConverter.convertCommit(it)
                            } to "${repo.webUrl}/-/tags/$refName"
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
                eventType = TGitEventType.TAG_PUSH.name,
                action = action,
                sender = user,
                commit = commit,
                extras = fillTagExtra(src).toMutableMap(),
                createFrom = createFrom
            )
        }
    }

    private fun parsePullRequestHook(body: String): PullRequestHook {
        val src = TGitJsonUtil.fromJson(body, TGitMergeRequestEvent::class.java)
        val objectAttributes = src.objectAttributes
        val action = when (objectAttributes.action) {
            "open" -> EventAction.OPEN
            "close" -> EventAction.CLOSE
            "reopen" -> EventAction.REOPEN
            "merge" -> EventAction.MERGE
            "update" -> if ("push-update" == objectAttributes.extensionAction) {
                EventAction.PUSH_UPDATE
            } else {
                EventAction.EDIT
            }

            else -> EventAction.EDIT
        }

        val extras = TGitObjectToMapConverter.convertMergeRequestEvent(src)

        val srcTarget = objectAttributes.target
        val targetRepositoryUrl = GitRepositoryUrl(srcTarget.httpUrl)
        val repo = GitScmServerRepository(
            id = objectAttributes.targetProjectId,
            group = targetRepositoryUrl.group,
            name = srcTarget.name,
            fullName = targetRepositoryUrl.fullName,
            httpUrl = srcTarget.httpUrl,
            sshUrl = srcTarget.sshUrl,
            webUrl = srcTarget.webUrl
        )
        val user = TGitObjectConverter.convertUser(src.user)
        val pullRequest = TGitObjectConverter.convertPullRequest(user, objectAttributes)
        val commit = TGitObjectConverter.convertCommit(objectAttributes.lastCommit)
        return PullRequestHook(
            action = action,
            repo = repo,
            eventType = TGitEventType.MERGE_REQUEST.name,
            pullRequest = pullRequest,
            sender = TGitObjectConverter.convertUser(src.user),
            commit = commit,
            extras = extras,
            changes = listOf()
        )
    }

    private fun parseIssueHook(body: String): IssueHook {
        val src = TGitJsonUtil.fromJson(body, TGitIssueEvent::class.java)
        val objectAttributes = src.objectAttributes

        val action = when (objectAttributes.action) {
            "close" -> EventAction.CLOSE
            "reopen" -> EventAction.REOPEN
            "update" -> EventAction.UPDATE
            else -> EventAction.OPEN
        }

        val repo = TGitObjectConverter.convertRepository(objectAttributes.projectId, src.repository).apply {
            httpUrl = src.repository.url
        }

        val sender = TGitObjectConverter.convertUser(src.user)
        val issue = TGitObjectConverter.convertIssue(sender, objectAttributes)
        val extra = HashMap<String, Any>().apply {
            put(BK_REPO_GIT_WEBHOOK_ISSUE_STATE, objectAttributes.state)
            put(BK_REPO_GIT_MANUAL_UNLOCK, false)
        }

        return IssueHook(
            repo = repo,
            eventType = TGitEventType.ISSUES.name,
            action = action,
            issue = issue,
            sender = sender,
            extras = extra
        )
    }

    @SuppressWarnings("LongMethod")
    private fun parseCommentHook(body: String): Webhook? {
        val src = TGitJsonUtil.fromJson(body, TGitNoteEvent::class.java)
        val objectAttributes = src.objectAttributes

        val tGitRepo = src.repository
        val httpUrl = tGitRepo.realHttpUrl
        val repositoryUrl = GitRepositoryUrl(httpUrl)
        // tgit note repository返回的url是ssh协议
        val repo = GitScmServerRepository(
            id = src.projectId,
            group = repositoryUrl.group,
            name = repositoryUrl.name,
            fullName = repositoryUrl.fullName,
            httpUrl = httpUrl,
            webUrl = tGitRepo.homepage,
            sshUrl = if (tGitRepo.gitSshUrl.isNullOrBlank()) {
                UrlConverter.gitHttp2Ssh(httpUrl)
            } else {
                tGitRepo.gitSshUrl
            }
        )

        val user = TGitObjectConverter.convertUser(src.user)
        val comment = TGitObjectConverter.convertComment(src.objectAttributes, user)
        val extra = fillNoteExtra(src).toMutableMap()
        return when (objectAttributes.noteableType) {
            TGitNoteableType.ISSUE -> {
                val issue = TGitObjectConverter.convertIssue(user, src.issue, objectAttributes.url)
                IssueCommentHook(
                    eventType = TGitEventType.NOTE.name,
                    issue = issue,
                    action = EventAction.CREATE,
                    comment = comment,
                    repo = repo,
                    sender = user,
                    extras = extra
                )
            }

            TGitNoteableType.COMMIT -> {
                val commit = TGitObjectConverter.convertCommit(src.commit)
                CommitCommentHook(
                    eventType = TGitEventType.NOTE.name,
                    commit = commit,
                    action = EventAction.CREATE,
                    comment = comment,
                    repo = repo,
                    sender = user,
                    extras = extra
                )
            }

            TGitNoteableType.REVIEW -> {
                if (src.mergeRequest != null) {
                    val pullRequest = TGitObjectConverter.convertPullRequest(user, src.mergeRequest)
                    extra.putAll(
                        TGitObjectToMapConverter.convertMergeRequestEvent(repo, src.mergeRequest)
                    )
                    PullRequestCommentHook(
                        eventType = TGitEventType.NOTE.name,
                        pullRequest = pullRequest,
                        action = EventAction.CREATE,
                        comment = comment,
                        repo = repo,
                        sender = user,
                        extras = extra
                    )
                } else if (src.review != null) {
                    val review = TGitObjectConverter.convertReview(src.review)
                    PullRequestCommentHook(
                        eventType = TGitEventType.NOTE.name,
                        review = review,
                        action = EventAction.CREATE,
                        comment = comment,
                        repo = repo,
                        sender = user,
                        extras = extra
                    )
                } else {
                    null
                }
            }

            else -> null
        }
    }

    private fun parsePullRequestReviewHook(body: String): PullRequestReviewHook {
        val src = TGitJsonUtil.fromJson(body, TGitReviewEvent::class.java)
        val repo = TGitObjectConverter.convertRepository(src.projectId, src.repository)

        val eventReviewer = src.reviewer
        val (sender, sourceState) = if (eventReviewer != null) {
            TGitObjectConverter.convertUser(eventReviewer.reviewer) to eventReviewer.state
        } else {
            TGitObjectConverter.convertUser(src.author) to src.state
        }

        val state = when (sourceState) {
            "approving" -> ReviewState.APPROVING
            "approved" -> ReviewState.APPROVED
            "change_required" -> ReviewState.CHANGE_REQUIRED
            "change_denied" -> ReviewState.CHANGE_DENIED
            "close" -> ReviewState.UNKNOWN
            else -> ReviewState.UNKNOWN
        }

        val review = Review(
            id = src.id,
            iid = src.iid,
            state = state,
            author = TGitObjectConverter.convertUser(src.author),
            link = "${src.repository.homepage}/reviews/${src.iid}",
            closed = sourceState == "close",
            title = ""
        )

        return PullRequestReviewHook(
            repo = repo,
            action = EventAction.CREATE,
            eventType = TGitEventType.REVIEW.name,
            review = review,
            sender = sender,
            extras = fillReviewExtra(src),
            pullRequest = if (src.reviewableType == "merge_request") {
                // 此处仅pull_request_id 在enrichHook有用，其他参数均为默认值
                PullRequest(
                    id = src.reviewableId,
                    body = "",
                    title = "",
                    link = "",
                    author = TGitObjectConverter.convertUser(src.author),
                    number = 0,
                    sourceRef = Reference("", "", ""),
                    targetRef = Reference("", "", ""),
                    sourceRepo = repo,
                    targetRepo = repo
                )
            } else {
                null
            }
        )
    }

    @SuppressWarnings("LongMethod", "CyclomaticComplexMethod")
    private fun convertPushHook(src: TGitPushEvent): GitPushHook {
        val action = when {
            src.createAndUpdate == false -> EventAction.NEW_BRANCH
            TGitPushOperationKind.DELETE.value == src.operationKind &&
                    "0000000000000000000000000000000000000000" == src.after -> EventAction.DELETE
            else -> EventAction.PUSH_FILE
        }

        val author = Signature(
            name = src.userName,
            email = src.userEmail
        )
        val commit = Commit(
            sha = src.checkoutSha,
            author = author,
            committer = author,
            message = ""
        ).let { baseCommit ->
            src.commits.firstOrNull()?.let {
                baseCommit.copy(
                    link = it.url,
                    message = it.message,
                    commitTime = DateUtils.convertDateToLocalDateTime(it.timestamp)
                )
            } ?: baseCommit
        }

        val operationKind = src.operationKind ?: ""
        val actionKind = src.actionKind ?: ""
        val changes = if (TGitPushOperationKind.UPDATE_NONFASTFORWORD.value != operationKind) {
            src.diffFiles?.map {
                Change(
                    path = it.newPath,
                    added = it.newFile,
                    deleted = it.deletedFile,
                    renamed = it.renamedFile,
                    oldPath = it.oldPath,
                    sha = "",
                    blobId = ""
                )
            } ?: emptyList()
        } else {
            emptyList()
        }

        val  extras = mutableMapOf<String,Any>()

        extras[BK_REPO_GIT_WEBHOOK_PUSH_ACTION_KIND] = actionKind
        extras[BK_REPO_GIT_WEBHOOK_PUSH_OPERATION_KIND] = operationKind
        extras[BK_REPO_GIT_MANUAL_UNLOCK] = false
        extras[PIPELINE_GIT_ACTION] = if (src.createAndUpdate == true) {
            EventAction.NEW_BRANCH_AND_PUSH_FILE.value
        } else {
            action.value
        }

        val repository = TGitObjectConverter.convertRepository(src.projectId, src.repository)
        val user = User(
            id = src.userId,
            name = src.userName,
            email = src.userEmail,
            username = src.userName
        )

        val commits = src.commits.map(TGitObjectConverter::convertCommit)
        val ref = GitUtils.trimRef(src.ref)
        val link = when (action) {
            EventAction.NEW_BRANCH -> "${repository.webUrl}/tree/$ref"
            EventAction.DELETE -> repository.webUrl
            else -> commit.link
        }
        return GitPushHook(
            action = action,
            ref = ref,
            eventType = TGitEventType.PUSH.name,
            repo = repository,
            before = src.before,
            after = src.after,
            commit = commit,
            link = link,
            sender = user,
            commits = commits,
            changes = changes,
            totalCommitsCount = src.totalCommitsCount,
            extras = extras,
            outputCommitIndexVar = true,
            skipCi = skipPushHook(src)
        )
    }

    companion object {
        private val logger = LoggerFactory.getLogger(TGitWebhookParser::class.java)

        private fun fillTagExtra(src: TGitTagPushEvent): MutableMap<String, Any> {
            val params = mutableMapOf<String, Any>()
            params[BK_REPO_GIT_WEBHOOK_TAG_OPERATION] = src.operationKind
            params[BK_REPO_GIT_WEBHOOK_PUSH_TOTAL_COMMIT] = src.totalCommitsCount
            params[BK_REPO_GIT_MANUAL_UNLOCK] = false
            src.createFrom?.let {
                params[BK_REPO_GIT_WEBHOOK_TAG_CREATE_FROM] = it
                params[PIPELINE_GIT_TAG_FROM] = it
            }
            params[PIPELINE_GIT_BEFORE_SHA] = src.before
            params[PIPELINE_GIT_BEFORE_SHA_SHORT] = GitUtils.getShortSha(src.before)
            params[PIPELINE_GIT_TAG_MESSAGE] = src.message
            src.commits.firstOrNull()?.let { lastCommit ->
                params[PIPELINE_GIT_COMMIT_AUTHOR] = lastCommit.author.name
                params[PIPELINE_GIT_COMMIT_MESSAGE] = lastCommit.message
                params.putAll(GitUtils.getOutputCommitIndexVar(src.commits.map(TGitObjectConverter::convertCommit)))
            }
            return params
        }

        private fun fillReviewExtra(src: TGitReviewEvent): MutableMap<String, Any> {
            val params = mutableMapOf<String, Any>()
            params[BK_REPO_GIT_WEBHOOK_REVIEW_STATE] = src.state
            params[BK_REPO_GIT_WEBHOOK_REVIEW_OWNER] = src.author?.username ?: ""
            params[BK_REPO_GIT_WEBHOOK_REVIEW_REVIEWABLE_ID] = src.reviewableId
            params[BK_REPO_GIT_WEBHOOK_REVIEW_REVIEWABLE_TYPE] = src.reviewableType
            params[BK_REPO_GIT_MANUAL_UNLOCK] = false
            params[PIPELINE_GIT_ACTION] = src.event
            val reviewers = ArrayList<String>(8)
            val approvingReviewers = ArrayList<String>(8)
            val approvedReviewers = ArrayList<String>(8)
            src.reviewers.forEach {
                reviewers.add(it.reviewer.username)
                when (it.state) {
                    "approving" -> approvingReviewers.add(it.reviewer.username)
                    "approved" -> approvedReviewers.add(it.reviewer.username)
                    else -> {}
                }
            }
            params[BK_REPO_GIT_WEBHOOK_REVIEW_REVIEWERS] = reviewers.joinToString(",")
            params[BK_REPO_GIT_WEBHOOK_REVIEW_APPROVING_REVIEWERS] = approvingReviewers.joinToString(",")
            params[BK_REPO_GIT_WEBHOOK_REVIEW_APPROVED_REVIEWERS] = approvedReviewers.joinToString(",")
            params[PIPELINE_START_WEBHOOK_USER_ID] = src.author?.username ?: ""
            when(src.reviewableType) {
                TGitReviewableType.MERGE_REQUEST.toValue() -> {
                    params[BK_HOOK_MR_ID] = src.reviewableId
                    params[PIPELINE_GIT_MR_URL] = "${src.repository.homepage ?: ""}/merge_requests/${src.iid}"
                }
            }
            return params
        }

        private fun fillNoteExtra(src: TGitNoteEvent): Map<String, Any> {
            val params = mutableMapOf<String, Any>()
            params[BK_REPO_GIT_MANUAL_UNLOCK] = false
            params[PIPELINE_GIT_BEFORE_SHA] = "----------"
            params[PIPELINE_GIT_BEFORE_SHA_SHORT] = "----------"
            params[PIPELINE_GIT_MR_ACTION] = src.objectAttributes.action
            return params
        }

        private fun skipPushHook(pushEvent: TGitPushEvent): Boolean {
            return with(pushEvent) {
                when {
                    totalCommitsCount <= 0 -> {
                        logger.info(
                            "Git web hook no commit $totalCommitsCount |operationKind = $operationKind"
                        )
                        TGitPushOperationKind.UPDATE_NONFASTFORWORD.value == operationKind
                    }
                    ref.startsWith("refs/for/") -> {
                        logger.info("Git web hook is pre-push event|branchName=$ref")
                        false
                    }
                    else -> true
                }
            }
        }
    }
}
