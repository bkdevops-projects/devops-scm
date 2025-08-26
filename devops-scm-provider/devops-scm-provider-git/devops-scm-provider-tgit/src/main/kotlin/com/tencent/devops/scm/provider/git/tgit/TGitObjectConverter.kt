package com.tencent.devops.scm.provider.git.tgit

import com.tencent.devops.scm.api.enums.CheckRunConclusion
import com.tencent.devops.scm.api.enums.CheckRunStatus
import com.tencent.devops.scm.api.enums.ContentKind
import com.tencent.devops.scm.api.enums.ReviewState
import com.tencent.devops.scm.api.enums.Visibility
import com.tencent.devops.scm.api.pojo.Change
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
import com.tencent.devops.scm.api.pojo.Review
import com.tencent.devops.scm.api.pojo.Signature
import com.tencent.devops.scm.api.pojo.CheckRun
import com.tencent.devops.scm.api.pojo.Tree
import com.tencent.devops.scm.api.pojo.User
import com.tencent.devops.scm.api.pojo.repository.git.GitRepositoryUrl
import com.tencent.devops.scm.api.pojo.repository.git.GitScmServerRepository
import com.tencent.devops.scm.sdk.common.util.DateUtils
import com.tencent.devops.scm.sdk.common.util.UrlConverter
import com.tencent.devops.scm.sdk.tgit.enums.TGitCheckRunState
import com.tencent.devops.scm.sdk.tgit.enums.TGitIssueState
import com.tencent.devops.scm.sdk.tgit.enums.TGitReviewState
import com.tencent.devops.scm.sdk.tgit.enums.TGitVisibility
import com.tencent.devops.scm.sdk.tgit.pojo.TGitAssignee
import com.tencent.devops.scm.sdk.tgit.pojo.TGitAuthor
import com.tencent.devops.scm.sdk.tgit.pojo.TGitBranch
import com.tencent.devops.scm.sdk.tgit.pojo.TGitCommit
import com.tencent.devops.scm.sdk.tgit.pojo.TGitCheckRun
import com.tencent.devops.scm.sdk.tgit.pojo.TGitDiff
import com.tencent.devops.scm.sdk.tgit.pojo.TGitIssue
import com.tencent.devops.scm.sdk.tgit.pojo.TGitMergeRequest
import com.tencent.devops.scm.sdk.tgit.pojo.TGitMilestone
import com.tencent.devops.scm.sdk.tgit.pojo.TGitNote
import com.tencent.devops.scm.sdk.tgit.pojo.TGitProject
import com.tencent.devops.scm.sdk.tgit.pojo.TGitProjectHook
import com.tencent.devops.scm.sdk.tgit.pojo.TGitRepositoryFile
import com.tencent.devops.scm.sdk.tgit.pojo.TGitReview
import com.tencent.devops.scm.sdk.tgit.pojo.TGitReviewAttributes
import com.tencent.devops.scm.sdk.tgit.pojo.TGitReviewer
import com.tencent.devops.scm.sdk.tgit.pojo.TGitTag
import com.tencent.devops.scm.sdk.tgit.pojo.TGitTreeItem
import com.tencent.devops.scm.sdk.tgit.pojo.TGitUser
import com.tencent.devops.scm.sdk.tgit.pojo.webhook.TGitEventCommit
import com.tencent.devops.scm.sdk.tgit.pojo.webhook.TGitEventIssue
import com.tencent.devops.scm.sdk.tgit.pojo.webhook.TGitEventMergeRequest
import com.tencent.devops.scm.sdk.tgit.pojo.webhook.TGitEventRepository
import com.tencent.devops.scm.sdk.tgit.pojo.webhook.TGitNoteEvent

import org.apache.commons.lang3.StringUtils

@SuppressWarnings("TooManyFunctions", "LongMethod")
object TGitObjectConverter {

