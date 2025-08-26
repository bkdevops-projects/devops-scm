package com.tencent.devops.scm.api.enums

enum class CheckRunConclusion {
    SUCCESS,
    FAILURE,
    CANCELLED,
    TIMED_OUT,
    ACTION_REQUIRED,
    SKIPPED,
    UNKNOWN;
}