package com.tencent.devops.scm.api.pojo

/**
 * 代表代码提交者
 */
data class Signature(
    // 提交者名称
    val name: String,
    // 提交者邮箱
    val email: String? = null,
    // 提交者头像
    val avatar: String? = null
)
