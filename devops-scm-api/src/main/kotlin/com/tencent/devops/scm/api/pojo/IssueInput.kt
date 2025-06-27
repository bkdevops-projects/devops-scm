package com.tencent.devops.scm.api.pojo

data class IssueInput(
    var title: String,
    var body: String? = null
)
