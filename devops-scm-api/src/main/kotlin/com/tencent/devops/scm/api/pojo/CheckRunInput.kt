package com.tencent.devops.scm.api.pojo

import com.tencent.devops.scm.api.enums.CheckRunConclusion
import com.tencent.devops.scm.api.enums.CheckRunStatus
import java.time.LocalDateTime

/**
 * 状态输入信息
 */
data class CheckRunInput(
    val id: Long? = null,
    // 引用名称
    val name: String,
    // 引用的SHA值
    val ref: String? = null,
    // 关联的PR ID
    val pullRequestId: Long? = null,
    // 状态
    val status: CheckRunStatus,
    // 检查链接
    val detailsUrl: String? = null,
    // 外部ID
    val externalId: String? = null,
    val startedAt: LocalDateTime? = null,
    val completedAt: LocalDateTime? = null,
    val conclusion: CheckRunConclusion? = null,
    val output: CheckRunOutput? = null,
    // 这两个字段是工蜂特有
    // 是否阻塞
    var block: Boolean? = null,
    // 目标分支列表
    var targetBranches: List<String>? = null
)
