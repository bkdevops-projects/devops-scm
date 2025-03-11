package com.tencent.devops.scm.provider.git.tgit;

import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_HOOK_MR_COMMITTER;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_HOOK_MR_ID;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_ASSIGNEE;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_AUTHOR;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_BASE_COMMIT;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_CREATE_TIME;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_CREATE_TIMESTAMP;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_DESCRIPTION;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_ID;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_LABELS;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_MILESTONE;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_MILESTONE_DUE_DATE;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_MILESTONE_ID;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_NUMBER;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_REVIEWERS;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_SOURCE_BRANCH;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_SOURCE_COMMIT;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_TARGET_BRANCH;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_TARGET_COMMIT;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_TITLE;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_UPDATE_TIME;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_UPDATE_TIMESTAMP;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_REVIEW_ID;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_REVIEW_IID;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_REVIEW_OWNER;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_REVIEW_STATE;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_BASE_REF;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_HEAD_REF;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_MR_DESC;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_MR_ID;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_MR_IID;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_MR_PROPOSER;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_MR_TITLE;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_WEBHOOK_SOURCE_BRANCH;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_WEBHOOK_SOURCE_PROJECT_ID;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_WEBHOOK_TARGET_BRANCH;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_WEBHOOK_TARGET_PROJECT_ID;
import static com.tencent.devops.scm.api.constant.WebhookOutputConstants.PR_DESC_MAX_LENGTH;

import com.tencent.devops.scm.api.constant.DateFormatConstants;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitAssignee;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitAuthor;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitMergeRequest;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitMilestone;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitReview;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitReviewer;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * TGitObject 转换成 Map
 * 根据API查询结果，组装Webhook参数
 */
public class TGitObjectToMapConverter {

