package com.tencent.devops.scm.api.util

import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_PUSH_ADD_FILE_COUNT
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_PUSH_ADD_FILE_PREFIX
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_PUSH_COMMIT_AUTHOR_PREFIX
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_PUSH_COMMIT_MSG_PREFIX
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_PUSH_COMMIT_PREFIX
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_PUSH_COMMIT_TIMESTAMP_PREFIX
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_PUSH_DELETE_FILE_COUNT
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_PUSH_DELETE_FILE_PREFIX
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_PUSH_MODIFY_FILE_COUNT
import com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_PUSH_MODIFY_FILE_PREFIX
import com.tencent.devops.scm.api.constant.WebhookOutputConstants.MAX_VARIABLE_COUNT
import com.tencent.devops.scm.api.pojo.Commit
import org.apache.commons.lang3.StringUtils
import java.time.ZoneId

/**
 * Git工具类
 */
object GitUtils {
    /**
     * 扩展引用名称
     * @param name 原始名称
     * @param prefix 前缀
     * @return 扩展后的名称
     */
    fun expandRef(name: String, prefix: String): String {
        val trimmedPrefix = StringUtils.removeEnd(prefix, "/")
        return if (StringUtils.startsWith(name, trimmedPrefix)) {
            name
        } else {
            "$trimmedPrefix/$name"
        }
    }

    /**
     * 移除分支或tag前缀
     * @param ref 分支或tag引用
     * @return 处理后的引用
     */
    fun trimRef(ref: String): String {
        var result = StringUtils.removeStart(ref, "refs/heads/")
        result = StringUtils.removeStart(result, "refs/tags/")
        return result
    }

    /**
     * 获取简短的提交SHA
     * @param commitId 完整提交ID
     * @return 前8位字符的提交ID
     */
    fun getShortSha(commitId: String?): String {
        return if (commitId != null && commitId.length > 8) {
            commitId.substring(0, 8)
        } else {
            ""
        }
    }

    /**
     * 获取提交变更信息
     * @param commits 提交记录列表
     * @return 包含提交变更信息的Map
     */
    fun getOutputCommitIndexVar(commits: List<Commit>): Map<String, Any> {
        val startParams = mutableMapOf<String, Any>()
        var addCount = 0
        var modifyCount = 0
        var deleteCount = 0

        commits.forEachIndexed { index, gitCommit ->
            val curIndex = index + 1
            startParams.apply {
                put("$BK_REPO_GIT_WEBHOOK_PUSH_COMMIT_PREFIX$curIndex", gitCommit.sha)
                put("$BK_REPO_GIT_WEBHOOK_PUSH_COMMIT_MSG_PREFIX$curIndex", gitCommit.message)
                put(
                    "$BK_REPO_GIT_WEBHOOK_PUSH_COMMIT_TIMESTAMP_PREFIX$curIndex",
                    gitCommit.commitTime.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000
                )
                put("$BK_REPO_GIT_WEBHOOK_PUSH_COMMIT_AUTHOR_PREFIX$curIndex", gitCommit.author)
            }

            addCount += gitCommit.added.size
            modifyCount += gitCommit.modified.size
            deleteCount += gitCommit.removed.size

            var count = 0

            gitCommit.added.forEachIndexed { innerIndex, file ->
                if (count <= MAX_VARIABLE_COUNT) {
                    startParams["$BK_REPO_GIT_WEBHOOK_PUSH_ADD_FILE_PREFIX${curIndex}_${innerIndex + 1}"] = file
                    count++
                }
            }

            gitCommit.modified.forEachIndexed { innerIndex, file ->
                if (count <= MAX_VARIABLE_COUNT) {
                    startParams["$BK_REPO_GIT_WEBHOOK_PUSH_MODIFY_FILE_PREFIX${curIndex}_${innerIndex + 1}"] = file
                    count++
                }
            }

            gitCommit.removed.forEachIndexed { innerIndex, file ->
                if (count <= MAX_VARIABLE_COUNT) {
                    startParams["$BK_REPO_GIT_WEBHOOK_PUSH_DELETE_FILE_PREFIX${curIndex}_${innerIndex + 1}"] = file
                    count++
                }
            }
        }

        startParams[BK_REPO_GIT_WEBHOOK_PUSH_ADD_FILE_COUNT] = addCount
        startParams[BK_REPO_GIT_WEBHOOK_PUSH_MODIFY_FILE_COUNT] = modifyCount
        startParams[BK_REPO_GIT_WEBHOOK_PUSH_DELETE_FILE_COUNT] =  deleteCount

        return startParams
    }
}
