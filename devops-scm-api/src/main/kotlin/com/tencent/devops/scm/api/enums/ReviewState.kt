package com.tencent.devops.scm.api.enums

enum class ReviewState(val value: String) {
    UNKNOWN("unknown"),
    APPROVING("approving"),
    APPROVED("approved"),
    CHANGE_REQUIRED("change_required"),
    CHANGE_DENIED("change_denied"),
    EMPTY("empty"),
    CLOSED("closed");
}
