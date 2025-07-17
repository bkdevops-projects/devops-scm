package com.tencent.devops.scm.provider.git.tgit

import com.tencent.devops.scm.api.constant.DateFormatConstants
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_HOOK_MR_COMMITTER
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_HOOK_MR_ID
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_MANUAL_UNLOCK
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_ASSIGNEE
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_AUTHOR
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
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_REVIEW_ID
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_REVIEW_IID
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_REVIEW_OWNER
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_REVIEW_STATE
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_ACTION
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_BASE_REF
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_BEFORE_SHA
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_BEFORE_SHA_SHORT
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_HEAD_REF
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_MR_ACTION
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_MR_DESC
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_MR_ID
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_MR_IID
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_MR_PROPOSER
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_MR_TITLE
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_MR_URL
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_WEBHOOK_SOURCE_BRANCH
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_WEBHOOK_SOURCE_PROJECT_ID
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_WEBHOOK_TARGET_BRANCH
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_WEBHOOK_TARGET_PROJECT_ID
import com.tencent.devops.scm.api.constant.WebhookOutputConstants.PR_DESC_MAX_LENGTH
import com.tencent.devops.scm.api.pojo.repository.git.GitScmServerRepository
import com.tencent.devops.scm.provider.git.utils.CollectionUtils.putMultipleKeys
import com.tencent.devops.scm.sdk.tgit.pojo.TGitMergeRequest
import com.tencent.devops.scm.sdk.tgit.pojo.TGitReview
import com.tencent.devops.scm.sdk.tgit.pojo.webhook.TGitEventMergeRequest
import com.tencent.devops.scm.sdk.tgit.pojo.webhook.TGitMergeRequestEvent
import com.tencent.devops.scm.sdk.tgit.pojo.webhook.TGitNoteEvent
import org.apache.commons.lang3.StringUtils
import java.text.SimpleDateFormat
import java.util.TimeZone

/**
 * TGitObject 转换成 Map
 * 根据API查询结果，组装Webhook参数
 */
object TGitObjectToMapConverter {

