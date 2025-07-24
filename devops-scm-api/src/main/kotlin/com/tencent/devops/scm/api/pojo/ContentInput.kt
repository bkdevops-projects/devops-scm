package com.tencent.devops.scm.api.pojo

data class ContentInput(
    // 提交的分支
    val ref: String,
    // 提交message
    val message: String,
    // 文件内容
    val content: String
)
