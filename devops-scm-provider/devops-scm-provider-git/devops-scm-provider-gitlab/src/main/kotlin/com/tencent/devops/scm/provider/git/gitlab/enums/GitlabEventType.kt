package com.tencent.devops.scm.provider.git.gitlab.enums

enum class GitlabEventType(val value: String) {
    PUSH("push"),
    TAG_PUSH("tag_push"),
    MERGE_REQUEST("merge_request"),
    ISSUES("issues"),
    NOTE("note")
}