    /*========================================repositories====================================================*/
    fun convertRepository(id: Long, src: TGitEventRepository) = with(src) {
        val repositoryUrl = GitRepositoryUrl(realHttpUrl)
         GitScmServerRepository(
            id = id,
            group = repositoryUrl.group,
            name = repositoryUrl.name,
            fullName = repositoryUrl.fullName,
            httpUrl = realHttpUrl,
            sshUrl = StringUtils.defaultIfBlank(gitSshUrl, UrlConverter.gitHttp2Ssh(realHttpUrl)),
            webUrl = homepage
        )
    }

    fun convertRepository(project: TGitProject) = with(project) {
        val repositoryUrl = GitRepositoryUrl(httpUrlToRepo)
        GitScmServerRepository(
            id = id,
            group = repositoryUrl.group,
            name = name,
            fullName = repositoryUrl.fullName,
            defaultBranch = defaultBranch,
            archived = archived,
            isPrivate = convertPrivate(visibilityLevel),
            visibility = convertVisibility(visibilityLevel),
            httpUrl = httpsUrlToRepo,
            sshUrl = sshUrlToRepo,
            webUrl = webUrl,
            created = DateUtils.convertDateToLocalDateTime(createdAt),
            updated = DateUtils.convertDateToLocalDateTime(createdAt)
        )
    }

    private fun convertPrivate(visibilityLevel: Int): Boolean {
        return visibilityLevel == TGitVisibility.PRIVATE.toValue()
    }

    private fun convertVisibility(visibilityLevel: Int): Visibility {
        val tgitVisibility = TGitVisibility.forValue(visibilityLevel) ?: return Visibility.UNDEFINED
        return when (tgitVisibility) {
            TGitVisibility.PRIVATE -> Visibility.PRIVATE
            TGitVisibility.INTERNAL -> Visibility.INTERNAL
            TGitVisibility.PUBLIC -> Visibility.PUBLIC
            else -> Visibility.UNDEFINED
        }
    }

    /*========================================file====================================================*/
    fun convertContent(from: TGitRepositoryFile) = with(from) {
        Content(
            path = filePath,
            content = decodedContentAsString,
            sha = commitId,
            blobId = blobId
        )
    }

    fun convertTree(from: TGitTreeItem) = with(from) {
        Tree(
            path = name,
            blobId = id,
            kind = when (mode) {
                "100644", "100664", "100755" -> ContentKind.FILE
                "040000" -> ContentKind.DIRECTORY
                "120000" -> ContentKind.SYMLINK
                "160000" -> ContentKind.GITLINK
                else -> ContentKind.UNSUPPORTED
            },
            sha = ""
        )
    }

    /*========================================pull request====================================================*/
    fun convertPullRequest(author: User, eventMergeRequest: TGitEventMergeRequest): PullRequest {
        val srcTarget = eventMergeRequest.target
        val srcSource = eventMergeRequest.source
        val targetRepositoryUrl = GitRepositoryUrl(srcTarget.httpUrl)
        val target = GitScmServerRepository(
            id = eventMergeRequest.targetProjectId,
            group = targetRepositoryUrl.group,
            name = srcTarget.name,
            fullName = targetRepositoryUrl.fullName,
            httpUrl = srcTarget.httpUrl,
            sshUrl = srcTarget.sshUrl,
            webUrl = srcTarget.webUrl
        )

        val sourceRepositoryUrl = GitRepositoryUrl(srcSource.httpUrl)
        val source = GitScmServerRepository(
            id = eventMergeRequest.sourceProjectId,
            group = sourceRepositoryUrl.group,
            name = srcSource.name,
            fullName = sourceRepositoryUrl.fullName,
            httpUrl = srcSource.httpUrl,
            sshUrl = srcSource.sshUrl,
            webUrl = srcSource.webUrl
        )

        val base = Reference(
            name = eventMergeRequest.targetBranch,
            sha = "",
            linkUrl = ""
        )

        val head = Reference(
            name = eventMergeRequest.sourceBranch,
            sha = eventMergeRequest.lastCommit.id,
            linkUrl = ""
        )
        // 描述可能为空
        val description = eventMergeRequest.description ?: ""
        return PullRequest(
            id = eventMergeRequest.id,
            number = eventMergeRequest.iid,
            title = eventMergeRequest.title,
            body = description,
            link = "${target.webUrl}/merge_requests/${eventMergeRequest.iid}",
            sha = eventMergeRequest.lastCommit.id,
            targetRepo = target,
            sourceRepo = source,
            targetRef = base,
            sourceRef = head,
            closed = eventMergeRequest.state != "opened",
            merged = eventMergeRequest.state == "merged",
            mergeType = eventMergeRequest.mergeType,
            mergeCommitSha = eventMergeRequest.mergeCommitSha,
            author = author,
            created = DateUtils.convertDateToLocalDateTime(eventMergeRequest.createdAt),
            updated = DateUtils.convertDateToLocalDateTime(eventMergeRequest.updatedAt),
            description = description
        )
    }

