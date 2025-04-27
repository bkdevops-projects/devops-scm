package com.tencent.devops.scm.provider.git.gitee;

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
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_SOURCE_BRANCH;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_SOURCE_COMMIT;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_TARGET_BRANCH;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_TARGET_COMMIT;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_TITLE;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_UPDATE_TIME;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_UPDATE_TIMESTAMP;
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
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeAssigne;
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeBaseLabel;
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeBaseMilestone;
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeBaseRepository;
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeBaseUser;
import com.tencent.devops.scm.sdk.gitee.pojo.GiteePullRequest;
import com.tencent.devops.scm.sdk.gitee.pojo.GiteePullRequestRef;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * GiteeObject 转换成 Map
 * 根据API查询结果，组装Webhook参数
 */
public class GiteeObjectToMapConverter {

    /**
     * 获取merge request 相关触发参数
     */
    public static Map<String, Object> convertPullRequest(GiteePullRequest pullRequest) {
        Map<String, Object> params = new HashMap<>();
        if (pullRequest == null) {
            return params;
        }
        // 基本MR信息
        putMultipleKeys(
                params,
                StringUtils.defaultString(pullRequest.getHead().getRef(), ""),
                SetUtils.hashSet(
                        PIPELINE_WEBHOOK_SOURCE_BRANCH,
                        BK_REPO_GIT_WEBHOOK_MR_SOURCE_BRANCH,
                        PIPELINE_GIT_BASE_REF
                )
        );
        putMultipleKeys(
                params,
                StringUtils.defaultString(pullRequest.getBase().getRef(), ""),
                SetUtils.hashSet(
                        PIPELINE_WEBHOOK_TARGET_BRANCH,
                        BK_REPO_GIT_WEBHOOK_MR_TARGET_BRANCH,
                        PIPELINE_GIT_HEAD_REF
                )
        );
        putMultipleKeys(
                params,
                pullRequest.getId() != null ? pullRequest.getId() : "",
                SetUtils.hashSet(
                        PIPELINE_GIT_MR_ID,
                        BK_HOOK_MR_ID,
                        BK_REPO_GIT_WEBHOOK_MR_ID
                )
        );
        putMultipleKeys(
                params,
                pullRequest.getNumber() != null ? pullRequest.getNumber() : "",
                SetUtils.hashSet(
                        BK_REPO_GIT_WEBHOOK_MR_NUMBER,
                        PIPELINE_GIT_MR_IID
                )
        );
        putMultipleKeys(
                params,
                Optional.ofNullable(pullRequest.getUser()).map(GiteeBaseUser::getName).orElse(""),
                SetUtils.hashSet(
                        BK_HOOK_MR_COMMITTER,
                        BK_REPO_GIT_WEBHOOK_MR_AUTHOR,
                        PIPELINE_GIT_MR_PROPOSER
                )
        );
        String sourceProjectId = Optional.ofNullable(pullRequest.getHead())
                .map(GiteePullRequestRef::getRepo)
                .map(GiteeBaseRepository::getId)
                .map(Object::toString)
                .orElse("");
        params.put(
                PIPELINE_WEBHOOK_SOURCE_PROJECT_ID,
                sourceProjectId
        );
        String targetProjectId = Optional.ofNullable(pullRequest.getBase())
                .map(GiteePullRequestRef::getRepo)
                .map(GiteeBaseRepository::getId)
                .map(Object::toString)
                .orElse("");
        params.put(
                PIPELINE_WEBHOOK_TARGET_PROJECT_ID,
                targetProjectId
        );
        String mrDesc = StringUtils.substring(
                StringUtils.defaultString(pullRequest.getBody(), ""),
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
                StringUtils.defaultString(pullRequest.getTitle(), ""),
                SetUtils.hashSet(
                        BK_REPO_GIT_WEBHOOK_MR_TITLE,
                        PIPELINE_GIT_MR_TITLE
                )
        );
        // 时间
        params.put(
                BK_REPO_GIT_WEBHOOK_MR_CREATE_TIME,
                new SimpleDateFormat(DateFormatConstants.ISO_8601)
                        .format(pullRequest.getCreatedAt())
        );
        params.put(BK_REPO_GIT_WEBHOOK_MR_CREATE_TIMESTAMP, pullRequest.getCreatedAt().getTime());

        params.put(
                BK_REPO_GIT_WEBHOOK_MR_UPDATE_TIME,
                new SimpleDateFormat(DateFormatConstants.ISO_8601)
                        .format(pullRequest.getUpdatedAt().getTime())
        );
        params.put(BK_REPO_GIT_WEBHOOK_MR_UPDATE_TIMESTAMP, pullRequest.getUpdatedAt().getTime());
        params.put(
                BK_REPO_GIT_WEBHOOK_MR_ASSIGNEE,
                CollectionUtils.emptyIfNull(pullRequest.getAssignees())
                        .stream()
                        .findFirst()
                        .map(GiteeAssigne::getName)
                        .orElse("")
        );
        // 里程碑信息
        GiteeBaseMilestone milestone = pullRequest.getMilestone();
        if (milestone != null) {
            params.put(
                    BK_REPO_GIT_WEBHOOK_MR_MILESTONE,
                    Optional.of(milestone)
                            .map(GiteeBaseMilestone::getTitle)
                            .orElse("")
            );
            params.put(
                    BK_REPO_GIT_WEBHOOK_MR_MILESTONE_ID,
                    Optional.of(milestone)
                            .map(GiteeBaseMilestone::getId)
                            .map(num -> Long.toString(num))
                            .orElse("")
            );

            params.put(
                    BK_REPO_GIT_WEBHOOK_MR_MILESTONE_DUE_DATE,
                    milestone.getDueOn()
            );
        }
        params.put(
                BK_REPO_GIT_WEBHOOK_MR_LABELS,
                StringUtils.join(
                        CollectionUtils.emptyIfNull(pullRequest.getLabels())
                                .stream()
                                .map(GiteeBaseLabel::getName)
                                .collect(Collectors.toList()),
                        ","
                )

        );
        putMultipleKeys(
                params,
                StringUtils.defaultString(pullRequest.getBase().getSha(), ""),
                SetUtils.hashSet(
                        BK_REPO_GIT_WEBHOOK_MR_BASE_COMMIT,
                        BK_REPO_GIT_WEBHOOK_MR_TARGET_COMMIT
                )
        );
        params.put(
                BK_REPO_GIT_WEBHOOK_MR_SOURCE_COMMIT,
                StringUtils.defaultString(pullRequest.getHead().getSha(), "")
        );
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
