package com.tencent.devops.scm.provider.git.gitee.enums;

enum class GiteeEventType(val value: String) {
    PUSH("push"),
    TAG_PUSH("tag_push"),
    MERGE_REQUEST("merge_request"),
    ISSUES("issues"),
    NOTE("note"),
    REVIEW("review");
}
