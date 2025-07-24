package com.tencent.devops.scm.api.pojo

data class Hook(
    val id: Long,
    val name: String,
    val url: String,
    val events: HookEvents? = null,
    // 在HookEvents中不包含的事件类型
    val nativeEvents: List<String>? = null,
    val active: Boolean = true,
    val skipVerify: Boolean = false,
    // 监听仓库的相对路径
    val path: String? = null
)
