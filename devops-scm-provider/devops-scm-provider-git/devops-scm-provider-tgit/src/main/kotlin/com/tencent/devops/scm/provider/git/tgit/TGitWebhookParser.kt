package com.tencent.devops.scm.provider.git.tgit

import com.tencent.devops.scm.api.WebhookParser
import com.tencent.devops.scm.api.enums.EventAction
import com.tencent.devops.scm.api.enums.ReviewState
import com.tencent.devops.scm.api.pojo.*
import com.tencent.devops.scm.api.pojo.repository.git.GitRepositoryUrl
import com.tencent.devops.scm.api.pojo.repository.git.GitScmServerRepository
import com.tencent.devops.scm.api.pojo.webhook.Webhook
import com.tencent.devops.scm.api.pojo.webhook.git.*
import com.tencent.devops.scm.api.util.GitUtils
import com.tencent.devops.scm.provider.git.tgit.enums.TGitEventType
import com.tencent.devops.scm.sdk.common.util.DateUtils
import com.tencent.devops.scm.sdk.common.util.UrlConverter
import com.tencent.devops.scm.sdk.tgit.enums.TGitPushOperationKind
import com.tencent.devops.scm.sdk.tgit.pojo.TGitUser
import com.tencent.devops.scm.sdk.tgit.pojo.webhook.*
import com.tencent.devops.scm.sdk.tgit.util.TGitJsonUtil
import org.apache.commons.collections4.CollectionUtils
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import java.util.*
import java.util.stream.Collectors

/**
 * TGit Webhook 解析器实现类
 */
class TGitWebhookParser : WebhookParser {

    override fun parse(request: HookRequest): Webhook {
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
        return secretToken.isNullOrEmpty() || secretToken == request.headers["X-Token"]
    }

    private fun parsePushHook(body: String): GitPushHook {
        val src = TGitJsonUtil.fromJson(body, TGitPushEvent::class.java)
        return convertPushHook(src)
    }

    private fun parseTagHook(body: String): GitTagHook {
        val src = TGitJsonUtil.fromJson(body, TGitTagPushEvent::class.java)

        val action = if (TGitPushOperationKind.DELETE.value == src.operationKind) {
            EventAction.DELETE
        } else {
            EventAction.CREATE
        }
        val sha = if (action == EventAction.DELETE) src.before else src.after
        val createFrom = when {
            action == EventAction.DELETE -> GitUtils.getShortSha(src.before)
            src.createFrom.isNullOrBlank() || src.checkoutSha.isNotBlank() -> GitUtils.getShortSha(src.checkoutSha)
            else -> src.createFrom
        }

        val user = User.builder()
                .id(src.userId)
                .name(src.userName)
                .email(src.userEmail)
                .build()

        val repo = TGitObjectConverter.convertRepository(src.projectId, src.repository)

        val refName = GitUtils.trimRef(src.ref)
        val (commit, linkUrl) = when (action) {
            EventAction.DELETE -> Pair(
                Commit.builder().sha(src.before).message("").build(),
                repo.webUrl
            )
            EventAction.CREATE -> Pair(
                src.commits.firstOrNull()?.let { TGitObjectConverter.convertCommit(it) },
                "${repo.webUrl}/-/tags/$refName"
            )
            else -> Pair(null, "")
        }

        val ref = Reference.builder()
                .name(refName)
                .sha(sha)
                .linkUrl(linkUrl)
                .build()

        val extras = fillTagExtra(src)

        return GitTagHook.builder()
                .ref(ref)
                .repo(repo)
                .eventType(TGitEventType.TAG_PUSH.name)
                .sender(user)
                .action(action)
                .commit(commit)
                .extras(extras)
                .createFrom(createFrom)
                .build()
    }

