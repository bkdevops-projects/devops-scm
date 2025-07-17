package com.tencent.devops.scm.api.pojo

import java.time.LocalDateTime

/**
 * 代表源代码平台用户信息
 */
data class User(
    // 用户ID
    var id: Long,
    // 登录用户名
    var username: String,
    // 用户名称
    var name: String,
    // 邮箱
    var email: String? = null,
    // 头像URL
    var avatar: String? = null,
    // 创建时间
    var created: LocalDateTime? = null,
    // 更新时间
    var updated: LocalDateTime? = null
)
