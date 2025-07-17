package com.tencent.devops.scm.api.enums

enum class StatusState(val value: String) {
    UNKNOWN("unknown"),
    PENDING("pending"),
    RUNNING("running"),
    SUCCESS("success"),
    FAILURE("failure"),
    CANCELED("canceled"),
    ERROR("error")
    ;
}
