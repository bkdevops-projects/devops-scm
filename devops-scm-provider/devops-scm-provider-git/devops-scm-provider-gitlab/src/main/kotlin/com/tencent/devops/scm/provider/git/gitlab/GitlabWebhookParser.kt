package com.tencent.devops.scm.provider.git.gitlab

import com.tencent.devops.scm.api.WebhookParser
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_ISSUE_OWNER
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_PUSH_TOTAL_COMMIT
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_ACTION
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_BEFORE_SHA
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_BEFORE_SHA_SHORT
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_COMMIT_AUTHOR
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_COMMIT_MESSAGE
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_EVENT_URL
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_WEBHOOK_COMMIT_MESSAGE
import com.tencent.devops.scm.api.enums.EventAction
import com.tencent.devops.scm.api.pojo.Change
import com.tencent.devops.scm.api.pojo.Comment
import com.tencent.devops.scm.api.pojo.Commit
import com.tencent.devops.scm.api.pojo.HookRequest
import com.tencent.devops.scm.api.pojo.Issue
import com.tencent.devops.scm.api.pojo.PullRequest
import com.tencent.devops.scm.api.pojo.Reference
import com.tencent.devops.scm.api.pojo.Signature
import com.tencent.devops.scm.api.pojo.User
import com.tencent.devops.scm.api.pojo.repository.git.GitScmServerRepository
import com.tencent.devops.scm.api.pojo.webhook.Webhook
import com.tencent.devops.scm.api.pojo.webhook.git.CommitCommentHook
import com.tencent.devops.scm.api.pojo.webhook.git.GitPushHook
import com.tencent.devops.scm.api.pojo.webhook.git.GitTagHook
import com.tencent.devops.scm.api.pojo.webhook.git.IssueCommentHook
import com.tencent.devops.scm.api.pojo.webhook.git.IssueHook
import com.tencent.devops.scm.api.pojo.webhook.git.PullRequestCommentHook
import com.tencent.devops.scm.api.pojo.webhook.git.PullRequestHook
import com.tencent.devops.scm.api.util.GitUtils
import com.tencent.devops.scm.provider.git.gitlab.enums.GitlabEventType
import com.tencent.devops.scm.sdk.gitlab.pojo.webhook.GitlabEventCommit
import com.tencent.devops.scm.sdk.gitlab.pojo.webhook.GitlabEventRepository
import com.tencent.devops.scm.sdk.gitlab.pojo.webhook.GitlabEventUser
import com.tencent.devops.scm.sdk.gitlab.pojo.webhook.GitlabIssueEvent
import com.tencent.devops.scm.sdk.gitlab.pojo.webhook.GitlabMergeRequestEvent
import com.tencent.devops.scm.sdk.gitlab.pojo.webhook.GitlabNoteEvent
import com.tencent.devops.scm.sdk.gitlab.pojo.webhook.GitlabPushEvent
import com.tencent.devops.scm.sdk.gitlab.pojo.webhook.GitlabWebhookEvent
import com.tencent.devops.scm.sdk.gitlab.util.GitlabJsonUtil
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class GitlabWebhookParser : WebhookParser {
    override fun parse(request: HookRequest): Webhook? {
        val event = decode(request.body, GitlabWebhookEvent::class.java) ?: return null
        val kind = event.objectKind ?: return null
        val header = request.header(EVENT_HEADER)
        if (header != null && HEADER_KINDS[header] != kind) return null
        return runCatching {
            when (kind) {
                "push" -> decode(request.body, GitlabPushEvent::class.java)?.let(::parsePush)
                "tag_push" -> decode(request.body, GitlabPushEvent::class.java)?.let(::parseTag)
                "merge_request" -> decode(request.body, GitlabMergeRequestEvent::class.java)?.let(::parseMergeRequest)
                "issue" -> decode(request.body, GitlabIssueEvent::class.java)?.let(::parseIssue)
                "note" -> decode(request.body, GitlabNoteEvent::class.java)?.let(::parseNote)
                else -> null
            }
        }.getOrNull()
    }

    override fun verify(request: HookRequest, secretToken: String?): Boolean {
        if (secretToken.isNullOrEmpty()) return true
        val supplied = request.header(TOKEN_HEADER) ?: return false
        return MessageDigest.isEqual(
            secretToken.toByteArray(StandardCharsets.UTF_8),
            supplied.toByteArray(StandardCharsets.UTF_8)
        )
    }

    private fun parsePush(src: GitlabPushEvent): GitPushHook {
        val before = src.before.orEmpty()
        val after = src.after.orEmpty()
        val action = when {
            before == ZERO_SHA -> EventAction.NEW_BRANCH
            after == ZERO_SHA -> EventAction.DELETE
            else -> EventAction.PUSH_FILE
        }
        val repo = repository(src.project, src.repository, src.projectId)
        val commits = src.commits.orEmpty().map(::commit)
        val latestCommit = commits.firstOrNull { it.sha == after } ?: commits.lastOrNull()
        val current = if (action == EventAction.DELETE) null else latestCommit
            ?: src.checkoutSha?.let { Commit(it, "") }
        val ref = src.ref.orEmpty().removePrefix("refs/heads/")
        val totalCommitsCount = src.totalCommitsCount ?: commits.size
        val extras = mutableMapOf<String, Any>().apply {
            latestCommit?.let {
                put(PIPELINE_WEBHOOK_COMMIT_MESSAGE, it.message)
                put(PIPELINE_GIT_COMMIT_MESSAGE, it.message)
                put(PIPELINE_GIT_EVENT_URL, it.link)
            }
        }
        return GitPushHook(
            action = action,
            ref = ref,
            repo = repo,
            eventType = GitlabEventType.PUSH.name,
            before = before,
            after = after,
            commit = current,
            link = when (action) {
                EventAction.NEW_BRANCH -> "${repo.webUrl}/-/tree/$ref"
                EventAction.DELETE -> repo.webUrl
                else -> current?.link.orEmpty()
            },
            sender = pushUser(src),
            commits = commits,
            changes = if (action == EventAction.DELETE) emptyList() else commitChanges(commits),
            totalCommitsCount = totalCommitsCount,
            extras = extras,
            outputCommitIndexVar = true,
            skipCi = totalCommitsCount <= 0
        )
    }

    private fun commitChanges(commits: List<Commit>): List<Change> {
        val changes = linkedMapOf<String, Change>()
        commits.forEach { commit ->
            commit.added.forEach { path ->
                changes[path] = Change(path = path, added = true, sha = "", blobId = "")
            }
            commit.modified.forEach { path ->
                if (changes[path]?.added != true) {
                    changes[path] = Change(path = path, sha = "", blobId = "")
                }
            }
            commit.removed.forEach { path ->
                changes[path] = Change(path = path, deleted = true, oldPath = path, sha = "", blobId = "")
            }
        }
        return changes.values.toList()
    }

    private fun parseTag(src: GitlabPushEvent): GitTagHook {
        val before = src.before.orEmpty()
        val after = src.after.orEmpty()
        val action = when {
            before == ZERO_SHA -> EventAction.CREATE
            after == ZERO_SHA -> EventAction.DELETE
            else -> EventAction.UPDATE
        }
        val repo = repository(src.project, src.repository, src.projectId)
        val name = src.ref.orEmpty().removePrefix("refs/tags/")
        val sha = if (action == EventAction.DELETE) before else after
        val commits = src.commits.orEmpty().map(::commit)
        val eventCommit = commits.lastOrNull()
        val extras = mutableMapOf<String, Any>(
            BK_REPO_GIT_WEBHOOK_PUSH_TOTAL_COMMIT to (src.totalCommitsCount ?: commits.size),
            PIPELINE_GIT_BEFORE_SHA to before,
            PIPELINE_GIT_BEFORE_SHA_SHORT to GitUtils.getShortSha(before)
        ).apply {
            eventCommit?.let {
                put(PIPELINE_GIT_COMMIT_AUTHOR, it.author?.name.orEmpty())
                put(PIPELINE_GIT_COMMIT_MESSAGE, it.message)
            }
            putAll(GitUtils.getOutputCommitIndexVar(commits))
        }
        return GitTagHook(
            ref = Reference(name, sha, if (action == EventAction.DELETE) repo.webUrl else "${repo.webUrl}/-/tags/$name"),
            repo = repo,
            eventType = GitlabEventType.TAG_PUSH.name,
            action = action,
            sender = pushUser(src),
            commit = eventCommit ?: Commit(sha, ""),
            createFrom = src.checkoutSha,
            extras = extras
        )
    }

    private fun parseMergeRequest(src: GitlabMergeRequestEvent): PullRequestHook {
        val attr = src.objectAttributes ?: throw IllegalArgumentException("object_attributes cannot be null")
        val actionName = attr.action.orEmpty()
        val action = when (actionName) {
            "open" -> EventAction.OPEN
            "reopen" -> EventAction.REOPEN
            "close" -> EventAction.CLOSE
            "merge" -> EventAction.MERGE
            "update" -> EventAction.PUSH_UPDATE
            "approved", "approval", "unapproved", "unapproval" -> EventAction.EDIT
            else -> EventAction.UNKNOWN
        }
        val filterAction = when (actionName) {
            "approved", "approval" -> "approved"
            else -> action.value
        }
        val repo = repository(src.project, src.repository, attr.targetProjectId ?: src.projectId)
        val sender = user(src.user)
        val pr = pullRequest(attr, repo, author(attr.author, attr.authorId, sender))
        val lastCommit = attr.lastCommit?.let(::commit) ?: Commit(attr.sha.orEmpty(), "")
        return PullRequestHook(
            action = action,
            repo = repo,
            eventType = GitlabEventType.MERGE_REQUEST.name,
            pullRequest = pr,
            sender = sender,
            commit = lastCommit,
            changes = emptyList(),
            extras = GitlabObjectToMapConverter.convertPullRequest(pr, filterAction, sender).apply {
                this[PIPELINE_GIT_ACTION] = actionName
            }
        )
    }

    private fun parseIssue(src: GitlabIssueEvent): IssueHook {
        val attr = src.objectAttributes ?: throw IllegalArgumentException("object_attributes cannot be null")
        val actionName = attr.action.orEmpty()
        val sender = user(src.user)
        val repo = repository(src.project, src.repository, src.projectId)
        return IssueHook(
            action = when (actionName) {
                "open" -> EventAction.OPEN
                "reopen" -> EventAction.REOPEN
                "close" -> EventAction.CLOSE
                "update" -> EventAction.UPDATE
                else -> EventAction.UNKNOWN
            },
            repo = repo,
            eventType = GitlabEventType.ISSUES.name,
            issue = issue(attr, author(attr.author, attr.authorId, sender), repo),
            sender = sender,
            extras = mutableMapOf(
                BK_REPO_GIT_WEBHOOK_ISSUE_OWNER to sender.username
            )
        )
    }

    private fun parseNote(src: GitlabNoteEvent): Webhook? {
        val attr = src.objectAttributes ?: throw IllegalArgumentException("object_attributes cannot be null")
        val sender = user(src.user)
        val repo = repository(src.project, src.repository, src.projectId)
        val action = when (attr.action) {
            "update" -> EventAction.UPDATE
            null, "", "create" -> EventAction.CREATE
            else -> EventAction.UNKNOWN
        }
        val comment = Comment(
            id = attr.id ?: 0,
            body = attr.note.orEmpty(),
            link = attr.url.orEmpty(),
            author = sender,
            created = time(attr.createdAt) ?: EPOCH,
            updated = time(attr.updatedAt),
            type = attr.noteableType.orEmpty()
        )
        val extras = GitlabObjectToMapConverter.convertNote(attr.createdAt, attr.updatedAt)
        return when (attr.noteableType) {
            "MergeRequest" -> src.mergeRequest?.let { mergeRequest ->
                PullRequestCommentHook(
                    action,
                    repo,
                    GitlabEventType.NOTE.name,
                    comment,
                    sender,
                    extras,
                    pullRequest(mergeRequest, repo, author(mergeRequest.author, mergeRequest.authorId, sender))
                )
            }
            "Issue" -> src.issue?.let { eventIssue ->
                IssueCommentHook(
                    action,
                    repo,
                    GitlabEventType.NOTE.name,
                    comment,
                    sender,
                    extras,
                    issue(eventIssue, author(eventIssue.author, eventIssue.authorId, sender), repo)
                )
            }
            "Commit" -> src.commit?.let { eventCommit ->
                CommitCommentHook(
                    action,
                    repo,
                    GitlabEventType.NOTE.name,
                    comment,
                    sender,
                    extras,
                    commit(eventCommit)
                )
            }
            else -> null
        }
    }

    private fun repository(
        project: GitlabEventRepository?,
        legacy: GitlabEventRepository?,
        projectId: Long?
    ): GitScmServerRepository {
        val raw = project ?: legacy
        val fullName = raw?.pathWithNamespace ?: raw?.name.orEmpty()
        return GitScmServerRepository(
            id = raw?.id ?: projectId ?: 0,
            group = fullName.substringBeforeLast('/', ""),
            name = raw?.name.orEmpty().ifBlank { fullName.substringAfterLast('/') },
            fullName = fullName,
            defaultBranch = raw?.defaultBranch,
            httpUrl = raw?.gitHttpUrl ?: raw?.httpUrl ?: raw?.httpUrlToRepo ?: raw?.url.orEmpty(),
            sshUrl = raw?.gitSshUrl ?: raw?.sshUrl ?: raw?.sshUrlToRepo.orEmpty(),
            webUrl = raw?.webUrl ?: raw?.homepage.orEmpty()
        )
    }

    private fun pullRequest(
        attr: GitlabMergeRequestEvent.ObjectAttributes,
        repo: GitScmServerRepository,
        author: User
    ): PullRequest {
        val source = repositoryFromNested(attr.source, attr.sourceProjectId, repo)
        val target = repositoryFromNested(attr.target, attr.targetProjectId, repo)
        val sha = attr.lastCommit?.id ?: attr.sha.orEmpty()
        val iid = attr.iid ?: 0
        return PullRequest(
            id = attr.id ?: 0,
            number = iid,
            title = attr.title.orEmpty(),
            body = attr.description.orEmpty(),
            link = attr.url ?: attr.webUrl ?: "${repo.webUrl}/-/merge_requests/$iid",
            sha = sha,
            sourceRepo = source,
            targetRepo = target,
            sourceRef = Reference(attr.sourceBranch.orEmpty(), sha),
            targetRef = Reference(attr.targetBranch.orEmpty(), ""),
            closed = attr.state == "closed",
            merged = attr.state == "merged",
            mergeCommitSha = attr.mergeCommitSha ?: attr.squashCommitSha,
            author = author,
            created = time(attr.createdAt),
            updated = time(attr.updatedAt),
            labels = attr.labels.orEmpty().mapNotNull { it.title },
            description = attr.description
        )
    }

    private fun repositoryFromNested(
        raw: GitlabEventRepository?,
        projectId: Long?,
        fallback: GitScmServerRepository
    ): GitScmServerRepository {
        if (raw == null) return fallback.copy(id = projectId?.takeIf { it > 0 } ?: fallback.id)
        return repository(raw, null, projectId)
    }

    private fun issue(
        attr: GitlabIssueEvent.ObjectAttributes,
        author: User,
        repo: GitScmServerRepository
    ): Issue {
        val iid = attr.iid ?: 0
        return Issue(
            id = attr.id ?: 0,
            number = iid,
            title = attr.title.orEmpty(),
            body = attr.description,
            link = attr.url ?: attr.webUrl ?: "${repo.webUrl}/-/issues/$iid",
            labels = attr.labels.orEmpty().mapNotNull { it.title },
            closed = attr.state == "closed",
            author = author,
            created = time(attr.createdAt) ?: EPOCH,
            updated = time(attr.updatedAt),
            milestoneId = attr.milestoneId?.toString(),
            state = attr.state
        )
    }

    private fun pushUser(src: GitlabPushEvent) = User(
        id = src.userId ?: 0,
        username = src.userUsername ?: src.userName.orEmpty(),
        name = src.userName.orEmpty(),
        email = src.userEmail,
        avatar = src.userAvatar
    )

    private fun user(src: GitlabEventUser?) = User(
        id = src?.id ?: 0,
        username = src?.username.orEmpty(),
        name = src?.name.orEmpty(),
        email = src?.email,
        avatar = src?.avatarUrl
    )

    private fun author(src: GitlabEventUser?, authorId: Long?, fallback: User): User {
        if (src != null) {
            return User(
                id = src.id ?: fallback.id,
                username = src.username ?: fallback.username,
                name = src.name ?: fallback.name,
                email = src.email ?: fallback.email,
                avatar = src.avatarUrl ?: fallback.avatar
            )
        }
        return if (authorId != null && authorId > 0) fallback.copy(id = authorId) else fallback
    }

    private fun commit(src: GitlabEventCommit): Commit {
        val author = Signature(
            src.author?.name ?: src.authorName.orEmpty(),
            src.author?.email ?: src.authorEmail
        )
        return Commit(
            sha = src.id ?: src.sha.orEmpty(),
            message = src.message.orEmpty(),
            author = author,
            committer = author,
            commitTime = time(src.timestamp),
            link = src.url ?: src.webUrl.orEmpty(),
            added = src.added.orEmpty(),
            modified = src.modified.orEmpty(),
            removed = src.removed.orEmpty()
        )
    }

    private fun time(value: String?): LocalDateTime? {
        if (value.isNullOrBlank()) return null
        return runCatching { LocalDateTime.ofInstant(Instant.parse(value), ZoneOffset.UTC) }
            .recoverCatching { OffsetDateTime.parse(value).withOffsetSameInstant(ZoneOffset.UTC).toLocalDateTime() }
            .recoverCatching { LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME) }
            .recoverCatching { LocalDateTime.parse(value, UTC_TIME_FORMATTER) }
            .getOrNull()
    }

    private fun <T> decode(body: String, type: Class<T>): T? = runCatching {
        GitlabJsonUtil.getJsonFactory().fromJson(body, type)
    }.getOrNull()

    private fun HookRequest.header(name: String) = headers?.entries?.firstOrNull {
        it.key.equals(name, ignoreCase = true)
    }?.value

    companion object {
        private val UTC_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss 'UTC'")
        private val EPOCH = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC)
        private const val ZERO_SHA = "0000000000000000000000000000000000000000"
        private const val EVENT_HEADER = "X-Gitlab-Event"
        private const val TOKEN_HEADER = "X-Gitlab-Token"
        private val HEADER_KINDS = mapOf(
            "Push Hook" to "push",
            "Tag Push Hook" to "tag_push",
            "Merge Request Hook" to "merge_request",
            "Issue Hook" to "issue",
            "Note Hook" to "note"
        )
    }
}
