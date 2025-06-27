package com.tencent.devops.scm.api.pojo

data class BranchListOptions(
    val search: String? = null,
    val page: Int = 1,
    val pageSize: Int = 20
)
