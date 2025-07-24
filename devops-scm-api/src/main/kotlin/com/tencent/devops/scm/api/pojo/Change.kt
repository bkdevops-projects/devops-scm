package com.tencent.devops.scm.api.pojo

data class Change(
    val path: String,
    val added: Boolean = false,
    val renamed: Boolean = false,
    val deleted: Boolean = false,
    val sha: String,
    val blobId: String,
    val oldPath: String? = null
)
