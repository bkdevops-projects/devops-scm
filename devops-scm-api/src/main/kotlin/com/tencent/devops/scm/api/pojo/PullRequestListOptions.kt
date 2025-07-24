package com.tencent.devops.scm.api.pojo

import com.tencent.devops.scm.api.enums.PullRequestState

data class PullRequestListOptions(
    // PR/MR 状态
    val state: PullRequestState? = null,
    // 源分支
    val sourceBranch: String? = null,
    // 目标分支
    val targetBranch: String? = null,
    // 页码
    val page: Int? = null,
    // 每页数量
    val pageSize: Int? = null
)
