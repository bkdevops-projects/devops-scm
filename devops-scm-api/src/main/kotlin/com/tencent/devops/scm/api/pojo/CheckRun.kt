package com.tencent.devops.scm.api.pojo

import com.tencent.devops.scm.api.enums.CheckRunConclusion
import com.tencent.devops.scm.api.enums.CheckRunStatus

data class CheckRun(
    val id: Long,
    // 状态值
    val status: CheckRunStatus,
    // 检查名称
    val name: String,
    // 状态描述
    val summary: String?,
    // 目标链接
    val detailsUrl: String?,
    // 详情
    val detail: String?,
    // 结论
    val conclusion: CheckRunConclusion?
)
