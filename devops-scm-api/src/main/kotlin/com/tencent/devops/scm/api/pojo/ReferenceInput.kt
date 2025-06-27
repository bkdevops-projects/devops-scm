package com.tencent.devops.scm.api.pojo

/**
 * Git引用输入参数
 */
data class ReferenceInput(
    // 引用名称
    val name: String,
    // 引用的SHA值
    val sha: String? = null
)
