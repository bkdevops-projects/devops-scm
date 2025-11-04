package com.tencent.devops.scm.provider.git.bkcode

import com.tencent.devops.scm.api.constant.DateFormatConstants
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_HOOK_MR_COMMITTER
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_HOOK_MR_ID
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_MANUAL_UNLOCK
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_AUTHOR
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_BASE_COMMIT
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_CREATE_TIME
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_CREATE_TIMESTAMP
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_DESCRIPTION
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_ID
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_LABELS
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_NUMBER
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
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeMergeRequest
import com.tencent.devops.scm.sdk.bkcode.pojo.webhook.BkCodeEventMergeRequest
import com.tencent.devops.scm.sdk.bkcode.pojo.webhook.BkCodeMergeRequestEvent
import com.tencent.devops.scm.sdk.bkcode.pojo.webhook.BkCodeNoteEvent
import org.apache.commons.lang3.StringUtils
import java.text.SimpleDateFormat
import java.util.TimeZone

/**
 * BkCode 转换成 Map
 * 根据API查询结果，组装Webhook参数
 */
object BkCodeObjectToMapConverter {

    private val simpleDateFormat = SimpleDateFormat(DateFormatConstants.ISO_8601).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    /**
     * 获取merge request 相关触发参数
     */
    fun convertPullRequest(
        mergeRequest: BkCodeMergeRequest?,
        scmServerRepository: GitScmServerRepository
    ): Map<String, Any> {
        val params = mutableMapOf<String, Any>()
        mergeRequest?.let { mr ->
//             基本MR信息
//             目前HEAD_REF和BASE_REF分别代表目标分支和源分支
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
                mr.code?.toString() ?: "",
                setOf(
                    BK_REPO_GIT_WEBHOOK_MR_NUMBER,
                    PIPELINE_GIT_MR_IID
                )
            )
            params.putMultipleKeys(
                mr.creator?.username ?: "",
                setOf(
                    BK_HOOK_MR_COMMITTER,
                    BK_REPO_GIT_WEBHOOK_MR_AUTHOR,
                    PIPELINE_GIT_MR_PROPOSER
                )
            )
            params[PIPELINE_WEBHOOK_SOURCE_PROJECT_ID] = mr.sourceRepoId ?: ""
            params[PIPELINE_WEBHOOK_TARGET_PROJECT_ID] = mr.targetRepoId ?: ""
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
            mr.createTime?.let {
                params[BK_REPO_GIT_WEBHOOK_MR_CREATE_TIME] = simpleDateFormat.format(it)
                params[BK_REPO_GIT_WEBHOOK_MR_CREATE_TIMESTAMP] = it.time
            }
            mr.updateTime?.let{
                params[BK_REPO_GIT_WEBHOOK_MR_UPDATE_TIME] = simpleDateFormat.format(it)
                params[BK_REPO_GIT_WEBHOOK_MR_UPDATE_TIMESTAMP] = it.time
            }
            params[BK_REPO_GIT_WEBHOOK_MR_LABELS] = mr.labels?.joinToString(",") ?: ""
            params[BK_REPO_GIT_WEBHOOK_MR_BASE_COMMIT] = mr.baseCommitId ?: ""
            params[BK_REPO_GIT_WEBHOOK_MR_TARGET_COMMIT] = mr.baseCommitId ?: ""
            params[BK_REPO_GIT_WEBHOOK_MR_SOURCE_COMMIT] = mr.headCommitId ?: ""
            // MR 链接
            if (scmServerRepository.webUrl.isNotBlank() && mr.code != null) {
                params[PIPELINE_GIT_MR_URL] = "${scmServerRepository.webUrl}/merge_requests/${mr.code}"
            }
        }
        return params
    }

    /**
     * 获取 merge request 基础触发参数
     */
    fun convertMergeRequestEvent(mergeRequestEvent: BkCodeMergeRequestEvent): MutableMap<String, Any> {
        val params = mutableMapOf<String, Any>()
        params.putMultipleKeys(
            mergeRequestEvent.action ?: "",
            setOf(
                PIPELINE_GIT_MR_ACTION,
                PIPELINE_GIT_ACTION
            )
        )
        mergeRequestEvent.mergeRequest?.let { attr ->
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
                attr.number?.toString() ?: "",
                setOf(
                    PIPELINE_GIT_MR_IID,
                    BK_REPO_GIT_WEBHOOK_MR_NUMBER
                )
            )
            params[PIPELINE_GIT_MR_PROPOSER] = mergeRequestEvent.sender.username
            params[PIPELINE_GIT_MR_URL] = attr.url ?: ""
            params[PIPELINE_WEBHOOK_SOURCE_PROJECT_ID] = attr.sourceRepository.id?.toString() ?: ""
            params[PIPELINE_WEBHOOK_TARGET_PROJECT_ID] = attr.targetRepository.id?.toString() ?: ""
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
        eventMergeRequest: BkCodeEventMergeRequest
    ): Map<String, Any> {
        with(eventMergeRequest) {
            val params = mutableMapOf<String, Any>()
            params[BK_HOOK_MR_ID] = id ?: ""
            params[PIPELINE_GIT_MR_URL] = url ?: getMergeRequestUrl(scmServerRepository.webUrl, number.toInt() ?: 0)
            return params
        }
    }

    fun convertNoteEvent(
        src: BkCodeNoteEvent
    ): MutableMap<String, Any> {
        with(src) {
            val params = mutableMapOf<String, Any>()
            params[BK_REPO_GIT_MANUAL_UNLOCK] = false
            params[PIPELINE_GIT_BEFORE_SHA] = "----------"
            params[PIPELINE_GIT_BEFORE_SHA_SHORT] = "----------"
            params[BK_REPO_GIT_WEBHOOK_NOTE_CREATED_AT] = simpleDateFormat.format(createdAt)
            params[BK_REPO_GIT_WEBHOOK_NOTE_UPDATED_AT] = simpleDateFormat.format(src.comment.updatedAt)
            return params
        }
    }

    private fun getMergeRequestUrl(homeUrl: String, iid: Int) = "$homeUrl/merge_requests/$iid"
}
