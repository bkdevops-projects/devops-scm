package com.tencent.devops.scm.provider.git.bkcode

import com.tencent.devops.scm.api.enums.ScmEventType
import com.tencent.devops.scm.api.enums.Visibility
import com.tencent.devops.scm.api.pojo.Change
import com.tencent.devops.scm.api.pojo.Comment
import com.tencent.devops.scm.api.pojo.Commit
import com.tencent.devops.scm.api.pojo.Content
import com.tencent.devops.scm.api.pojo.Hook
import com.tencent.devops.scm.api.pojo.HookEvents
import com.tencent.devops.scm.api.pojo.HookInput
import com.tencent.devops.scm.api.pojo.Issue
import com.tencent.devops.scm.api.pojo.PullRequest
import com.tencent.devops.scm.api.pojo.Reference
import com.tencent.devops.scm.api.pojo.Signature
import com.tencent.devops.scm.api.pojo.User
import com.tencent.devops.scm.api.pojo.repository.ScmProviderRepository
import com.tencent.devops.scm.api.pojo.repository.git.GitRepositoryUrl
import com.tencent.devops.scm.api.pojo.repository.git.GitScmProviderRepository
import com.tencent.devops.scm.api.pojo.repository.git.GitScmServerRepository
import com.tencent.devops.scm.sdk.bkcode.enums.BkCodeEventType
import com.tencent.devops.scm.sdk.bkcode.enums.BkCodeNoteableType
import com.tencent.devops.scm.sdk.bkcode.enums.BkCodeRepoType
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeAuthor
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeBranch
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeCommit
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeCommitDetail
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeCommitter
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeDiffFile
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeFileContent
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeMergeRequest
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeProjectHookInput
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeRepositoryDetail
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeTag
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeUser
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeWebhookConfig
import com.tencent.devops.scm.sdk.bkcode.pojo.webhook.BkCodeEventComment
import com.tencent.devops.scm.sdk.bkcode.pojo.webhook.BkCodeEventCommit
import com.tencent.devops.scm.sdk.bkcode.pojo.webhook.BkCodeEventIssue
import com.tencent.devops.scm.sdk.bkcode.pojo.webhook.BkCodeEventMergeRequest
import com.tencent.devops.scm.sdk.bkcode.pojo.webhook.BkCodeEventRepository
import com.tencent.devops.scm.sdk.bkcode.pojo.webhook.BkCodeEventUser
import com.tencent.devops.scm.sdk.common.util.DateUtils
import com.tencent.devops.scm.sdk.common.util.UrlConverter

import org.apache.commons.lang3.StringUtils

@SuppressWarnings("TooManyFunctions", "LongMethod")
object BkCodeObjectConverter {

    /*========================================repositories====================================================*/
    fun convertRepository(src: BkCodeEventRepository) = with(src) {
        val repositoryUrl = GitRepositoryUrl(httpUrl)
         GitScmServerRepository(
            id = id,
            group = repositoryUrl.group,
            name = repositoryUrl.name,
            fullName = repositoryUrl.fullName,
            httpUrl = httpUrl,
            sshUrl = StringUtils.defaultIfBlank(sshUrl, UrlConverter.gitHttp2Ssh(httpUrl)),
            webUrl = homepage
        )
    }

    fun convertRepository(
        project: BkCodeRepositoryDetail,
        repository: ScmProviderRepository? = null
    ) = with(project) {
        val finalUrl = httpUrl?.ifBlank { sshUrl }?.ifBlank { (repository as GitScmProviderRepository).url } ?: ""
        val repositoryUrl = GitRepositoryUrl(finalUrl)
        GitScmServerRepository(
            id = id,
            group = repositoryUrl.group,
            name = name,
            fullName = repositoryUrl.fullName,
            defaultBranch = defaultBranch,
            archived = false, // bkcode 现版本没有归档操作
            isPrivate = convertPrivate(repoType),
            visibility = convertVisibility(repoType),
            httpUrl = httpUrl,
            sshUrl = sshUrl,
            webUrl = httpUrl,
            created = DateUtils.convertDateToLocalDateTime(createTime),
            updated = DateUtils.convertDateToLocalDateTime(updateTime)
        )
    }

    private fun convertPrivate(repoType: BkCodeRepoType): Boolean {
        return repoType == BkCodeRepoType.PRIVATE
    }

    private fun convertVisibility(repoType: BkCodeRepoType): Visibility {
        return when (repoType) {
            BkCodeRepoType.PRIVATE -> Visibility.PRIVATE
            BkCodeRepoType.INTERNAL -> Visibility.INTERNAL
            else -> Visibility.UNDEFINED
        }
    }

