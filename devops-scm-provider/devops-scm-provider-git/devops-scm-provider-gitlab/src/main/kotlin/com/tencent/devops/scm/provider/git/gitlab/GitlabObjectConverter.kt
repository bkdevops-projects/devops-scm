package com.tencent.devops.scm.provider.git.gitlab

import com.tencent.devops.scm.api.enums.CheckRunConclusion
import com.tencent.devops.scm.api.enums.CheckRunStatus
import com.tencent.devops.scm.api.enums.ContentKind
import com.tencent.devops.scm.api.enums.Visibility
import com.tencent.devops.scm.api.pojo.Change
import com.tencent.devops.scm.api.pojo.CheckRun
import com.tencent.devops.scm.api.pojo.Comment
import com.tencent.devops.scm.api.pojo.Commit
import com.tencent.devops.scm.api.pojo.Content
import com.tencent.devops.scm.api.pojo.Hook
import com.tencent.devops.scm.api.pojo.HookEvents
import com.tencent.devops.scm.api.pojo.HookInput
import com.tencent.devops.scm.api.pojo.Issue
import com.tencent.devops.scm.api.pojo.Milestone
import com.tencent.devops.scm.api.pojo.PullRequest
import com.tencent.devops.scm.api.pojo.Reference
import com.tencent.devops.scm.api.pojo.Signature
import com.tencent.devops.scm.api.pojo.Tree
import com.tencent.devops.scm.api.pojo.User
import com.tencent.devops.scm.api.pojo.repository.git.GitScmServerRepository
import com.tencent.devops.scm.sdk.common.util.DateUtils
import com.tencent.devops.scm.sdk.gitlab.pojo.GitlabBranch
import com.tencent.devops.scm.sdk.gitlab.pojo.GitlabCommit
import com.tencent.devops.scm.sdk.gitlab.pojo.GitlabCommitStatus
import com.tencent.devops.scm.sdk.gitlab.pojo.GitlabDiff
import com.tencent.devops.scm.sdk.gitlab.pojo.GitlabIssue
import com.tencent.devops.scm.sdk.gitlab.pojo.GitlabMergeRequest
import com.tencent.devops.scm.sdk.gitlab.pojo.GitlabMilestone
import com.tencent.devops.scm.sdk.gitlab.pojo.GitlabNote
import com.tencent.devops.scm.sdk.gitlab.pojo.GitlabProject
import com.tencent.devops.scm.sdk.gitlab.pojo.GitlabProjectHook
import com.tencent.devops.scm.sdk.gitlab.pojo.GitlabRepositoryFile
import com.tencent.devops.scm.sdk.gitlab.pojo.GitlabTag
import com.tencent.devops.scm.sdk.gitlab.pojo.GitlabTreeItem
import com.tencent.devops.scm.sdk.gitlab.pojo.GitlabUser
import java.nio.charset.StandardCharsets
import java.util.Base64
import java.util.Date

@Suppress("TooManyFunctions")
object GitlabObjectConverter {
    fun convertRepository(from: GitlabProject): GitScmServerRepository {
        val fullName = from.pathWithNamespace.orEmpty().ifBlank { from.path.orEmpty() }
        return GitScmServerRepository(
            id = from.id ?: 0,
            group = fullName.substringBeforeLast('/', ""),
            name = from.name.orEmpty().ifBlank { fullName.substringAfterLast('/') },
            fullName = fullName,
            defaultBranch = from.defaultBranch,
            archived = from.archived,
            isPrivate = from.visibility == "private",
            visibility = when (from.visibility) {
                "private" -> Visibility.PRIVATE
                "internal" -> Visibility.INTERNAL
                "public" -> Visibility.PUBLIC
                else -> Visibility.UNDEFINED
            },
            httpUrl = from.httpUrlToRepo.orEmpty(),
            sshUrl = from.sshUrlToRepo.orEmpty(),
            webUrl = from.webUrl.orEmpty()
        )
    }

    fun convertBranch(from: GitlabBranch) = Reference(from.name.orEmpty(), from.commit?.id.orEmpty())
    fun convertTag(from: GitlabTag) = Reference(from.name.orEmpty(), from.commit?.id.orEmpty())