    fun convertPullRequest(
        from: TGitMergeRequest,
        sourceProject: TGitProject?,
        targetProject: TGitProject
    ): PullRequest {
        val base = Reference(
            name = from.targetBranch,
            sha = from.targetCommit ?: ""
        )

        val head = Reference(
            name = from.sourceBranch,
            sha = from.sourceCommit ?: ""
        )

        val targetRepositoryUrl = GitRepositoryUrl(targetProject.httpsUrlToRepo)

        val target = GitScmServerRepository(
            id = from.targetProjectId,
            group = targetRepositoryUrl.group,
            name = targetProject.name,
            fullName = targetRepositoryUrl.fullName,
            httpUrl = targetProject.httpsUrlToRepo,
            sshUrl = targetProject.sshUrlToRepo,
            webUrl = targetProject.webUrl
        )

        val source = if (from.targetProjectId == from.sourceProjectId) {
            target
        } else {
            sourceProject?.let { proj ->
                val sourceRepositoryUrl = GitRepositoryUrl(proj.httpsUrlToRepo)
                GitScmServerRepository(
                    id = from.sourceProjectId,
                    group = sourceRepositoryUrl.group,
                    name = sourceRepositoryUrl.name,
                    fullName = sourceRepositoryUrl.fullName,
                    httpUrl = proj.httpsUrlToRepo,
                    sshUrl = proj.sshUrlToRepo,
                    webUrl = proj.webUrl
                )
            } ?: GitScmServerRepository(
                id = from.sourceProjectId,
                group = "",
                name = "",
                fullName = "",
                httpUrl = "",
                sshUrl = "",
                webUrl = ""
            )
        }

        val assignees = from.assignee?.let { listOf(convertUser(it)) } ?: listOf()
        val description = from.description ?: ""
        return PullRequest(
            id = from.id,
            number = from.iid,
            title = from.title,
            body = description,
            link = "${target.webUrl}/merge_requests/${from.iid}",
            targetRef = base,
            sourceRef = head,
            sourceRepo = source,
            targetRepo = target,
            closed = from.state != "opened",
            merged = from.state == "merged",
            author = convertUser(from.author),
            created = from.createdAt?.let { DateUtils.convertDateToLocalDateTime(it) },
            updated = from.updatedAt?.let { DateUtils.convertDateToLocalDateTime(it) },
            milestone = from.milestone?.let { convertMilestone(it) },
            baseCommit = from.baseCommit,
            labels = from.labels,
            assignee = assignees,
            description = description
        )
    }

    /*========================================ref====================================================*/
    fun convertCommit(eventCommit: TGitEventCommit) = with(eventCommit) {
        val committer = Signature(
            name = author.name,
            email = author.email
        )
        Commit(
            sha = id,
            message = message,
            commitTime = DateUtils.convertDateToLocalDateTime(timestamp),
            link = url,
            author = committer,
            committer = committer,
            added = added ?: listOf(),
            modified = modified ?: listOf(),
            removed = removed ?: listOf()
        )
    }

