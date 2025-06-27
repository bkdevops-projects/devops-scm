package com.tencent.devops.scm.provider.git.tgit.enums

enum class TGitEventType(val value: String) {
    PUSH("push"),
    TAG_PUSH("tag_push"),
    MERGE_REQUEST("merge_request"),
    ISSUES("issues"),
    NOTE("note"),
    REVIEW("review");
}