    private fun parsePullRequestHook(body: String): PullRequestHook {
        val src = TGitJsonUtil.fromJson(body, TGitMergeRequestEvent::class.java)
        val objectAttributes = src.objectAttributes
        val action = when (objectAttributes.action) {
            "open" -> EventAction.OPEN
            "close" -> EventAction.CLOSE
            "reopen" -> EventAction.REOPEN
            "merge" -> EventAction.MERGE
            "update" -> if ("push-update" == objectAttributes.extensionAction) EventAction.PUSH_UPDATE else EventAction.EDIT
            else -> EventAction.EDIT
        }

        val extras = HashMap<String, Any>().apply {
            put("BK_REPO_GIT_MANUAL_UNLOCK", src.manualUnlock)
            put("PIPELINE_GIT_MR_ACTION", objectAttributes.action)
            put("PIPELINE_GIT_ACTION", objectAttributes.action)
            putAll(TGitObjectToMapConverter.convertMergeRequestEvent(src))
        }

        val srcTarget = objectAttributes.target
        val targetRepositoryUrl = GitRepositoryUrl(srcTarget.httpUrl)
        val repo = GitScmServerRepository.builder()
                .id(objectAttributes.targetProjectId)
                .group(targetRepositoryUrl.group)
                .name(srcTarget.name)
                .fullName(targetRepositoryUrl.fullName)
                .httpUrl(srcTarget.httpUrl)
                .sshUrl(srcTarget.sshUrl)
                .webUrl(srcTarget.webUrl)
                .build()

        val tGitUser = src.user
        val user = User.builder()
                .name(tGitUser.name)
                .username(tGitUser.username)
                .email(tGitUser.email)
                .avatar(tGitUser.avatarUrl)
                .build()

        val pullRequest = TGitObjectConverter.convertPullRequest(user, objectAttributes)
        val commit = TGitObjectConverter.convertCommit(objectAttributes.lastCommit)

        return PullRequestHook.builder()
                .action(action)
                .repo(repo)
                .eventType(TGitEventType.MERGE_REQUEST.name)
                .pullRequest(pullRequest)
                .sender(user)
                .commit(commit)
                .extras(extras)
                .build()
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

        val tGitUser = src.user
        val sender = User.builder()
                .id(tGitUser.id)
                .name(tGitUser.name)
                .username(tGitUser.username)
                .avatar(tGitUser.avatarUrl)
                .build()

        val issue = TGitObjectConverter.convertIssue(sender, objectAttributes)

        val extra = HashMap<String, Any>().apply {
            put("BK_REPO_GIT_WEBHOOK_ISSUE_STATE", objectAttributes.state)
            put("BK_REPO_GIT_MANUAL_UNLOCK", false)
        }

        return IssueHook.builder()
                .repo(repo)
                .eventType(TGitEventType.ISSUES.name)
                .action(action)
                .issue(issue)
                .sender(sender)
                .extras(extra)
                .build()
    }

    private fun parseCommentHook(body: String): Webhook? {
        val src = TGitJsonUtil.fromJson(body, TGitNoteEvent::class.java)
        val objectAttributes = src.objectAttributes

        val tGitRepo = src.repository
        val httpUrl = tGitRepo.realHttpUrl
        val repositoryUrl = GitRepositoryUrl(httpUrl)
        val repo = GitScmServerRepository.builder()
                .id(src.projectId)
                .group(repositoryUrl.group)
                .name(repositoryUrl.name)
                .fullName(repositoryUrl.fullName)
                .webUrl(tGitRepo.homepage)
                .httpUrl(httpUrl)
                .sshUrl(tGitRepo.gitSshUrl.ifBlank { UrlConverter.gitHttp2Ssh(httpUrl) })
                .build()

        val tGitUser = src.user
        val user = User.builder()
                .id(objectAttributes.authorId)
                .name(tGitUser.name)
                .username(tGitUser.username)
                .avatar(tGitUser.avatarUrl)
                .build()

        val comment = Comment.builder()
                .id(objectAttributes.id)
                .body(objectAttributes.note)
                .author(user)
                .created(objectAttributes.createdAt)
                .updated(objectAttributes.updatedAt)
                .type(objectAttributes.noteableType.value)
                .link(objectAttributes.url)
                .build()

        return when (objectAttributes.noteableType) {
            TGitNoteEvent.NoteableType.ISSUE -> {
                val issue = TGitObjectConverter.convertIssue(user, src.issue)
                IssueCommentHook.builder()
                        .eventType(TGitEventType.NOTE.name)
                        .issue(issue)
                        .build()
            }
            TGitNoteEvent.NoteableType.COMMIT -> {
                val commit = TGitObjectConverter.convertCommit(src.commit)
                CommitCommentHook.builder()
                        .eventType(TGitEventType.NOTE.name)
                        .commit(commit)
                        .build()
            }
            TGitNoteEvent.NoteableType.REVIEW -> {
                if (src.mergeRequest != null) {
                    val pullRequest = TGitObjectConverter.convertPullRequest(user, src.mergeRequest)
                    PullRequestCommentHook.builder()
                            .eventType(TGitEventType.NOTE.name)
                            .pullRequest(pullRequest)
                            .build()
                } else if (src.review != null) {
                    val review = TGitObjectConverter.convertReview(src.review)
                    PullRequestCommentHook.builder()
                            .eventType(TGitEventType.NOTE.name)
                            .review(review)
                            .build()
                } else {
                    null
                }
            }
            else -> null
        }?.apply {
            this.comment = comment
            this.action = EventAction.CREATE
            this.repo = repo
            this.sender = user
            this.extras = fillNoteExtra(src)
        }
    }