    fun convertCommit(from: GitlabCommit) = Commit(
        sha = from.id.orEmpty(),
        message = from.message.orEmpty(),
        link = from.webUrl.orEmpty(),
        author = Signature(from.authorName.orEmpty(), from.authorEmail.orEmpty()),
        committer = Signature(from.committerName.orEmpty(), from.committerEmail.orEmpty()),
        commitTime = date(from.committedDate ?: from.authoredDate ?: from.createdAt)
    )

    fun convertChange(from: GitlabDiff) = Change(
        added = from.isNewFile,
        renamed = from.isRenamedFile,
        deleted = from.isDeletedFile,
        path = from.newPath.orEmpty(),
        oldPath = from.oldPath.orEmpty(),
        sha = "",
        blobId = ""
    )

    fun convertPullRequest(
        from: GitlabMergeRequest,
        sourceProject: GitlabProject?,
        targetProject: GitlabProject
    ): PullRequest {
        val target = convertRepository(targetProject)
        val source = if (from.sourceProjectId == from.targetProjectId) target else sourceProject?.let(::convertRepository)
            ?: emptyRepository(from.sourceProjectId ?: 0)
        return PullRequest(
            id = from.id ?: 0,
            number = from.iid?.toInt() ?: 0,
            title = from.title.orEmpty(),
            body = from.description.orEmpty(),
            link = from.webUrl.orEmpty(),
            sha = from.sha,
            sourceRepo = source,
            targetRepo = target,
            targetRef = Reference(from.targetBranch.orEmpty(), ""),
            sourceRef = Reference(from.sourceBranch.orEmpty(), from.sha.orEmpty()),
            closed = from.state == "closed",
            merged = from.state == "merged",
            mergeCommitSha = from.mergeCommitSha ?: from.squashCommitSha,
            author = from.author?.let(::convertUser),
            created = from.createdAt?.let(::date),
            updated = from.updatedAt?.let(::date),
            labels = from.labels,
            description = from.description,
            milestone = from.milestone?.let(::convertMilestone),
            assignee = (from.assignees ?: from.assignee?.let(::listOf) ?: emptyList()).map(::convertUser)
        )
    }

    fun convertIssue(from: GitlabIssue) = Issue(
        id = from.id ?: 0,
        number = from.iid?.toInt() ?: 0,
        title = from.title.orEmpty(),
        body = from.description,
        link = from.webUrl.orEmpty(),
        labels = from.labels,
        closed = from.state == "closed",
        author = from.author?.let(::convertUser) ?: emptyUser(),
        created = date(from.createdAt),
        updated = from.updatedAt?.let(::date),
        milestoneId = from.milestone?.id?.toString(),
        state = from.state
    )

    fun convertUser(from: GitlabUser) = User(
        id = from.id ?: 0,
        username = from.username.orEmpty(),
        name = from.name.orEmpty(),
        email = from.email,
        avatar = from.avatarUrl
    )

    fun convertComment(from: GitlabNote) = Comment(
        id = from.id ?: 0,
        body = from.body.orEmpty(),
        link = "",
        author = from.author?.let(::convertUser) ?: emptyUser(),
        created = date(from.createdAt),
        updated = from.updatedAt?.let(::date),
        type = from.noteableType.orEmpty()
    )

    fun convertContent(from: GitlabRepositoryFile): Content {
        val content = if (from.encoding.equals("base64", true)) {
            String(Base64.getMimeDecoder().decode(from.content.orEmpty()), StandardCharsets.UTF_8)
        } else from.content.orEmpty()
        return Content(from.filePath.orEmpty(), content, from.commitId.orEmpty(), from.blobId.orEmpty())
    }

