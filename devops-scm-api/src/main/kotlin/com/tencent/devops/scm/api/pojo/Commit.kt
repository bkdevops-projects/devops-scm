package com.tencent.devops.scm.api.pojo

import java.time.LocalDateTime

data class Commit(
    val sha: String,
    var message: String,
    var author: Signature? = null,
    val committer: Signature? = null,
    val commitTime: LocalDateTime? = null,
    val link: String = "",
    val added: List<String> = listOf(),
    val modified: List<String> = listOf(),
    val removed: List<String> = listOf()
)
