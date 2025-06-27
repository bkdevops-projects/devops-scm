package com.tencent.devops.scm.api.pojo

data class Reference(
    // 引用名称
    val name: String,
    // 引用的SHA值
    val sha: String,
    // 链接URL
    val linkUrl: String,
    // 路径
    val path: String? = null
)
