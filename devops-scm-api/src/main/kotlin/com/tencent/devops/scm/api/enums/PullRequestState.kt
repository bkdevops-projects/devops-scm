package com.tencent.devops.scm.api.enums

enum class PullRequestState(val value: String) {
    ALL("all"),
    OPENED("opened"),
    REOPENED("reopened"),
    MERGED("merged"),
    CLOSED("closed");
}