    private val simpleDateFormat = SimpleDateFormat(DateFormatConstants.ISO_8601).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    /**
     * 获取merge request 相关触发参数
     */
    fun convertPullRequest(
        mergeRequest: TGitMergeRequest?,
        scmServerRepository: GitScmServerRepository
    ): Map<String, Any> {
        val params = mutableMapOf<String, Any>()
        mergeRequest?.let { mr ->
            // 基本MR信息
            // 目前HEAD_REF和BASE_REF分别代表目标分支和源分支
            params.putMultipleKeys(
                mr.sourceBranch ?: "",
                setOf(
                    PIPELINE_WEBHOOK_SOURCE_BRANCH,
                    BK_REPO_GIT_WEBHOOK_MR_SOURCE_BRANCH,
                    PIPELINE_GIT_BASE_REF
                )
            )
            params.putMultipleKeys(
                mr.targetBranch ?: "",
                setOf(
                    PIPELINE_WEBHOOK_TARGET_BRANCH,
                    BK_REPO_GIT_WEBHOOK_MR_TARGET_BRANCH,
                    PIPELINE_GIT_HEAD_REF
                )
            )
            params.putMultipleKeys(
                mr.id?.toString() ?: "",
                setOf(
                    PIPELINE_GIT_MR_ID,
                    BK_HOOK_MR_ID,
                    BK_REPO_GIT_WEBHOOK_MR_ID
                )
            )
            params.putMultipleKeys(
                mr.iid?.toString() ?: "",
                setOf(
                    BK_REPO_GIT_WEBHOOK_MR_NUMBER,
                    PIPELINE_GIT_MR_IID
                )
            )
            params.putMultipleKeys(
                mr.author?.username ?: "",
                setOf(
                    BK_HOOK_MR_COMMITTER,
                    BK_REPO_GIT_WEBHOOK_MR_AUTHOR,
                    PIPELINE_GIT_MR_PROPOSER
                )
            )
            params[PIPELINE_WEBHOOK_SOURCE_PROJECT_ID] = mr.sourceProjectId?.toString() ?: ""
            params[PIPELINE_WEBHOOK_TARGET_PROJECT_ID] = mr.targetProjectId?.toString() ?: ""
            val mrDesc = StringUtils.substring(mr.description ?: "", 0, PR_DESC_MAX_LENGTH)
            params.putMultipleKeys(
                mrDesc,
                setOf(
                    BK_REPO_GIT_WEBHOOK_MR_DESCRIPTION,
                    PIPELINE_GIT_MR_DESC
                )
            )
            params.putMultipleKeys(
                mr.title ?: "",
                setOf(
                    BK_REPO_GIT_WEBHOOK_MR_TITLE,
                    PIPELINE_GIT_MR_TITLE
                )
            )

            // 时间
            params[BK_REPO_GIT_WEBHOOK_MR_CREATE_TIME] = simpleDateFormat.format(mr.createdAt)
            params[BK_REPO_GIT_WEBHOOK_MR_CREATE_TIMESTAMP] = mr.createdAt.time

            params[BK_REPO_GIT_WEBHOOK_MR_UPDATE_TIME] = simpleDateFormat.format(mr.updatedAt.time)
            params[BK_REPO_GIT_WEBHOOK_MR_UPDATE_TIMESTAMP] = mr.updatedAt.time

            params[BK_REPO_GIT_WEBHOOK_MR_ASSIGNEE] = mr.assignee?.username ?: ""

            // 里程碑信息
            mr.milestone?.let { milestone ->
                params[BK_REPO_GIT_WEBHOOK_MR_MILESTONE] = milestone.title ?: ""
                params[BK_REPO_GIT_WEBHOOK_MR_MILESTONE_ID] = milestone.id?.toString() ?: ""
                params[BK_REPO_GIT_WEBHOOK_MR_MILESTONE_DUE_DATE] =
                    SimpleDateFormat("yyyy-MM-dd").format(milestone.dueDate)
            }

            params[BK_REPO_GIT_WEBHOOK_MR_LABELS] = mr.labels?.joinToString(",") ?: ""
            params[BK_REPO_GIT_WEBHOOK_MR_BASE_COMMIT] = mr.baseCommit ?: ""
            params[BK_REPO_GIT_WEBHOOK_MR_TARGET_COMMIT] = mr.targetCommit ?: ""
            params[BK_REPO_GIT_WEBHOOK_MR_SOURCE_COMMIT] = mr.sourceCommit ?: ""

            // MR 链接
            if (scmServerRepository.webUrl.isNotBlank() && mr.iid != null) {
                params[PIPELINE_GIT_MR_URL] = "${scmServerRepository.webUrl}/merge_requests/${mr.iid}"
            }
        }
        return params
    }

    /**
     * 获取 review 相关触发参数
     */
    fun convertReview(review: TGitReview?): Map<String, Any> {
        val params = mutableMapOf<String, Any>()
        review?.let { r ->
            params[BK_REPO_GIT_WEBHOOK_MR_REVIEWERS] = r.reviewers
                    ?.joinToString(",") { it.username }
                ?: ""
            params[BK_REPO_GIT_WEBHOOK_REVIEW_STATE] = r.state?.toValue() ?: ""
            params[BK_REPO_GIT_WEBHOOK_REVIEW_OWNER] = r.author?.username ?: ""
            params[BK_REPO_GIT_WEBHOOK_REVIEW_ID] = r.id
            params[BK_REPO_GIT_WEBHOOK_REVIEW_IID] = r.iid
        }
        return params
    }

