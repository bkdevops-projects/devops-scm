package com.tencent.devops.scm.api.pojo.webhook.git;

import com.tencent.devops.scm.api.pojo.PullRequest;
import java.util.Collections;
import java.util.Map;

import com.tencent.devops.scm.api.pojo.Review;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * pr或mr评论事件
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
public class PullRequestCommentHook extends AbstractCommentHook {
    public static final String CLASS_TYPE = "pull_request_comment";
    private PullRequest pullRequest;
    private Review review; // 工蜂commit review 专用

    @Override
    public Map<String, Object> outputs() {
        Map<String, Object> outputs = super.outputs();
        if (getExtras() != null) {
            outputs.putAll(getExtras());
        }
        // commit review
        if (review != null) {
            outputs.put("BK_REPO_GIT_WEBHOOK_REVIEW_STATE", review.getState().value);
            outputs.put("BK_REPO_GIT_WEBHOOK_REVIEW_ID", review.getId());
            outputs.put("BK_REPO_GIT_WEBHOOK_REVIEW_IID", review.getIid());
            outputs.put("BK_REPO_GIT_WEBHOOK_REVIEW_SOURCE_BRANCH", review.getSourceBranch());
            outputs.put("BK_REPO_GIT_WEBHOOK_REVIEW_SOURCE_PROJECT_ID", review.getSourceProjectId());
            outputs.put("BK_REPO_GIT_WEBHOOK_REVIEW_SOURCE_COMMIT", review.getSourceCommit());
            outputs.put("BK_REPO_GIT_WEBHOOK_REVIEW_TARGET_COMMIT", review.getTargetCommit());
            outputs.put("BK_REPO_GIT_WEBHOOK_REVIEW_TARGET_BRANCH", review.getTargetBranch());
            outputs.put("BK_REPO_GIT_WEBHOOK_REVIEW_TARGET_PROJECT_ID", review.getTargetProjectId());
        }
        return outputs;
    }
}