    /*========================================pull request====================================================*/
    fun convertPullRequest(author: User, eventMergeRequest: BkCodeEventMergeRequest): PullRequest {
        val srcTarget = eventMergeRequest.targetRepository
        val srcSource = eventMergeRequest.sourceRepository
        val targetRepositoryUrl = GitRepositoryUrl(srcSource.httpUrl)
        val target = GitScmServerRepository(
            id = srcTarget.id,
            group = targetRepositoryUrl.group,
            name = srcTarget.name,
            fullName = targetRepositoryUrl.fullName,
            httpUrl = srcTarget.httpUrl,
            sshUrl = srcTarget.sshUrl,
            webUrl = srcTarget.httpUrl
        )

        val sourceRepositoryUrl = GitRepositoryUrl(srcSource.httpUrl)
        val source = GitScmServerRepository(
            id = srcSource.id,
            group = sourceRepositoryUrl.group,
            name = srcSource.name,
            fullName = sourceRepositoryUrl.fullName,
            httpUrl = srcSource.httpUrl,
            sshUrl = srcSource.sshUrl,
            webUrl = srcSource.httpUrl
        )
        // :TODO 最新commitId
        val lastCommitSha = ""
        val base = Reference(
            name = eventMergeRequest.targetBranch,
            sha = "",
            linkUrl = ""
        )

        val head = Reference(
            name = eventMergeRequest.sourceBranch,
            sha = lastCommitSha,
            linkUrl = ""
        )
        // 描述可能为空
        val description = eventMergeRequest.description ?: ""
        return PullRequest(
            id = eventMergeRequest.id,
            number = eventMergeRequest.number.toInt(),
            title = eventMergeRequest.title,
            body = description,
            link = "${target.webUrl}/-/mergeRequest/detail/${eventMergeRequest.number}",
            sha = lastCommitSha,
            targetRepo = target,
            sourceRepo = source,
            targetRef = base,
            sourceRef = head,
            closed = eventMergeRequest.state != "opened",
            merged = eventMergeRequest.state == "merged",
            mergeType = "",
            mergeCommitSha = eventMergeRequest.mergeCommitSha,
            author = author,
            created = DateUtils.convertDateToLocalDateTime(eventMergeRequest.createdAt),
            updated = DateUtils.convertDateToLocalDateTime(eventMergeRequest.updatedAt),
            description = description,
            labels = eventMergeRequest.labels.map { it.name }
        )
    }

    fun convertPullRequest(
        from: BkCodeMergeRequest,
        sourceProject: BkCodeRepositoryDetail?,
        targetProject: BkCodeRepositoryDetail
    ): PullRequest {
        val base = Reference(
            name = from.targetBranch,
            sha = from.baseCommitId ?: ""
        )
        val head = Reference(
            name = from.sourceBranch,
            sha = from.headCommitId ?: ""
        )
        val targetRepositoryUrl = GitRepositoryUrl(targetProject.httpUrl)

        val target = GitScmServerRepository(
            id = from.targetRepoId,
            group = targetRepositoryUrl.group,
            name = targetProject.name,
            fullName = targetRepositoryUrl.fullName,
            httpUrl = targetProject.httpUrl,
            sshUrl = targetProject.sshUrl,
            webUrl = targetRepositoryUrl.homePage
        )

        val source = if (from.targetRepoId == from.sourceRepoId) {
            target
        } else {
            sourceProject?.let { proj ->
                val sourceRepositoryUrl = GitRepositoryUrl(proj.httpUrl)
                GitScmServerRepository(
                    id = from.sourceRepoId,
                    group = sourceRepositoryUrl.group,
                    name = sourceRepositoryUrl.name,
                    fullName = sourceRepositoryUrl.fullName,
                    httpUrl = proj.httpUrl,
                    sshUrl = proj.sshUrl,
                    webUrl = sourceRepositoryUrl.homePage
                )
            } ?: GitScmServerRepository(
                id = from.sourceRepoId,
                group = "",
                name = "",
                fullName = "",
                httpUrl = "",
                sshUrl = "",
                webUrl = ""
            )
        }

        val description = from.description ?: ""
        return PullRequest(
            id = from.id,
            number = from.code,
            title = from.title,
            body = description,
            link = "${targetRepositoryUrl.homePage}/merge_requests/${from.code}",
            targetRef = base,
            sourceRef = head,
            sourceRepo = source,
            targetRepo = target,
            closed = from.closedAt != null,
            merged = from.mergedAt!= null,
            author = convertUser(from.creator),
            created = from.createTime?.let { DateUtils.convertDateToLocalDateTime(it) },
            updated = from.updateTime?.let { DateUtils.convertDateToLocalDateTime(it) },
            milestone = null,
            baseCommit = from.baseCommitId,
            labels = from.labels?.map { it.name },
            assignee = listOf(),
            description = description,
            sha = from.headCommitId
        )
    }
    /*========================================ref====================================================*/

    fun convertCommit(from: BkCodeCommit) = with(from) {
        Commit(
            sha = id,
            message = message,
            author = author?.let { convertSignature(it) },
            committer = committer?.let { convertSignature(it) },
            commitTime = committer?.let {
                DateUtils.convertDateToLocalDateTime(it.committedDate)
            },
            added = listOf(),
            modified = listOf(),
            removed = listOf(),
            link = ""
        )
    }

