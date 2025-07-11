package com.tencent.devops.scm.api.pojo

import java.time.LocalDateTime

data class Milestone(
    var id: Int,
    var title: String,
    var state: String? = null,
    var iid: Int? = null,
    var dueDate: LocalDateTime? = null,
    var description: String? = null
)
