package com.tencent.devops.scm.api.pojo.webhook.git;

import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_ISSUE_DESCRIPTION;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_ISSUE_ID;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_ISSUE_IID;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_ISSUE_MILESTONE_ID;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_ISSUE_OWNER;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_ISSUE_STATE;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_ISSUE_TITLE;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_ISSUE_URL;

import com.tencent.devops.scm.api.pojo.Issue;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.collections4.CollectionUtils;

/**
 * issue评论事件
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class IssueCommentHook extends AbstractCommentHook {
    public static final String CLASS_TYPE = "issue_comment";
    private Issue issue;

    @Override
    public Map<String, Object> outputs() {
        Map<String, Object> outputs = super.outputs();
        if (getExtras() != null) {
            outputs.putAll(getExtras());
        }
        if (issue != null) {
            outputs.put(BK_REPO_GIT_WEBHOOK_ISSUE_TITLE, issue.getTitle());
            outputs.put(BK_REPO_GIT_WEBHOOK_ISSUE_ID, issue.getId());
            outputs.put(BK_REPO_GIT_WEBHOOK_ISSUE_IID, issue.getNumber());
            outputs.put(BK_REPO_GIT_WEBHOOK_ISSUE_DESCRIPTION, issue.getBody());
            outputs.put(BK_REPO_GIT_WEBHOOK_ISSUE_STATE, issue.getState());
            outputs.put(BK_REPO_GIT_WEBHOOK_ISSUE_OWNER, getSender().getName());
            outputs.put(BK_REPO_GIT_WEBHOOK_ISSUE_URL, issue.getLink());
            outputs.put(BK_REPO_GIT_WEBHOOK_ISSUE_MILESTONE_ID, issue.getMilestoneId());
        }
        return outputs;
    }
}
