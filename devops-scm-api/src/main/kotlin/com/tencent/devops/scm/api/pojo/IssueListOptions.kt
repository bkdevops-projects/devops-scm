package com.tencent.devops.scm.api.pojo

import com.tencent.devops.scm.api.enums.IssueState

data class IssueListOptions(
    val page: Int? = null,
    val pageSize: Int? = null,
    val state: IssueState? = null
)
