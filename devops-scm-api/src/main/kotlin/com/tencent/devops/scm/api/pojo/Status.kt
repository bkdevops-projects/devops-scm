package com.tencent.devops.scm.api.pojo

import com.tencent.devops.scm.api.enums.StatusState

data class Status(
    // 状态值
    val state: StatusState,
    // 上下文标识
    val context: String,
    // 状态描述
    val desc: String,
    // 目标链接
    val targetUrl: String
)
