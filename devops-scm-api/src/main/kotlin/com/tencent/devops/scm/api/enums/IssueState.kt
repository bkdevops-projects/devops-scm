package com.tencent.devops.scm.api.enums

enum class IssueState(val value: String) {
    ALL("all"),
    OPENED("opened"),
    REOPENED("reopened"),
    CLOSED("closed");
}
