package com.tencent.devops.scm.api.pojo

import java.time.LocalDateTime

data class Issue(
    var id: Long,
    var number: Int,
    var title: String,
    var body: String? = null,
    var link: String,
    var labels: List<String>? = null,
    var closed: Boolean = false,
    var locked: Boolean = false,
    var author: User,
    var created: LocalDateTime,
    var updated: LocalDateTime? = null,
    var milestoneId: String? = null,
    var state: String? = null
)
