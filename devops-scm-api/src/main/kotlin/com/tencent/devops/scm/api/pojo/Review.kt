package com.tencent.devops.scm.api.pojo

import com.tencent.devops.scm.api.enums.ReviewState

/**
 * 代码评审信息
 */
data class Review(
    // 评审ID (非空)
    val id: Long,
    // 工蜂评审事件的iid
    val iid: Int,
    // 评审内容
    val body: String? = null,
    // 评审状态
    val state: ReviewState? = null,
    // 评审链接 (非空)
    val link: String,
    // 评审作者
    val author: User? = null,
    // 是否已关闭
    val closed: Boolean? = null,
    // 评审标题
    val title: String,
    // 评审人列表
    val reviewers: List<User>? = null,
    // 源提交
    val sourceCommit: String? = null,
    // 源分支
    val sourceBranch: String? = null,
    // 源项目ID
    val sourceProjectId: String? = null,
    // 目标提交
    val targetCommit: String? = null,
    // 目标分支
    val targetBranch: String? = null,
    // 目标项目ID
    val targetProjectId: String? = null
)