    /**
     * 获取 merge request 基础触发参数
     */
    fun convertMergeRequestEvent(mergeRequestEvent: TGitMergeRequestEvent): MutableMap<String, Any> {
        val params = mutableMapOf<String, Any>()
        mergeRequestEvent.objectAttributes?.let { attr ->
            params[BK_REPO_GIT_MANUAL_UNLOCK] = mergeRequestEvent.manualUnlock
            params[PIPELINE_GIT_MR_ACTION] = attr.action
            params[PIPELINE_GIT_ACTION] = attr.action
            params.putMultipleKeys(
                attr.title ?: "",
                setOf(
                    BK_REPO_GIT_WEBHOOK_MR_TITLE,
                    PIPELINE_GIT_MR_TITLE
                )
            )
            params.putMultipleKeys(
                attr.sourceBranch ?: "",
                setOf(
                    PIPELINE_GIT_BASE_REF,
                    PIPELINE_WEBHOOK_SOURCE_BRANCH,
                    BK_REPO_GIT_WEBHOOK_MR_SOURCE_BRANCH
                )
            )
            params.putMultipleKeys(
                attr.targetBranch ?: "",
                setOf(
                    PIPELINE_GIT_HEAD_REF,
                    PIPELINE_WEBHOOK_TARGET_BRANCH,
                    BK_REPO_GIT_WEBHOOK_MR_TARGET_BRANCH
                )
            )
            params.putMultipleKeys(
                attr.id?.toString() ?: "",
                setOf(
                    BK_HOOK_MR_ID,
                    PIPELINE_GIT_MR_ID,
                    BK_REPO_GIT_WEBHOOK_MR_ID
                )
            )
            params.putMultipleKeys(
                attr.iid?.toString() ?: "",
                setOf(
                    PIPELINE_GIT_MR_IID,
                    BK_REPO_GIT_WEBHOOK_MR_NUMBER
                )
            )
            params[PIPELINE_GIT_MR_PROPOSER] = mergeRequestEvent.user.username
            params[PIPELINE_GIT_MR_URL] = attr.url ?: ""
            params[PIPELINE_WEBHOOK_SOURCE_PROJECT_ID] = attr.sourceProjectId?.toString() ?: ""
            params[PIPELINE_WEBHOOK_TARGET_PROJECT_ID] = attr.targetProjectId?.toString() ?: ""
            attr.createdAt?.let {
                params[BK_REPO_GIT_WEBHOOK_MR_CREATE_TIMESTAMP] = it.time
                params[BK_REPO_GIT_WEBHOOK_MR_CREATE_TIME] = simpleDateFormat.format(it)
            }
            attr.updatedAt?.let {
                params[BK_REPO_GIT_WEBHOOK_MR_UPDATE_TIMESTAMP] = it.time
                params[BK_REPO_GIT_WEBHOOK_MR_UPDATE_TIME] = simpleDateFormat.format(it)
            }
        }
        return params
    }

    /**
     * 获取 merge request 基础触发参数
     */
    fun convertMergeRequestEvent(
        scmServerRepository: GitScmServerRepository,
        eventMergeRequest: TGitEventMergeRequest
    ): Map<String, Any> {
        with(eventMergeRequest) {
            val params = mutableMapOf<String, Any>()
            params[PIPELINE_GIT_MR_ACTION] = action ?: ""
            params[BK_HOOK_MR_ID] = id ?: ""
            params[PIPELINE_GIT_MR_URL] = url ?: getMergeRequestUrl(scmServerRepository.webUrl, iid)
            return params
        }
    }

    fun convertNoteEvent(
        src: TGitNoteEvent
    ): MutableMap<String, Any> {
        with(src.objectAttributes) {
            val params = mutableMapOf<String, Any>()
            params[BK_REPO_GIT_MANUAL_UNLOCK] = false
            params[PIPELINE_GIT_BEFORE_SHA] = "----------"
            params[PIPELINE_GIT_BEFORE_SHA_SHORT] = "----------"
            params[PIPELINE_GIT_MR_ACTION] = action
            params[BK_REPO_GIT_WEBHOOK_NOTE_CREATED_AT] = simpleDateFormat.format(createdAt)
            params[BK_REPO_GIT_WEBHOOK_NOTE_UPDATED_AT] = simpleDateFormat.format(updatedAt)
            return params
        }
    }

    fun getMergeRequestUrl(homeUrl: String, iid: Int) = "$homeUrl/merge_requests/$iid"
}
