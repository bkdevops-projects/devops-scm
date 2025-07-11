package com.tencent.devops.scm.api.pojo

data class Content(
    val path: String,
    val content: String,
    val sha: String,
    val blobId: String
)