    fun convertCommit(from: BkCodeCommitDetail) = with(from.commitInfo) {
        convertCommit(this)
    }

    fun convertCommit(from: BkCodeEventCommit) = with(from) {
        Commit(
            sha = id,
            message = message,
            author = convertSignature(author),
            committer = convertSignature(committer),
            commitTime = null,
            added = listOf(),
            modified = listOf(),
            removed = listOf(),
            link = ""
        )
    }

    fun convertBranch(from: BkCodeBranch) = with(from) {
        Reference(
            name = name,
            sha = commit.id
        )
    }

    fun convertTag(from: BkCodeTag) = with(from) {
        Reference(
            name = name,
            sha = commit.id
        )
    }

    fun convertChange(src: BkCodeDiffFile) = with(src) {
        Change(
            added = created(),
            renamed = renamed(),
            deleted = removed(),
            path = dstPath,
            oldPath = srcPath,
            sha = "",
            blobId = ""
        )
    }

    /*========================================issue====================================================*/
    fun convertIssue(
        eventIssue: BkCodeEventIssue,
        issuesUrl: String = ""
    ) = with(eventIssue) {
        Issue(
            id = id,
            number = number.toInt(),
            title = title,
            body = description,
            link = issuesUrl.ifBlank { url },
            closed = state == "closed",
            author = convertUser(author),
            created = DateUtils.convertDateToLocalDateTime(createdAt),
            updated = DateUtils.convertDateToLocalDateTime(updatedAt),
            state = state,
            labels = labels.map { it.name }
        )
    }

    /*========================================user====================================================*/
    fun convertUser(src: BkCodeEventUser) = with(src) {
        User(
            id = -1L,
            username = username,
            name = displayName,
            avatar = "",
            email = email
        )
    }

    fun convertUser(src: BkCodeUser) = with(src) {
        User(
            id = -1L,
            username = username,
            name = displayName,
            avatar = "",
            email = email,
            created = src.createTime?.let { DateUtils.convertDateToLocalDateTime(it) }
        )
    }

    fun convertSignature(from: BkCodeEventUser) = with(from) {
        Signature(
            name = username,
            email = email
        )
    }

    fun convertSignature(from: BkCodeCommitter) = with(from) {
        Signature(
            name = name,
            email = email
        )
    }

    fun convertSignature(from: BkCodeAuthor) = with(from) {
        Signature(
            name = name,
            email = email
        )
    }

    fun convertComment(from: BkCodeEventComment, noteableType: BkCodeNoteableType) = with(from) {
        Comment(
            id = id,
            body = body,
            author = convertUser(author),
            created = DateUtils.convertDateToLocalDateTime(createdAt),
            updated = DateUtils.convertDateToLocalDateTime(updatedAt),
            link = url,
            type = noteableType.getValue()
        )
    }

    /*========================================hook config====================================================*/

    fun convertHookInput(input: HookInput)  = with(input) {
        BkCodeProjectHookInput.builder()
                .token(secret)
                .url(url)
                .build()
    }

    fun convertHook(from: BkCodeWebhookConfig) = with(from){
        Hook(
            id = id.toLong(),
            url = url,
            active = true,
            events = convertEvents(this),
            name = name
        )
    }

    private fun convertEvents(from: BkCodeWebhookConfig) = with(from) {
        HookEvents(
            push = events.contains(BkCodeEventType.PUSH.hookName),
            tag = events.contains(BkCodeEventType.TAG_PUSH.hookName),
            pullRequest = events.contains(BkCodeEventType.MERGE_REQUEST.hookName),
            pullRequestReview = events.contains(BkCodeEventType.MERGE_REQUEST.hookName),
            issue = events.contains(BkCodeEventType.ISSUES.hookName),
            comment = events.contains(BkCodeEventType.NOTE.hookName)
        )
    }

    fun convertFromHookInput(input: HookInput) = with(input) {
        val events = events?.getEnabledEvents()
                ?.map { convertEventType(it).hookName }
                ?.toTypedArray() ?: arrayOf()
        BkCodeProjectHookInput.builder()
                .url(url)
                .name(name)
                .events(events)
                .token(secret ?: "")
                .build()
    }

    /*========================================event type====================================================*/
    fun convertEventType(from: String) = when (ScmEventType.parse(from)) {
        ScmEventType.PUSH -> BkCodeEventType.PUSH
        ScmEventType.TAG -> BkCodeEventType.TAG_PUSH
        ScmEventType.PULL_REQUEST -> BkCodeEventType.MERGE_REQUEST
        ScmEventType.ISSUE -> BkCodeEventType.ISSUES
        ScmEventType.NOTE -> BkCodeEventType.NOTE
        ScmEventType.PULL_REQUEST_REVIEW -> BkCodeEventType.MERGE_REQUEST
        else -> BkCodeEventType.UNKNOWN
    }

    /*========================================file content====================================================*/
    fun convertContent(from: BkCodeFileContent) = Content(
        path = from.path,
        content = from.content,
        sha = "",
        blobId = ""
    )
}
