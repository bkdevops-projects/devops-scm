package com.tencent.devops.scm.provider.git.gitee

import com.tencent.devops.scm.api.constant.DateFormatConstants
import com.tencent.devops.scm.api.enums.CheckRunConclusion
import com.tencent.devops.scm.api.enums.CheckRunStatus
import com.tencent.devops.scm.api.enums.EventAction
import com.tencent.devops.scm.api.pojo.Change
import com.tencent.devops.scm.api.pojo.CheckRun
import com.tencent.devops.scm.api.pojo.CheckRunInput
import com.tencent.devops.scm.api.pojo.Commit
import com.tencent.devops.scm.api.pojo.Content
import com.tencent.devops.scm.api.pojo.Hook
import com.tencent.devops.scm.api.pojo.HookEvents
import com.tencent.devops.scm.api.pojo.Milestone
import com.tencent.devops.scm.api.pojo.Reference
import com.tencent.devops.scm.api.pojo.Signature
import com.tencent.devops.scm.api.pojo.User
import com.tencent.devops.scm.api.pojo.repository.git.GitScmServerRepository
import com.tencent.devops.scm.provider.git.gitee.enums.GiteeActionDesc
import com.tencent.devops.scm.sdk.common.util.DateUtils
import com.tencent.devops.scm.sdk.gitee.enums.GiteeCheckRunConclusion
import com.tencent.devops.scm.sdk.gitee.enums.GiteeCheckRunStatus
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeBaseUser
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeBranch
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeCheckRun
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeCheckRunOutput
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeCommitCompare
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeCommitDetail
import com.tencent.devops.scm.sdk.gitee.pojo.GiteePullRequestDiff
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeRepositoryDetail
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeTag
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeTagDetail
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeUserInfo
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeWebhookConfig
import com.tencent.devops.scm.sdk.gitee.pojo.webhook.GiteeEventAuthor
import com.tencent.devops.scm.sdk.gitee.pojo.webhook.GiteeEventCommit
import com.tencent.devops.scm.sdk.gitee.pojo.webhook.GiteeEventMilestone
import com.tencent.devops.scm.sdk.gitee.pojo.webhook.GiteeEventRef
import com.tencent.devops.scm.sdk.gitee.pojo.webhook.GiteeEventRepository
import java.text.ParseException
import java.text.SimpleDateFormat

object GiteeObjectConverter {

    /*========================================ref====================================================*/
    fun convertBranches(branch: GiteeBranch) = with(branch) {
        Reference(
            name = name,
            sha = commit.sha,
            linkUrl = protectionUrl
        )
    }

    fun convertRef(form: GiteeEventRef) = with(form) {
        Reference(
            name = ref,
            sha = sha
        )
    }

    fun convertTag(form: GiteeTag) = with(form) {
        Reference(
            name = name,
            sha = commit.sha
        )
    }

    fun convertTag(form: GiteeTagDetail) = with(form) {
        Reference(
            name = name,
            sha = targetCommitish
        )
    }

    /*========================================repository====================================================*/
    fun convertRepository(repository: GiteeEventRepository) = with(repository) {
        GitScmServerRepository(
            id = id,
            group = namespace,
            name = name,
            defaultBranch = defaultBranch,
            isPrivate = privateRepository,
            httpUrl = gitHttpUrl,
            sshUrl = sshUrl,
            webUrl = htmlUrl,
            created = DateUtils.convertDateToLocalDateTime(createdAt),
            updated = DateUtils.convertDateToLocalDateTime(updatedAt),
            fullName = fullName
        )
    }

    fun convertRepository(repository: GiteeRepositoryDetail) = with(repository) {
        GitScmServerRepository(
            id = id,
            group = nameSpace.path,
            name = name,
            defaultBranch = defaultBranch,
            isPrivate = privateRepository,
            httpUrl = htmlUrl,
            sshUrl = sshUrl,
            webUrl = htmlUrl,
            created = DateUtils.convertDateToLocalDateTime(createdAt),
            updated = DateUtils.convertDateToLocalDateTime(updatedAt),
            fullName = fullName
        )
    }

