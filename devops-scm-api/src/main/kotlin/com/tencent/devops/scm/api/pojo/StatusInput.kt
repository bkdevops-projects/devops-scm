package com.tencent.devops.scm.api.pojo

import com.tencent.devops.scm.api.enums.StatusState

/**
 * 状态输入信息
 */
data class StatusInput(
    // 状态值
    var state: StatusState,
    // 标签
    var label: String? = null,
    // 描述
    var desc: String,
    // 目标
    var targetUrl: String,
    // 这两个字段是工蜂特有
    // 是否阻塞
    var block: Boolean? = null,
    // 目标分支列表
    var targetBranches: List<String>? = null
)
