package com.tencent.devops.scm.api.pojo

data class CommitListOptions(
    val ref: String,
    val page: Int,
    val pageSize: Int,
    val path: String? = null
)
