package com.tencent.devops.scm.provider.svn.tsvn.enums

enum class TSvnEventType(val value: String) {
    POST_COMMIT("post_commit"),
    PRE_COMMIT("pre_commit"),
    POST_LOCK("post_lock"),
    PRE_LOCK("pre_lock");
}