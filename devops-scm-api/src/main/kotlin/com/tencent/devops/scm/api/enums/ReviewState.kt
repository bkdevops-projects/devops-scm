package com.tencent.devops.scm.api.enums

enum class ReviewState(val value: String) {
    UNKNOWN("unknown"),
    APPROVING("approving"),
    APPROVED("approved"),
    CHANGES_REQUESTED("changes_requested"),
    CHANGE_DENIED("change_denied"),
    EMPTY("empty"),
    CLOSED("closed");
}
