package com.tencent.devops.scm.provider.git.gitee

import com.tencent.devops.scm.api.constant.DateFormatConstants
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_HOOK_MR_COMMITTER
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_HOOK_MR_ID
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
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_SOURCE_BRANCH
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_SOURCE_COMMIT
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_TARGET_BRANCH
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_TARGET_COMMIT
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_TITLE
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_UPDATE_TIME
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_UPDATE_TIMESTAMP
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_BASE_REF
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_HEAD_REF
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_MR_DESC
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_MR_ID
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_MR_IID
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_MR_PROPOSER
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_MR_TITLE
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_WEBHOOK_SOURCE_BRANCH
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_WEBHOOK_SOURCE_PROJECT_ID
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_WEBHOOK_TARGET_BRANCH
import com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_WEBHOOK_TARGET_PROJECT_ID
import com.tencent.devops.scm.api.constant.WebhookOutputConstants
import com.tencent.devops.scm.provider.git.utils.CollectionUtils.putMultipleKeys
import com.tencent.devops.scm.sdk.gitee.pojo.GiteePullRequest
import org.apache.commons.lang3.StringUtils
import java.text.SimpleDateFormat

/**
 * GiteeObject 转换成 Map
 * 根据API查询结果，组装Webhook参数
 */
object GiteeObjectToMapConverter {

    /**
     * 获取merge request 相关触发参数
     */
    fun convertPullRequest(pullRequest: GiteePullRequest): Map<String, Any> {
        return with(pullRequest) {
            val params = mutableMapOf<String, Any>()
            // 基本MR信息
            params.putMultipleKeys(
                head?.ref ?: "",
                setOf(
                    PIPELINE_WEBHOOK_SOURCE_BRANCH,
                    BK_REPO_GIT_WEBHOOK_MR_SOURCE_BRANCH,
                    PIPELINE_GIT_BASE_REF
                )
            )
            params.putMultipleKeys(
                base?.ref ?: "",
                setOf(
                    PIPELINE_WEBHOOK_TARGET_BRANCH,
                    BK_REPO_GIT_WEBHOOK_MR_TARGET_BRANCH,
                    PIPELINE_GIT_HEAD_REF
                )
            )
            params.putMultipleKeys(
                id ?: "",
                setOf(
                    PIPELINE_GIT_MR_ID,
                    BK_HOOK_MR_ID,
                    BK_REPO_GIT_WEBHOOK_MR_ID
                )
            )
            params.putMultipleKeys(
                number ?: "",
                setOf(
                    BK_REPO_GIT_WEBHOOK_MR_NUMBER,
                    PIPELINE_GIT_MR_IID
                )
            )
            params.putMultipleKeys(
                user?.name ?: "",
                setOf(
                    BK_HOOK_MR_COMMITTER,
                    BK_REPO_GIT_WEBHOOK_MR_AUTHOR,
                    PIPELINE_GIT_MR_PROPOSER
                )
            )
            params[PIPELINE_WEBHOOK_SOURCE_PROJECT_ID] = head?.repo?.id ?: ""
            params[PIPELINE_WEBHOOK_TARGET_PROJECT_ID] = base?.repo?.id ?: ""
            val mrDesc = StringUtils.substring(
                body ?: "",
                0,
                WebhookOutputConstants.PR_DESC_MAX_LENGTH
            )
            params.putMultipleKeys(
                mrDesc,
                setOf(
                    BK_REPO_GIT_WEBHOOK_MR_DESCRIPTION,
                    PIPELINE_GIT_MR_DESC
                )
            )
            params.putMultipleKeys(
                title ?: "",
                setOf(
                    BK_REPO_GIT_WEBHOOK_MR_TITLE,
                    PIPELINE_GIT_MR_TITLE
                )
            )
            params.putMultipleKeys(
                base?.sha ?: "",
                setOf(
                    BK_REPO_GIT_WEBHOOK_MR_BASE_COMMIT,
                    BK_REPO_GIT_WEBHOOK_MR_TARGET_COMMIT
                )
            )
            params[BK_REPO_GIT_WEBHOOK_MR_SOURCE_COMMIT] = head?.sha ?: ""
            // 时间
            params[BK_REPO_GIT_WEBHOOK_MR_CREATE_TIME] =
                SimpleDateFormat(DateFormatConstants.ISO_8601).format(createdAt)
            params[BK_REPO_GIT_WEBHOOK_MR_CREATE_TIMESTAMP] = createdAt.time
            params[BK_REPO_GIT_WEBHOOK_MR_UPDATE_TIME] =
                SimpleDateFormat(DateFormatConstants.ISO_8601).format(updatedAt.time)
            params[BK_REPO_GIT_WEBHOOK_MR_UPDATE_TIMESTAMP] = updatedAt.time
            // 指派人
            params[BK_REPO_GIT_WEBHOOK_MR_ASSIGNEE] = assignees.firstOrNull()?.name ?: ""
            // 里程碑信息
            val milestone = milestone
            milestone?.let {
                params[BK_REPO_GIT_WEBHOOK_MR_MILESTONE] = it.title ?: ""

                params[BK_REPO_GIT_WEBHOOK_MR_MILESTONE_ID] = it.id?.toString() ?: ""

                params[BK_REPO_GIT_WEBHOOK_MR_MILESTONE_DUE_DATE] = it.dueOn
            }
            // 标签
            params[BK_REPO_GIT_WEBHOOK_MR_LABELS] =
                pullRequest?.labels
                        ?.filterNotNull()
                        ?.toSet()
                        ?.joinToString(separator = ",") { it.name } ?: ""

            params
        }
    }
}