    fun convertTree(from: GitlabTreeItem, rootPath: String) = Tree(
        path = rootPath.trim('/').let { normalizedRootPath ->
            if (normalizedRootPath.isEmpty()) {
                from.name.orEmpty()
            } else {
                val itemPath = from.path.orEmpty()
                val rootPrefix = "$normalizedRootPath/"
                if (itemPath.startsWith(rootPrefix)) itemPath.removePrefix(rootPrefix) else itemPath
            }
        },
        sha = "",
        blobId = from.id.orEmpty(),
        kind = when (from.type) {
            "blob" -> if (from.mode == "120000") ContentKind.SYMLINK else ContentKind.FILE
            "tree" -> ContentKind.DIRECTORY
            "commit" -> ContentKind.GITLINK
            else -> ContentKind.UNSUPPORTED
        }
    )

    fun convertHook(from: GitlabProjectHook) = Hook(
        id = from.id ?: 0,
        url = from.url.orEmpty(),
        active = true,
        name = "",
        events = HookEvents(
            push = from.isPushEvents,
            tag = from.isTagPushEvents,
            pullRequest = from.isMergeRequestsEvents,
            issue = from.isIssuesEvents,
            comment = from.isNoteEvents,
            issueComment = from.isNoteEvents,
            pullRequestComment = from.isNoteEvents
        )
    )

    fun convertFromHookInput(input: HookInput): GitlabProjectHook {
        require(input.nativeEvents.isNullOrEmpty()) { "GitLab native hook events are not supported" }
        return GitlabProjectHook().apply {
            url = input.url
            isPushEvents = input.events?.push == true
            isTagPushEvents = input.events?.tag == true
            isMergeRequestsEvents = input.events?.pullRequest == true
            isIssuesEvents = input.events?.issue == true
            isNoteEvents = input.events?.let {
                it.comment == true || it.issueComment == true || it.pullRequestComment == true
            } == true
            isEnableSslVerification = input.skipVerify != true
        }
    }

    fun convertCheckRun(from: GitlabCommitStatus): CheckRun {
        val (status, conclusion) = when (from.status) {
            "pending" -> CheckRunStatus.QUEUED to null
            "running" -> CheckRunStatus.IN_PROGRESS to null
            "success" -> CheckRunStatus.COMPLETED to CheckRunConclusion.SUCCESS
            "failed" -> CheckRunStatus.COMPLETED to CheckRunConclusion.FAILURE
            "canceled" -> CheckRunStatus.COMPLETED to CheckRunConclusion.CANCELLED
            "skipped" -> CheckRunStatus.COMPLETED to CheckRunConclusion.SKIPPED
            else -> throw IllegalArgumentException("unknown GitLab commit status: ${from.status}")
        }
        return CheckRun(from.id ?: 0, status, from.name.orEmpty(), from.description, from.targetUrl, null, conclusion)
    }

    fun convertCheckRunState(status: CheckRunStatus, conclusion: CheckRunConclusion?): String = when (status) {
        CheckRunStatus.QUEUED -> "pending"
        CheckRunStatus.IN_PROGRESS -> "running"
        CheckRunStatus.COMPLETED -> when (requireNotNull(conclusion) {
            "conclusion cannot be null when status is COMPLETED"
        }) {
            CheckRunConclusion.SUCCESS -> "success"
            CheckRunConclusion.CANCELLED -> "canceled"
            CheckRunConclusion.SKIPPED -> "skipped"
            CheckRunConclusion.FAILURE, CheckRunConclusion.TIMED_OUT, CheckRunConclusion.ACTION_REQUIRED -> "failed"
            CheckRunConclusion.UNKNOWN -> throw IllegalArgumentException("unknown check run conclusion")
        }
        CheckRunStatus.UNKNOWN -> throw IllegalArgumentException("unknown check run status")
    }

    private fun convertMilestone(from: GitlabMilestone) = Milestone(
        id = from.id?.toInt() ?: 0,
        iid = from.iid?.toInt(),
        title = from.title.orEmpty(),
        state = from.state,
        description = from.description,
        dueDate = from.dueDate?.let(::date)
    )

    private fun emptyRepository(id: Long) = GitScmServerRepository(id, "", "", "", httpUrl = "", sshUrl = "", webUrl = "")
    private fun emptyUser() = User(0, "", "")
    private fun date(value: Date?) = DateUtils.convertDateToLocalDateTime(value ?: Date(0))
}
