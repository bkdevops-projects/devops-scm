package com.tencent.devops.scm.api.pojo

import com.tencent.devops.scm.api.pojo.repository.git.GitScmServerRepository
import java.time.LocalDateTime

/**
 * 表示pr/mr
 */
data class PullRequest(
    // PR/MR ID
    val id: Long,
    // PR/MR 编号
    val number: Int,
    // 标题
    var title: String,
    // 正文内容
    val body: String,
    // 链接
    val link: String,

    // 提交SHA
    val sha: String? = null,
    // 引用
    val ref: String? = null,
    // 源仓库
    val sourceRepo: GitScmServerRepository,
    // 目标仓库
    val targetRepo: GitScmServerRepository,
    // 目标分支
    val targetRef: Reference,
    // 源分支
    val sourceRef: Reference,

    // 是否已关闭
    val closed: Boolean = false,
    // 是否已合并
    val merged: Boolean = false,
    // 合并类型
    val mergeType: String? = null,
    // 合并后的commitId
    val mergeCommitSha: String? = null,

    // 作者
    val author: User? = null,
    // 创建时间
    val created: LocalDateTime? = null,
    // 更新时间
    val updated: LocalDateTime? = null,
    // 标签列表
    val labels: List<String>? = null,
    // 描述
    val description: String? = null,
    // 里程碑
    val milestone: Milestone? = null,
    // 基础提交
    val baseCommit: String? = null,
    // 分配人列表
    val assignee: List<User>? = null
)
