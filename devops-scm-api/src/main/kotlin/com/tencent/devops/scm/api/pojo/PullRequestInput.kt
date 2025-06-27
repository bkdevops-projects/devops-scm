package com.tencent.devops.scm.api.pojo

data class PullRequestInput(
    // PR/MR 标题
    val title: String,
    // PR/MR 正文内容
    val body: String? = null,
    // 源分支
    val sourceBranch: String,
    // 目标分支
    val targetBranch: String,
    // 源仓库
    val sourceRepo: Any? = null,
    // 目标仓库
    val targetRepo: Any? = null
)
