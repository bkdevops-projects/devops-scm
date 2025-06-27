package com.tencent.devops.scm.api.pojo

import java.time.LocalDateTime

data class Comment(
    val id: Long,
    val body: String,
    val link: String,
    val author: User,
    val created: LocalDateTime,
    val updated: LocalDateTime,
    val type: String
)
