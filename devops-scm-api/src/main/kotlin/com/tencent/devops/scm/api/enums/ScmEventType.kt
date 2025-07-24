package com.tencent.devops.scm.api.enums

/**
 * scm通用事件类型
 */
enum class ScmEventType(val value: String) {
    ISSUE("issue"),
    ISSUE_COMMENT("issue_comment"),
    PULL_REQUEST("pull_request"),
    PULL_REQUEST_COMMENT("pull_request_comment"),
    PUSH("push"),
    TAG("tag"),
    PULL_REQUEST_REVIEW("pull_request_review"),
    POST_COMMIT("post_commit");
}
