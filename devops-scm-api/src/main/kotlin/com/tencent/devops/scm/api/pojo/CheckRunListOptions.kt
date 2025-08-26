package com.tencent.devops.scm.api.pojo

data class CheckRunListOptions(
    val page: Int? = null,
    val pageSize: Int? = null,
    val ref: String,
    val pullRequestId: Long? = null
)