    fun convertCommit(from: TGitCommit) = with(from) {
        val author = Signature(
            name = authorName,
            email = authorEmail
        )
        val committer = Signature(
            name = committerName,
            email = committerEmail
        )
        Commit(
            sha = id,
            message = message,
            author = author,
            committer = committer,
            commitTime = DateUtils.convertDateToLocalDateTime(committedDate),
            added = listOf(),
            modified = listOf(),
            removed = listOf(),
            link = ""
        )
    }

    fun convertBranch(from: TGitBranch) = with(from) {
        Reference(
            name = name,
            sha = commit.id
        )
    }

    fun convertTag(from: TGitTag) = with(from) {
        Reference(
            name = name,
            sha = commit.id
        )
    }

    fun convertChange(src: TGitDiff) = with(src) {
        Change(
            added = newFile,
            renamed = renamedFile,
            deleted = deletedFile,
            path = newPath,
            oldPath = oldPath,
            sha = "",
            blobId = ""
        )
    }

    fun convertCheckRun(from: TGitCheckRun) = with(from) {
        val (status, conclusion) = convertState(state)
        CheckRun(
            id = 0L,
            status = status,
            name = context,
            summary = description,
            detailsUrl = targetUrl,
            conclusion = conclusion,
            detail = detail
        )
    }

    private fun convertState(from: String) = when (from) {
        TGitCheckRunState.PENDING.toValue() -> Pair(CheckRunStatus.IN_PROGRESS, null)
        TGitCheckRunState.SUCCESS.toValue() -> Pair(CheckRunStatus.COMPLETED, CheckRunConclusion.SUCCESS)
        else -> Pair(CheckRunStatus.COMPLETED, CheckRunConclusion.FAILURE)
    }

    fun convertCheckRunState(
        status: CheckRunStatus,
        conclusion: CheckRunConclusion?
    ) = when (status) {
        CheckRunStatus.IN_PROGRESS, CheckRunStatus.QUEUED -> {
            TGitCheckRunState.PENDING
        }

        CheckRunStatus.COMPLETED -> {
            if (conclusion == null) {
                throw IllegalArgumentException("conclusion cannot be null when status is COMPLETED")
            }
            if (conclusion == CheckRunConclusion.SUCCESS) {
                TGitCheckRunState.SUCCESS
            } else {
                TGitCheckRunState.FAILURE
            }
        }

        else -> throw IllegalArgumentException("unknown check run status $status")
    }

    /*========================================issue====================================================*/
    fun convertIssue(
        author: User,
        objectAttributes: TGitEventIssue,
        issuesUrl: String = ""
    ) = with(objectAttributes) {
        Issue(
            id = id,
            number = iid,
            title = title,
            body = description,
            link = issuesUrl.ifBlank { url },
            closed = state == "closed",
            author = author,
            created = DateUtils.convertDateToLocalDateTime(createdAt),
            updated = DateUtils.convertDateToLocalDateTime(updatedAt),
            milestoneId = milestoneId,
            state = state
        )
    }

    fun convertIssue(from: TGitIssue) = with(from) {
        Issue(
            id = id,
            number = iid,
            title = title,
            body = description,
            labels = labels,
            closed = state == TGitIssueState.CLOSED,
            author = convertUser(author),
            created = DateUtils.convertDateToLocalDateTime(createdAt),
            updated = DateUtils.convertDateToLocalDateTime(updatedAt),
            link = ""
        )
    }

    /*========================================user====================================================*/
    fun convertUser(src: TGitUser) = with(src) {
        User(
            id = id ?: 0,
            username = username,
            name = name,
            avatar = avatarUrl,
            email = email
        )
    }

    fun convertUser(from: TGitAuthor) = with(from) {
        User(
            id = id,
            username = username,
            name = name,
            avatar = avatarUrl,
            email = email
        )
    }

    fun convertUser(assignee: TGitAssignee) = with(assignee) {
        User(
            id = id,
            name = name,
            username = username,
            avatar = avatarUrl,
            email = email
        )
    }

