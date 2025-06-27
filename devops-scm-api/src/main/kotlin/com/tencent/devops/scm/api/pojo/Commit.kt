package com.tencent.devops.scm.api.pojo

import java.time.LocalDateTime

data class Commit(
    val sha: String,
    val message: String,
    val author: Signature,
    val committer: Signature,
    val commitTime: LocalDateTime,
    val link: String,
    val added: List<String>,
    val modified: List<String>,
    val removed: List<String>
)