    private fun parsePullRequestReviewHook(body: String): PullRequestReviewHook {
        val src = TGitJsonUtil.fromJson(body, TGitReviewEvent::class.java)
        val repo = TGitObjectConverter.convertRepository(src.projectId, src.repository)

        val eventReviewer = src.reviewer
        val (sender, sourceState) = if (eventReviewer != null) {
            val reviewer = eventReviewer.reviewer
            Pair(
                User.builder()
                        .id(reviewer.id)
                        .name(reviewer.name)
                        .avatar(reviewer.avatarUrl)
                        .build(),
                eventReviewer.state
            )
        } else {
            val author = src.author
            Pair(
                User.builder()
                        .id(author.id)
                        .name(author.name)
                        .avatar(author.avatarUrl)
                        .build(),
                src.state
            )
        }

        val state = when (sourceState) {
            "approving" -> ReviewState.APPROVING
            "approved" -> ReviewState.APPROVED
            "changes_requested" -> ReviewState.CHANGES_REQUESTED
            "change_denied" -> ReviewState.CHANGE_DENIED
            "close" -> ReviewState.UNKNOWN
            else -> ReviewState.UNKNOWN
        }

        val closed = sourceState == "close"
        val link = "${src.repository.homepage}/reviews/${src.iid}"
        val review = Review.builder()
                .id(src.id)
                .iid(src.iid)
                .state(state)
                .author(TGitObjectConverter.convertUser(src.author))
                .link(link)
                .closed(closed)
                .build()

        val extra = fillReviewExtra(src)

        return PullRequestReviewHook.builder()
                .repo(repo)
                .action(EventAction.CREATE)
                .eventType(TGitEventType.REVIEW.name)
                .review(review)
                .sender(sender)
                .extras(extra)
                .apply {
                    if ("merge_request" == src.reviewableType) {
                        pullRequest = PullRequest().apply { id = src.reviewableId }
                    } else {
                        extra["BK_REPO_GIT_WEBHOOK_REVIEW_STATE"] = src.state
                        extra["BK_REPO_GIT_WEBHOOK_REVIEW_OWNER"] = src.author?.username ?: ""
                    }
                }
                .build()
    }

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

        val lastCommit = src.commits.firstOrNull()
        val commit = lastCommit?.let {
            Commit(
                sha = it.id,
                author = author,
                committer = author,
                message = it.message,
                link =
            )
        }.builder()
                .sha(src.checkoutSha)
                .author(author)
                .committer(author)
                .message(lastCommit?.message)
                .apply {
                    lastCommit?.let {
                        link = it.url
                        message = it.message
                        commitTime = DateUtils.convertDateToLocalDateTime(it.timestamp)
                    }
                }
                .build()

        val operationKind = src.operationKind ?: ""
        val actionKind = src.actionKind ?: ""
        val changes = if (TGitPushOperationKind.UPDATE_NONFASTFORWORD.value != operationKind) {
            src.diffFiles?.map {
                Change.builder()
                        .added(it.newFile)
                        .renamed(it.renamedFile)
                        .deleted(it.deletedFile)
                        .path(it.newPath)
                        .oldPath(it.oldPath)
                        .build()
            } ?: emptyList()
        } else {
            emptyList()
        }

        val extras = HashMap<String, Any>().apply {
            put("BK_REPO_GIT_WEBHOOK_PUSH_ACTION_KIND", actionKind)
            put("BK_REPO_GIT_WEBHOOK_PUSH_OPERATION_KIND", operationKind)
            put("BK_REPO_GIT_MANUAL_UNLOCK", false)
            val ciAction = if (src.createAndUpdate == true) EventAction.NEW_BRANCH_AND_PUSH_FILE.value else action.value
            put("PIPELINE_GIT_ACTION", ciAction)
        }

        val repository = TGitObjectConverter.convertRepository(src.projectId, src.repository)
        val user = User.builder()
                .id(src.userId)
                .name(src.userName)
                .email(src.userEmail)
                .build()

