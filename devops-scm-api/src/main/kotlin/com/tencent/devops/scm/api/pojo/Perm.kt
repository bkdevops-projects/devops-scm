package com.tencent.devops.scm.api.pojo

data class Perm(
    // 是否有拉取权限
    val pull: Boolean = false,
    // 是否有推送权限
    val push: Boolean = false,
    // 是否有管理员权限
    val admin: Boolean = false
)
