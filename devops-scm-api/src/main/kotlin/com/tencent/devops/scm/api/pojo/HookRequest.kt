package com.tencent.devops.scm.api.pojo

data class HookRequest(
    var headers: Map<String, String>? = null,
    var body: String,
    var queryParams: Map<String, String>? = null
)