    /*========================================pull_request====================================================*/
    fun convertAction(sourceAction: String, actionDesc: String): EventAction {
        return if (EventAction.UPDATE.value == sourceAction && 
            GiteeActionDesc.SOURCE_BRANCH_CHANGED.value == actionDesc) {
            EventAction.PUSH_UPDATE
        } else {
            EventAction.values().first { it.value == sourceAction }
        }
    }

    /*========================================user====================================================*/
    fun convertAuthor(author: GiteeEventAuthor) = with(author) {
        User(
            id = id,
            name = name,
            avatar = avatarUrl,
            username = username,
            email = email
        )
    }

    /*========================================milestone====================================================*/
    fun convertMilestone(milestone: GiteeEventMilestone) = with(milestone) {
        Milestone(
            id = id.toInt(),
            title = title,
            description = description,
            state = state,
            iid = number.toInt(),
            dueDate = try {
                DateUtils.convertDateToLocalDateTime(
                    SimpleDateFormat(DateFormatConstants.YYYY_MM_DD).parse(dueOn)
                )
            } catch (e: ParseException) {
                null
            }
        )
    }

    fun convertChange(diff: GiteePullRequestDiff) = with(diff) {
        Change(
            path = filename,
            added = patch.newFile,
            renamed = patch.renamedFile,
            deleted = patch.deletedFile,
            blobId = sha,
            sha = sha,
            oldPath = patch.oldPath
        )
    }

    fun convertChange(commit: GiteeCommitDetail) = commit.files.map { file ->
        with(file) {
            Change(
                sha = sha,
                path = filename,
                blobId = blobUrl,
                deleted = "removed" == status,
                added = "added" == status,
                renamed = false
            )
        }
    }

    /*========================================commit====================================================*/
    fun convertCommit(commit: GiteeEventCommit) = with(commit) {
        Commit(
            sha = id,
            message = message,
            author = convertSignature(author),
            committer = convertSignature(committer),
            link = url,
            added = added,
            modified = modified,
            removed = removed,
            commitTime = DateUtils.convertDateToLocalDateTime(timestamp)
        )
    }

    fun convertCommit(form: GiteeCommitDetail) = with(form) {
        val added = mutableSetOf<String>()
        val modified = mutableSetOf<String>()
        val removed = mutableSetOf<String>()

        files.forEach { file ->
            when (file.status) {
                "removed" -> removed.add(file.filename)
                "added" -> added.add(file.filename)
                else -> modified.add(file.filename)
            }
        }
        Commit(
            sha = sha,
            message = commit.message,
            author = convertSignature(author),
            committer = convertSignature(committer),
            link = url,
            added = added.toList(),
            modified = modified.toList(),
            removed = removed.toList(),
            commitTime = commit?.committer?.date?.let {
                DateUtils.convertDateToLocalDateTime(it)
            }
        )
    }

    /*========================================user====================================================*/
    fun convertSignature(user: GiteeBaseUser) = with(user) {
        Signature(
            name = name,
            email = email
        )
    }

    fun convertUser(user: GiteeBaseUser) = with(user) {
        User(
            id = id,
            name = name,
            email = email,
            username = user.name
        )
    }

    fun convertUser(user: GiteeEventAuthor) = with(user) {
        User(
            id = id,
            name = name,
            email = email,
            username = username
        )
    }

    fun convertUser(user: GiteeUserInfo) = with(user) {
        User(
            id = id,
            name = name,
            email = email,
            username = user.login
        )
    }

    /*========================================compare====================================================*/
    fun convertCompare(commitCompare: GiteeCommitCompare) = with(commitCompare) {
        files.map {
            Change(
                sha = it.sha,
                path = it.filename,
                blobId = it.blobUrl,
                renamed = false, // gitee 的webhook中暂时没办法区分出是rename操作
                deleted = "removed" == it.status,
                added = "added" == it.status
            )
        }
    }

    /*========================================hook====================================================*/
    fun convertHook(webhookConfig: GiteeWebhookConfig) = with(webhookConfig) {
        Hook(
            id = id,
            url = url,
            events = convertEvents(webhookConfig),
            active = true,
            name = ""
        )
    }