    private fun convertUser(reviewers: List<TGitReviewer>) = reviewers.map { reviewer ->
        with(reviewer) {
            User(
                id = id,
                name = name,
                username = username,
                avatar = avatarUrl,
                email = email
            )
        }
    }

    fun convertUser(src: TGitUser, attr: TGitNoteEvent.ObjectAttributes) = with(src) {
        User(
            id = id ?: attr.authorId ?: 0,
            username = username,
            name = name,
            avatar = avatarUrl,
            email = email
        )
    }

    /*========================================comment====================================================*/
    fun convertComment(from: TGitNote) = with(from) {
        Comment(
            id = id,
            body = body,
            author = convertUser(author),
            created = DateUtils.convertDateToLocalDateTime(createdAt),
            updated = null,
            link = "",
            type = ""
        )
    }

    fun convertComment(from: TGitNoteEvent.ObjectAttributes, user: User) = with(from) {
        Comment(
            id = id,
            body = note,
            author = user,
            created = DateUtils.convertDateToLocalDateTime(createdAt),
            updated = DateUtils.convertDateToLocalDateTime(updatedAt),
            link = url,
            type = noteableType.value
        )
    }

    /*========================================milestones====================================================*/
    fun convertMilestone(milestone: TGitMilestone) = with(milestone) {
        Milestone(
            id = id,
            iid = iid,
            title = title,
            state = state,
            description = description,
            dueDate = DateUtils.convertDateToLocalDateTime(dueDate)
        )
    }

    /*========================================review====================================================*/
    fun convertReview(review: TGitReview) = with(review) {
        Review(
            id = id,
            iid = iid.toInt(),
            title = title,
            state = convertReviewState(state),
            reviewers = convertUser(reviewers),
            author = convertUser(author),
            link = ""
        )
    }

    fun convertReview(review: TGitReviewAttributes) = with(review) {
        Review(
            id = id,
            iid = iid.toInt(),
            title = title,
            state = convertReviewState(
                TGitReviewState.values()
                        .firstOrNull { it.toValue() == review.state } ?: TGitReviewState.EMPTY
            ),
            sourceBranch = sourceBranch,
            sourceCommit = sourceCommit,
            sourceProjectId = sourceProjectId,
            targetBranch = targetBranch,
            targetCommit = targetCommit,
            targetProjectId = targetProjectId,
            link = ""
        )
    }

    fun convertReviewState(reviewState: TGitReviewState): ReviewState {
        return when (reviewState) {
            TGitReviewState.APPROVING -> ReviewState.APPROVING
            TGitReviewState.APPROVED -> ReviewState.APPROVED
            TGitReviewState.CHANGE_DENIED -> ReviewState.CHANGE_DENIED
            TGitReviewState.CHANGE_REQUIRED -> ReviewState.CHANGE_REQUIRED
            TGitReviewState.EMPTY -> ReviewState.EMPTY
            TGitReviewState.CLOSED -> ReviewState.CLOSED
            else -> ReviewState.UNKNOWN
        }
    }

    /*========================================hook config====================================================*/
    fun convertHook(from: TGitProjectHook) = with(from){
        Hook(
            id = id,
            url = url,
            active = true,
            events = convertEvents(this),
            name = ""
        )
    }

    private fun convertEvents(from: TGitProjectHook) = with(from) {
        HookEvents(
            push = pushEvents,
            tag = tagPushEvents,
            pullRequest = mergeRequestsEvents,
            issue = issuesEvents,
            issueComment = noteEvents,
            pullRequestComment = noteEvents,
            pullRequestReview = reviewEvents
        )
    }

    fun convertFromHookInput(input: HookInput) = with(input){
        TGitProjectHook.builder()
                .url(url)
                .pushEvents(events?.push == true)
                .tagPushEvents(events?.tag == true)
                .mergeRequestsEvents(events?.pullRequest == true)
                .issuesEvents(events?.issue == true)
                .noteEvents(events?.pullRequestComment == true || events?.issueComment == true)
                .reviewEvents(events?.pullRequestReview == true)
                .build()
    }
}