    /**
     * 获取merge request 相关触发参数
     */
    public static Map<String, Object> convertPullRequest(TGitMergeRequest mergeRequest) {
        Map<String, Object> params = new HashMap<>();
        if (mergeRequest != null) {
            // 基本MR信息
            putMultipleKeys(
                    params,
                    StringUtils.defaultString(mergeRequest.getSourceBranch(), ""),
                    SetUtils.hashSet(
                            PIPELINE_WEBHOOK_SOURCE_BRANCH,
                            BK_REPO_GIT_WEBHOOK_MR_SOURCE_BRANCH,
                            PIPELINE_GIT_BASE_REF
                    )
            );
            putMultipleKeys(
                    params,
                    StringUtils.defaultString(mergeRequest.getTargetBranch(), ""),
                    SetUtils.hashSet(
                            PIPELINE_WEBHOOK_TARGET_BRANCH,
                            BK_REPO_GIT_WEBHOOK_MR_TARGET_BRANCH,
                            PIPELINE_GIT_HEAD_REF
                    )
            );
            putMultipleKeys(
                    params,
                    mergeRequest.getId() != null ? mergeRequest.getId() : "",
                    SetUtils.hashSet(
                            PIPELINE_GIT_MR_ID,
                            BK_HOOK_MR_ID,
                            BK_REPO_GIT_WEBHOOK_MR_ID
                    )
            );
            putMultipleKeys(
                    params,
                    mergeRequest.getIid() != null ? mergeRequest.getIid() : "",
                    SetUtils.hashSet(
                            BK_REPO_GIT_WEBHOOK_MR_NUMBER,
                            PIPELINE_GIT_MR_IID
                    )
            );
            putMultipleKeys(
                    params,
                    Optional.ofNullable(mergeRequest.getAuthor()).map(TGitAuthor::getUsername).orElse(""),
                    SetUtils.hashSet(
                            BK_HOOK_MR_COMMITTER,
                            BK_REPO_GIT_WEBHOOK_MR_AUTHOR,
                            PIPELINE_GIT_MR_PROPOSER
                    )
            );
            params.put(
                    PIPELINE_WEBHOOK_SOURCE_PROJECT_ID,
                    mergeRequest.getSourceProjectId() != null ? mergeRequest.getSourceProjectId() : ""
            );
            params.put(
                    PIPELINE_WEBHOOK_TARGET_PROJECT_ID,
                    mergeRequest.getTargetProjectId() != null ? mergeRequest.getTargetProjectId() : ""
            );
            String mrDesc = StringUtils.substring(
                    StringUtils.defaultString(mergeRequest.getDescription(), ""),
                    0,
                    PR_DESC_MAX_LENGTH
            );
            putMultipleKeys(
                    params,
                    mrDesc,
                    SetUtils.hashSet(
                            BK_REPO_GIT_WEBHOOK_MR_DESCRIPTION,
                            PIPELINE_GIT_MR_DESC
                    )
            );
            putMultipleKeys(
                    params,
                    StringUtils.defaultString(mergeRequest.getTitle(), ""),
                    SetUtils.hashSet(
                            BK_REPO_GIT_WEBHOOK_MR_TITLE,
                            PIPELINE_GIT_MR_TITLE
                    )
            );
            // 时间
            params.put(
                    BK_REPO_GIT_WEBHOOK_MR_CREATE_TIME,
                    new SimpleDateFormat(DateFormatConstants.ISO_8601)
                            .format(mergeRequest.getCreatedAt())
            );
            params.put(BK_REPO_GIT_WEBHOOK_MR_CREATE_TIMESTAMP, mergeRequest.getCreatedAt().getTime());

            params.put(
                    BK_REPO_GIT_WEBHOOK_MR_UPDATE_TIME,
                    new SimpleDateFormat(DateFormatConstants.ISO_8601)
                            .format(mergeRequest.getUpdatedAt().getTime())
            );
            params.put(BK_REPO_GIT_WEBHOOK_MR_UPDATE_TIMESTAMP, mergeRequest.getUpdatedAt().getTime());
            params.put(
                    BK_REPO_GIT_WEBHOOK_MR_ASSIGNEE,
                    Optional.ofNullable(mergeRequest.getAssignee())
                            .map(TGitAssignee::getName)
                            .orElse("")
            );
            // 里程碑信息
            TGitMilestone milestone = mergeRequest.getMilestone();
            if (milestone != null) {
                params.put(
                        BK_REPO_GIT_WEBHOOK_MR_MILESTONE,
                        Optional.of(milestone)
                                .map(TGitMilestone::getTitle)
                                .orElse("")
                );
                params.put(
                        BK_REPO_GIT_WEBHOOK_MR_MILESTONE_ID,
                        Optional.of(milestone)
                                .map(TGitMilestone::getId)
                                .map(num -> Integer.toString(num))
                                .orElse("")
                );

                params.put(
                        BK_REPO_GIT_WEBHOOK_MR_MILESTONE_DUE_DATE,
                        new SimpleDateFormat("yyyy-MM-dd")
                                .format(milestone.getDueDate())
                );
            }
            params.put(
                    BK_REPO_GIT_WEBHOOK_MR_LABELS,
                    Optional.ofNullable(mergeRequest.getLabels())
                            .map(labels -> String.join(",", labels))
                            .orElse("")
            );
            params.put(
                    BK_REPO_GIT_WEBHOOK_MR_BASE_COMMIT,
                    StringUtils.defaultString(mergeRequest.getBaseCommit(), "")
            );
            params.put(
                    BK_REPO_GIT_WEBHOOK_MR_TARGET_COMMIT,
                    StringUtils.defaultString(mergeRequest.getTargetCommit(), "")
            );
            params.put(
                    BK_REPO_GIT_WEBHOOK_MR_SOURCE_COMMIT,
                    StringUtils.defaultString(mergeRequest.getSourceCommit(), "")
            );
        }
        return params;
    }

    /**
     * 获取 review 相关触发参数
     */
    public static Map<String, Object> convertReview(TGitReview review) {
        Map<String, Object> params = new HashMap<>();
        // Review信息
        if (review != null) {
            params.put(
                    BK_REPO_GIT_WEBHOOK_MR_REVIEWERS,
                    Optional.ofNullable(review.getReviewers())
                            .map(
                                    reviewers -> reviewers.stream()
                                            .map(TGitReviewer::getUsername)
                                            .collect(Collectors.joining(","))
                            )
                            .orElse("")
            );
            params.put(
                    BK_REPO_GIT_WEBHOOK_REVIEW_STATE,
                    StringUtils.defaultString(review.getState().toValue(), "")
            );
            params.put(
                    BK_REPO_GIT_WEBHOOK_REVIEW_OWNER,
                    Optional.ofNullable(review.getAuthor()).map(TGitAuthor::getUsername).orElse("")
            );
            params.put(
                    BK_REPO_GIT_WEBHOOK_REVIEW_ID,
                    review.getId()
            );
            params.put(
                    BK_REPO_GIT_WEBHOOK_REVIEW_IID,
                    review.getIid()
            );
        }

        return params;
    }

    /**
     * 填充多个key-value
     *
     * @param map 目标map
     * @param value 填充值
     * @param keys key列表
     */
    public static void putMultipleKeys(Map<String, Object> map, Object value, Set<String> keys) {
        for (String key : keys) {
            map.put(key, value);
        }
    }
}
