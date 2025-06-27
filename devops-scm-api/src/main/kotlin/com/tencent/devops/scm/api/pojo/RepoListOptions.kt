package com.tencent.devops.scm.api.pojo

data class RepoListOptions(
    // 页码
    val page: Int? = null,
    // 每页数量
    val pageSize: Int? = null,
    // 仓库名称
    val repoName: String? = null,
    // 用户名称
    val user: String? = null
)
