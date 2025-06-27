package com.tencent.devops.scm.api.pojo

import com.tencent.devops.scm.api.enums.ContentKind

data class Tree(
    // 文件路径
    val path: String,
    // SHA值
    val sha: String,
    // Blob ID
    val blobId: String,
    // 内容类型
    val kind: ContentKind
)
