package com.tencent.devops.scm.api.pojo;

import com.tencent.devops.scm.api.enums.ScmEventType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HookEvents {
    private Boolean issue;
    private Boolean issueComment;
    private Boolean pullRequest;
    private Boolean pullRequestComment;
    private Boolean push;
    private Boolean tag;
    private Boolean pullRequestReview;
    private Boolean svnPreCommitEvents;
    private Boolean svnPostCommitEvents;
    private Boolean svnPreLockEvents;
    private Boolean svnPostLockEvents;

    public HookEvents(List<String> enabledEvents) {
        if (enabledEvents.contains(ScmEventType.ISSUE.value)) {
            this.issue = true;
        }
        if (enabledEvents.contains(ScmEventType.ISSUE_COMMENT.value)) {
            this.issueComment = true;
        }
        if (enabledEvents.contains(ScmEventType.PULL_REQUEST.value)) {
            this.pullRequest = true;
        }
        if (enabledEvents.contains(ScmEventType.PULL_REQUEST_COMMENT.value)) {
            this.pullRequestComment = true;
        }
        if (enabledEvents.contains(ScmEventType.PUSH.value)) {
            this.push = true;
        }
        if (enabledEvents.contains(ScmEventType.TAG.value)) {
            this.tag = true;
        }
        if (enabledEvents.contains(ScmEventType.PULL_REQUEST_REVIEW.value)) {
            this.pullRequestReview = true;
        }
        if (enabledEvents.contains(ScmEventType.POST_COMMIT.value)) {
            this.svnPostCommitEvents = true;
        }
    }

    public HookEvents(ScmEventType eventType) {
        this(Collections.singletonList(eventType.value));
    }

    /**
     * 获取启用的事件类型
     */
    public List<String> getEnabledEvents() {
        List<String> events = new ArrayList<>(8);
        if (issue != null && issue) {
            events.add(ScmEventType.ISSUE.value);
        }
        if (issueComment != null && issueComment) {
            events.add(ScmEventType.ISSUE_COMMENT.value);
        }
        if (pullRequest != null && pullRequest) {
            events.add(ScmEventType.PULL_REQUEST.value);
        }
        if (pullRequestComment != null && pullRequestComment) {
            events.add(ScmEventType.PULL_REQUEST_COMMENT.value);
        }
        if (push != null && push) {
            events.add(ScmEventType.PUSH.value);
        }
        if (tag != null && tag) {
            events.add(ScmEventType.TAG.value);
        }
        if (pullRequestReview != null && pullRequestReview) {
            events.add(ScmEventType.PULL_REQUEST_REVIEW.value);
        }
        if (svnPostCommitEvents != null && svnPostCommitEvents) {
            events.add(ScmEventType.POST_COMMIT.value);
        }
        return events;
    }
}
