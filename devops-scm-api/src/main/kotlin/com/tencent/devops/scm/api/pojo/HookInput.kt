package com.tencent.devops.scm.api.pojo

data class HookInput(
    var name: String,
    // webhook接收的url地址
    var url: String,
    var secret: String? = null,
    var events: HookEvents? = null,
    var skipVerify: Boolean? = null,
    // 在HookEvents中不包含的事件类型
    var nativeEvents: List<String>? = null,
    // 仓库下级目录
    var path: String? = null
)