    private fun convertEvents(from: GiteeWebhookConfig) = with(from) {
        HookEvents(
            push = pushEvents,
            tag = tagPushEvents,
            pullRequest = mergeRequestsEvents,
            issue = issuesEvents,
            issueComment = noteEvents,
            pullRequestComment = noteEvents
        )
    }

    /*========================================check-run====================================================*/
    fun convertCheckRunInput(checkRun: CheckRunInput) = with(checkRun) {
        GiteeCheckRun.builder()
                .id(id)
                .name(name)
                .headSha(ref)
                .detailsUrl(detailsUrl)
                .startedAt(DateUtils.convertLocalDateTimeToDate(startedAt))
                .status(GiteeCheckRunStatus.IN_PROGRESS)
                .output(
                    GiteeCheckRunOutput.builder()
                            .text(output?.text)
                            .summary(output?.summary)
                            .title(output?.title)
                            .build()
                )
                .status(convertCheckRunStatus(status))
                .conclusion(convertCheckRunConclusion(conclusion))
                .pullRequestId(pullRequestId)
                .completedAt(DateUtils.convertLocalDateTimeToDate(completedAt))
                .build()
    }

    fun convertCheckRun(checkRun: GiteeCheckRun) = with(checkRun) {
        CheckRun(
            id = id,
            name = name,
            status = convertCheckRunStatus(status),
            conclusion = convertCheckRunConclusion(conclusion),
            detail = output?.text,
            summary = output?.summary,
            detailsUrl = detailsUrl
        )
    }

    private fun convertCheckRunStatus(status: CheckRunStatus) = when (status) {
        CheckRunStatus.COMPLETED -> GiteeCheckRunStatus.COMPLETED
        CheckRunStatus.IN_PROGRESS -> GiteeCheckRunStatus.IN_PROGRESS
        else -> GiteeCheckRunStatus.QUEUED
    }

    private fun convertCheckRunConclusion(conclusion: CheckRunConclusion?) = conclusion?.let {
        when (conclusion) {
            CheckRunConclusion.SUCCESS -> GiteeCheckRunConclusion.SUCCESS
            CheckRunConclusion.FAILURE -> GiteeCheckRunConclusion.FAILURE
            CheckRunConclusion.CANCELLED -> GiteeCheckRunConclusion.CANCELLED
            CheckRunConclusion.TIMED_OUT -> GiteeCheckRunConclusion.TIMED_OUT
            CheckRunConclusion.SKIPPED -> GiteeCheckRunConclusion.SKIPPED
            CheckRunConclusion.ACTION_REQUIRED -> GiteeCheckRunConclusion.ACTION_REQUIRED
            else -> GiteeCheckRunConclusion.NEUTRAL
        }
    }

    private fun convertCheckRunStatus(status: GiteeCheckRunStatus) = when (status) {
        GiteeCheckRunStatus.COMPLETED -> CheckRunStatus.COMPLETED
        GiteeCheckRunStatus.IN_PROGRESS -> CheckRunStatus.IN_PROGRESS
        GiteeCheckRunStatus.QUEUED -> CheckRunStatus.QUEUED
        else -> CheckRunStatus.UNKNOWN
    }

    private fun convertCheckRunConclusion(conclusion: GiteeCheckRunConclusion?) = conclusion?.let {
        when (conclusion) {
            GiteeCheckRunConclusion.SUCCESS -> CheckRunConclusion.SUCCESS
            GiteeCheckRunConclusion.FAILURE -> CheckRunConclusion.FAILURE
            GiteeCheckRunConclusion.CANCELLED -> CheckRunConclusion.CANCELLED
            GiteeCheckRunConclusion.TIMED_OUT -> CheckRunConclusion.TIMED_OUT
            GiteeCheckRunConclusion.SKIPPED -> CheckRunConclusion.SKIPPED
            GiteeCheckRunConclusion.ACTION_REQUIRED -> CheckRunConclusion.ACTION_REQUIRED
            else -> CheckRunConclusion.UNKNOWN
        }
    }

    /*========================================file content====================================================*/
    fun convertFileContent(path: String, form: String) = with(form) {
        Content(
            path = path,
            content = form,
            sha = "",
            blobId = ""
        )
    }
}
