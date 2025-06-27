package com.tencent.devops.scm.api.pojo

/**
 * 标签列表查询选项
 */
data class TagListOptions(
    // 搜索关键字
    val search: String? = null,
    // 页码
    val page: Int? = null,
    // 每页大小
    val pageSize: Int? = null,
    // 排序字段
    val orderBy: String? = null,
    // 排序方式(asc/desc)
    val sort: String? = null
)
