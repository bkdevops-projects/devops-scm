package com.tencent.devops.scm.api.constant;

/**
 * webhook i18 code
 */
public interface WebhookI18Code {
    // git push事件描述
    String GIT_PUSH_EVENT_DESC = "bkGitPushEventDesc";
    // git push创建分支事件描述
    String GIT_PUSH_CREATE_EVENT_DESC = "bkGitPushCreateEventDesc";
    // git push删除事件描述
    String GIT_PUSH_DELETE_EVENT_DESC = "bkGitPushDeleteEventDesc";


    // git issue创建事件描述
    String GIT_ISSUE_OPENED_EVENT_DESC = "bkGitIssueOpenedEventDesc";
    // git issue 更新事件描述
    String GIT_ISSUE_UPDATED_EVENT_DESC = "bkGitIssueUpdatedEventDesc";
    // git issue 关闭事件描述
    String GIT_ISSUE_CLOSED_EVENT_DESC = "bkGitIssueClosedEventDesc";
    // git issue reopen事件描述
    String GIT_ISSUE_REOPENED_EVENT_DESC = "bkGitIssueReopenedEventDesc";

    // git pr 创建事件
    String GIT_PR_CREATED_EVENT_DESC = "bkGitPrCreatedEventDesc";
    // git pr 更新事件
    String GIT_PR_UPDATED_EVENT_DESC = "bkGitPrUpdatedEventDesc";
    // git pr关闭事件
    String GIT_PR_CLOSED_EVENT_DESC = "bkGitPrClosedEventDesc";
    // git pr reopen事件
    String GIT_PR_REOPENED_EVENT_DESC = "bkGitPrReopenedEventDesc";
    // git pr 源分支push事件
    String GIT_PR_PUSH_UPDATED_EVENT_DESC = "bkGitPrPushUpdatedEventDesc";
    // git pr merged事件
    String GIT_PR_MERGED_EVENT_DESC = "bkGitPrMergedEventDesc";

    // git评论事件
    String GIT_NOTE_EVENT_DESC = "bkGitNoteEventDesc";

    String GIT_REVIEW_APPROVED_EVENT_DESC = "bkGitReviewApprovedEventDesc";
    String GIT_REVIEW_APPROVING_EVENT_DESC = "bkGitReviewApprovingEventDesc";
    String GIT_REVIEW_CLOSED_EVENT_DESC = "bkGitReviewClosedEventDesc";
    String GIT_REVIEW_CHANGE_DENIED_EVENT_DESC = "bkGitReviewChangeDeniedEventDesc";
    String GIT_REVIEW_CHANGE_REQUIRED_EVENT_DESC = "bkGitReviewChangeRequiredEventDesc";
    String GIT_REVIEW_CREATED_EVENT_DESC = "bkGitReviewCreatedEventDesc";

    // git tag push事件
    String GIT_TAG_PUSH_EVENT_DESC = "bkGitTagPushEventDesc";
    // git tag删除事件
    String GIT_TAG_DELETE_EVENT_DESC = "bkGitTagDeleteEventDesc";
}
