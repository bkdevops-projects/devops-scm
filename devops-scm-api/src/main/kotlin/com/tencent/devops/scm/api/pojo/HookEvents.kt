package com.tencent.devops.scm.api.pojo

import com.fasterxml.jackson.annotation.JsonIgnore
import com.tencent.devops.scm.api.enums.ScmEventType

data class HookEvents(
    var issue: Boolean? = null,
    var issueComment: Boolean? = null,
    var pullRequest: Boolean? = null,
    var pullRequestComment: Boolean? = null,
    var push: Boolean? = null,
    var tag: Boolean? = null,
    var pullRequestReview: Boolean? = null,
    var svnPreCommitEvents: Boolean? = null,
    var svnPostCommitEvents: Boolean? = null,
    var svnPreLockEvents: Boolean? = null,
    var svnPostLockEvents: Boolean? = null
) {
    constructor(enabledEvents: List<String>) : this() {
        if (enabledEvents.contains(ScmEventType.ISSUE.value)) issue = true
        if (enabledEvents.contains(ScmEventType.ISSUE_COMMENT.value)) issueComment = true
        if (enabledEvents.contains(ScmEventType.PULL_REQUEST.value)) pullRequest = true
        if (enabledEvents.contains(ScmEventType.PULL_REQUEST_COMMENT.value)) pullRequestComment = true
        if (enabledEvents.contains(ScmEventType.PUSH.value)) push = true
        if (enabledEvents.contains(ScmEventType.TAG.value)) tag = true
        if (enabledEvents.contains(ScmEventType.PULL_REQUEST_REVIEW.value)) pullRequestReview = true
        if (enabledEvents.contains(ScmEventType.POST_COMMIT.value)) svnPostCommitEvents = true
    }

    constructor(eventType: ScmEventType) : this(listOf(eventType.value))

    /**
     * 获取启用的事件类型
     */
    @JsonIgnore
    fun getEnabledEvents(): List<String> = mutableListOf<String>().apply {
        if (issue == true) add(ScmEventType.ISSUE.value)
        if (issueComment == true) add(ScmEventType.ISSUE_COMMENT.value)
        if (pullRequest == true) add(ScmEventType.PULL_REQUEST.value)
        if (pullRequestComment == true) add(ScmEventType.PULL_REQUEST_COMMENT.value)
        if (push == true) add(ScmEventType.PUSH.value)
        if (tag == true) add(ScmEventType.TAG.value)
        if (pullRequestReview == true) add(ScmEventType.PULL_REQUEST_REVIEW.value)
        if (svnPostCommitEvents == true) add(ScmEventType.POST_COMMIT.value)
    }
}