        val commits = src.commits.map(TGitObjectConverter::convertCommit)
        val ref = GitUtils.trimRef(src.ref)
        val link = when (action) {
            EventAction.NEW_BRANCH -> "${repository.webUrl}/tree/$ref"
            EventAction.DELETE -> repository.webUrl
            else -> commit.link
        }

        return GitPushHook.builder()
                .action(action)
                .ref(ref)
                .eventType(TGitEventType.PUSH.name)
                .repo(repository)
                .before(src.before)
                .after(src.after)
                .commit(commit)
                .link(link)
                .sender(user)
                .commits(commits)
                .changes(changes)
                .totalCommitsCount(src.totalCommitsCount)
                .extras(extras)
                .outputCommitIndexVar(true)
                .skipCi(skipPushHook(src))
                .build()
    }

    companion object {
        private val logger = LoggerFactory.getLogger(TGitWebhookParser::class.java)

        private fun fillTagExtra(src: TGitTagPushEvent): Map<String, Any> {
            return HashMap<String, Any>().apply {
                put("BK_REPO_GIT_WEBHOOK_TAG_OPERATION", src.operationKind)
                put("BK_REPO_GIT_WEBHOOK_PUSH_TOTAL_COMMIT", src.totalCommitsCount)
                put("BK_REPO_GIT_MANUAL_UNLOCK", false)
                src.createFrom?.let {
                    put("BK_REPO_GIT_WEBHOOK_TAG_CREATE_FROM", it)
                    put("PIPELINE_GIT_TAG_FROM", it)
                }
                put("PIPELINE_GIT_BEFORE_SHA", src.before)
                put("PIPELINE_GIT_BEFORE_SHA_SHORT", GitUtils.getShortSha(src.before))
                put("PIPELINE_GIT_TAG_MESSAGE", src.message)
                src.commits.firstOrNull()?.let { lastCommit ->
                    put("PIPELINE_GIT_COMMIT_AUTHOR", lastCommit.author.name)
                    put("PIPELINE_GIT_COMMIT_MESSAGE", lastCommit.message)
                    putAll(GitUtils.getOutputCommitIndexVar(src.commits.map(TGitObjectConverter::convertCommit)))
                }
            }
        }

        private fun fillReviewExtra(src: TGitReviewEvent): Map<String, Any> {
            return HashMap<String, Any>().apply {
                put("BK_REPO_GIT_WEBHOOK_REVIEW_REVIEWABLE_ID", src.reviewableId)
                put("BK_REPO_GIT_WEBHOOK_REVIEW_REVIEWABLE_TYPE", src.reviewableType)
                put("BK_REPO_GIT_MANUAL_UNLOCK", false)
                put("PIPELINE_GIT_ACTION", src.event)
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
                put("BK_REPO_GIT_WEBHOOK_REVIEW_REVIEWERS", reviewers.joinToString(","))
                put("BK_REPO_GIT_WEBHOOK_REVIEW_APPROVING_REVIEWERS", approvingReviewers.joinToString(","))
                put("BK_REPO_GIT_WEBHOOK_REVIEW_APPROVED_REVIEWERS", approvedReviewers.joinToString(","))
                put("PIPELINE_START_WEBHOOK_USER_ID", src.author?.username ?: "")
            }
        }

        private fun fillNoteExtra(src: TGitNoteEvent): Map<String, Any> {
            return HashMap<String, Any>().apply {
                put("BK_REPO_GIT_MANUAL_UNLOCK", false)
                put("PIPELINE_GIT_BEFORE_SHA", "----------")
                put("PIPELINE_GIT_BEFORE_SHA_SHORT", "----------")
                put("PIPELINE_GIT_MR_ACTION", src.objectAttributes.action)
            }
        }

        private fun skipPushHook(pushEvent: TGitPushEvent): Boolean {
            return when {
                pushEvent.totalCommitsCount <= 0 -> {
                    logger.info(
                        "Git web hook no commit {} |operationKind= {}",
                        pushEvent.totalCommitsCount,
                        pushEvent.operationKind
                    )
                    TGitPushOperationKind.UPDATE_NONFASTFORWORD.value == pushEvent.operationKind
                }
                pushEvent.ref.startsWith("refs/for/") -> {
                    logger.info("Git web hook is pre-push event|branchName={}", pushEvent.ref)
                    false
                }
                else -> true
            }
        }
    }
}
