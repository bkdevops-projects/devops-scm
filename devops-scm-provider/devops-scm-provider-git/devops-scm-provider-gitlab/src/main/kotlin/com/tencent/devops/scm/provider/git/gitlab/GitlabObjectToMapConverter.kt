package com.tencent.devops.scm.provider.git.gitlab

import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_HOOK_MR_ID
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_HOOK_MR_COMMITTER
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_MANUAL_UNLOCK
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_AUTHOR
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_ACTION
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_ASSIGNEE
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_BASE_COMMIT
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_CREATE_TIME
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_CREATE_TIMESTAMP
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_DESCRIPTION
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_ID
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_LABELS
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_MILESTONE
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_MILESTONE_DUE_DATE
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_MILESTONE_ID
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_NUMBER
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_REVIEWERS
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_SOURCE_BRANCH
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_SOURCE_COMMIT
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_TARGET_BRANCH
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_TARGET_COMMIT
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_TITLE
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_UPDATE_TIME
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_UPDATE_TIMESTAMP
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_NOTE_CREATED_AT
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_NOTE_UPDATED_AT
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_ACTION
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_HEAD_REF
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_MR_ID
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_MR_IID
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_MR_PROPOSER
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_MR_TITLE
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_MR_URL
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_BASE_REF
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_MR_DESC
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_WEBHOOK_SOURCE_BRANCH
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_WEBHOOK_SOURCE_PROJECT_ID
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_WEBHOOK_TARGET_BRANCH
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_WEBHOOK_TARGET_PROJECT_ID
import com.tencent.devops.scm.api.pojo.PullRequest
import com.tencent.devops.scm.api.pojo.User
import com.tencent.devops.scm.api.pojo.repository.git.GitScmServerRepository
import com.tencent.devops.scm.api.constant.DateFormatConstants
import com.tencent.devops.scm.api.constant.WebhookOutputConstants.PR_DESC_MAX_LENGTH
import com.tencent.devops.scm.sdk.gitlab.pojo.GitlabMergeRequest
import org.apache.commons.lang3.StringUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

object GitlabObjectToMapConverter {
    fun convertPullRequest(
        pullRequest: PullRequest,
        action: String,
        sender: User
    ): MutableMap<String, Any> = mutableMapOf(
        BK_HOOK_MR_ID to pullRequest.id,
        BK_REPO_GIT_WEBHOOK_MR_ID to pullRequest.id,
        BK_REPO_GIT_WEBHOOK_MR_NUMBER to pullRequest.number,
        BK_REPO_GIT_WEBHOOK_MR_TITLE to pullRequest.title,
        BK_REPO_GIT_WEBHOOK_MR_DESCRIPTION to pullRequest.body,
        BK_REPO_GIT_WEBHOOK_MR_AUTHOR to pullRequest.author?.username.orEmpty(),
        BK_REPO_GIT_WEBHOOK_MR_ACTION to action,
        BK_REPO_GIT_WEBHOOK_MR_SOURCE_BRANCH to pullRequest.sourceRef.name,
        BK_REPO_GIT_WEBHOOK_MR_TARGET_BRANCH to pullRequest.targetRef.name,
        PIPELINE_GIT_MR_IID to pullRequest.number,
        PIPELINE_GIT_MR_ID to pullRequest.id,
        PIPELINE_GIT_MR_URL to pullRequest.link,
        PIPELINE_GIT_MR_TITLE to pullRequest.title,
        PIPELINE_GIT_MR_PROPOSER to sender.username,
        PIPELINE_GIT_HEAD_REF to pullRequest.targetRef.name,
        PIPELINE_WEBHOOK_SOURCE_PROJECT_ID to pullRequest.sourceRepo.id,
        PIPELINE_WEBHOOK_TARGET_PROJECT_ID to pullRequest.targetRepo.id,
        PIPELINE_WEBHOOK_SOURCE_BRANCH to pullRequest.sourceRef.name,
        PIPELINE_WEBHOOK_TARGET_BRANCH to pullRequest.targetRef.name,
        PIPELINE_GIT_ACTION to action,
        BK_REPO_GIT_MANUAL_UNLOCK to false
    )

    fun convertNote(createdAt: String?, updatedAt: String?): MutableMap<String, Any> = mutableMapOf(
        BK_REPO_GIT_WEBHOOK_NOTE_CREATED_AT to createdAt.orEmpty(),
        BK_REPO_GIT_WEBHOOK_NOTE_UPDATED_AT to updatedAt.orEmpty(),
        BK_REPO_GIT_MANUAL_UNLOCK to false
    )

    fun convertPullRequest(
        mergeRequest: GitlabMergeRequest,
        repository: GitScmServerRepository
    ): Map<String, Any> = mutableMapOf<String, Any>().apply {
        val id = mergeRequest.id?.toString().orEmpty()
        val iid = mergeRequest.iid?.toString().orEmpty()
        val author = mergeRequest.author?.username.orEmpty()
        val description = StringUtils.substring(mergeRequest.description.orEmpty(), 0, PR_DESC_MAX_LENGTH)

        put(BK_HOOK_MR_ID, id)
        put(BK_REPO_GIT_WEBHOOK_MR_ID, id)
        put(PIPELINE_GIT_MR_ID, id)
        put(BK_REPO_GIT_WEBHOOK_MR_NUMBER, iid)
        put(PIPELINE_GIT_MR_IID, iid)
        put(BK_HOOK_MR_COMMITTER, author)
        put(BK_REPO_GIT_WEBHOOK_MR_AUTHOR, author)
        put(PIPELINE_GIT_MR_PROPOSER, author)
        put(BK_REPO_GIT_WEBHOOK_MR_TITLE, mergeRequest.title.orEmpty())
        put(PIPELINE_GIT_MR_TITLE, mergeRequest.title.orEmpty())
        put(BK_REPO_GIT_WEBHOOK_MR_DESCRIPTION, description)
        put(PIPELINE_GIT_MR_DESC, description)
        put(BK_REPO_GIT_WEBHOOK_MR_SOURCE_BRANCH, mergeRequest.sourceBranch.orEmpty())
        put(PIPELINE_WEBHOOK_SOURCE_BRANCH, mergeRequest.sourceBranch.orEmpty())
        put(PIPELINE_GIT_BASE_REF, mergeRequest.sourceBranch.orEmpty())
        put(BK_REPO_GIT_WEBHOOK_MR_TARGET_BRANCH, mergeRequest.targetBranch.orEmpty())
        put(PIPELINE_WEBHOOK_TARGET_BRANCH, mergeRequest.targetBranch.orEmpty())
        put(PIPELINE_GIT_HEAD_REF, mergeRequest.targetBranch.orEmpty())
        put(PIPELINE_WEBHOOK_SOURCE_PROJECT_ID, mergeRequest.sourceProjectId?.toString().orEmpty())
        put(PIPELINE_WEBHOOK_TARGET_PROJECT_ID, mergeRequest.targetProjectId?.toString().orEmpty())
        put(BK_REPO_GIT_WEBHOOK_MR_ASSIGNEE, mergeRequest.assignee?.username.orEmpty())
        put(BK_REPO_GIT_WEBHOOK_MR_REVIEWERS, mergeRequest.reviewers.orEmpty().joinToString(",") { it.username.orEmpty() })
        put(BK_REPO_GIT_WEBHOOK_MR_LABELS, mergeRequest.labels.orEmpty().joinToString(","))
        put(BK_REPO_GIT_WEBHOOK_MR_BASE_COMMIT, mergeRequest.diffRefs?.baseSha.orEmpty())
        put(BK_REPO_GIT_WEBHOOK_MR_TARGET_COMMIT, mergeRequest.diffRefs?.startSha.orEmpty())
        put(BK_REPO_GIT_WEBHOOK_MR_SOURCE_COMMIT, mergeRequest.diffRefs?.headSha ?: mergeRequest.sha.orEmpty())

        mergeRequest.createdAt?.let {
            put(BK_REPO_GIT_WEBHOOK_MR_CREATE_TIME, formatDate(it))
            put(BK_REPO_GIT_WEBHOOK_MR_CREATE_TIMESTAMP, it.time)
        }
        mergeRequest.updatedAt?.let {
            put(BK_REPO_GIT_WEBHOOK_MR_UPDATE_TIME, formatDate(it))
            put(BK_REPO_GIT_WEBHOOK_MR_UPDATE_TIMESTAMP, it.time)
        }
        mergeRequest.milestone?.let { milestone ->
            put(BK_REPO_GIT_WEBHOOK_MR_MILESTONE, milestone.title.orEmpty())
            put(BK_REPO_GIT_WEBHOOK_MR_MILESTONE_ID, milestone.id?.toString().orEmpty())
            milestone.dueDate?.let { put(BK_REPO_GIT_WEBHOOK_MR_MILESTONE_DUE_DATE, formatDueDate(it)) }
        }

        val url = mergeRequest.webUrl.orEmpty().ifBlank {
            if (repository.webUrl.isNotBlank() && mergeRequest.iid != null) {
                "${repository.webUrl}/-/merge_requests/${mergeRequest.iid}"
            } else {
                ""
            }
        }
        put(PIPELINE_GIT_MR_URL, url)
    }

    private fun formatDate(date: Date): String = SimpleDateFormat(DateFormatConstants.ISO_8601).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }.format(date)

    private fun formatDueDate(date: Date): String = SimpleDateFormat("yyyy-MM-dd").apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }.format(date)
}
