package com.tencent.devops.scm.api.pojo.webhook.git;

import static com.tencent.devops.scm.api.constant.WebhookI18Code.GIT_PR_CLOSED_EVENT_DESC;
import static com.tencent.devops.scm.api.constant.WebhookI18Code.GIT_PR_CREATED_EVENT_DESC;
import static com.tencent.devops.scm.api.constant.WebhookI18Code.GIT_PR_MERGED_EVENT_DESC;
import static com.tencent.devops.scm.api.constant.WebhookI18Code.GIT_PR_PUSH_UPDATED_EVENT_DESC;
import static com.tencent.devops.scm.api.constant.WebhookI18Code.GIT_PR_REOPENED_EVENT_DESC;
import static com.tencent.devops.scm.api.constant.WebhookI18Code.GIT_PR_UPDATED_EVENT_DESC;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_GIT_MR_NUMBER;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_HOOK_MR_ID;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_BRANCH;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_COMMIT_ID;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_DESCRIPTION;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_ID;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_LAST_COMMIT;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_LAST_COMMIT_MSG;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_MERGE_COMMIT_SHA;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_MERGE_TYPE;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_NUMBER;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_SOURCE_BRANCH;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_SOURCE_URL;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_TARGET_URL;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_MR_URL;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_ACTION;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_BASE_REF;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_BASE_REPO_URL;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_COMMIT_AUTHOR;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_COMMIT_MESSAGE;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_EVENT;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_EVENT_URL;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_HEAD_REPO_URL;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_MR_ACTION;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_MR_DESC;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_MR_URL;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_REPO;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_REPO_GROUP;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_REPO_ID;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_REPO_NAME;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_REPO_URL;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_SHA;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_REPO_NAME;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_START_WEBHOOK_USER_ID;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_WEBHOOK_BRANCH;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_WEBHOOK_COMMIT_MESSAGE;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_WEBHOOK_EVENT_TYPE;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_WEBHOOK_REVISION;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_WEBHOOK_SOURCE_REPO_NAME;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_WEBHOOK_SOURCE_URL;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_WEBHOOK_TARGET_REPO_NAME;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_WEBHOOK_TARGET_URL;
import static com.tencent.devops.scm.api.constant.WebhookOutputConstants.PR_DESC_MAX_LENGTH;

import com.tencent.devops.scm.api.enums.EventAction;
import com.tencent.devops.scm.api.pojo.Change;
import com.tencent.devops.scm.api.pojo.Commit;
import com.tencent.devops.scm.api.pojo.PullRequest;
import com.tencent.devops.scm.api.pojo.ScmI18Variable;
import com.tencent.devops.scm.api.pojo.User;
import com.tencent.devops.scm.api.pojo.repository.git.GitScmServerRepository;
import com.tencent.devops.scm.api.pojo.webhook.Webhook;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

/**
 * pr/mr 事件
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PullRequestHook implements Webhook {
    public static final String CLASS_TYPE = "pull_request";
    private EventAction action;
    private GitScmServerRepository repo;
    @NonNull
    private String eventType;
    private PullRequest pullRequest;
    // pr 创建者
    private User sender;
    // pr源分支最后的commit信息
    private Commit commit;
    // 变更的文件路径
    private List<Change> changes;
    // 扩展属性,提供者额外补充需要输出的变量
    private Map<String, Object> extras;

    @Override
    public GitScmServerRepository repository() {
        return repo;
    }

    @Override
    public String getUserName() {
        return sender.getName();
    }

    @Override
    public ScmI18Variable getEventDesc() {
        List<String> params = Arrays.asList(

                pullRequest.getLink(),
                String.valueOf(pullRequest.getNumber()),
                sender.getName()
        );
        return ScmI18Variable.builder()
                .code(getI18Code())
                .params(params)
                .build();
    }

    @Override
    public Map<String, Object> outputs() {
        Map<String, Object> outputParams = new HashMap<>();
        outputParams.put(BK_GIT_MR_NUMBER, pullRequest.getNumber());
        outputParams.put(BK_HOOK_MR_ID, pullRequest.getId());
        outputParams.put(
                BK_REPO_GIT_WEBHOOK_BRANCH,
                action == EventAction.MERGE ? pullRequest.getTargetRef().getName() :
                        pullRequest.getSourceRef().getName()
        );
        outputParams.put(BK_REPO_GIT_WEBHOOK_COMMIT_ID, commit.getSha());
        outputParams.put(BK_REPO_GIT_WEBHOOK_MR_LAST_COMMIT, commit.getSha());
        outputParams.put(BK_REPO_GIT_WEBHOOK_MR_LAST_COMMIT_MSG, commit.getMessage());
        outputParams.put(BK_REPO_GIT_WEBHOOK_MR_MERGE_COMMIT_SHA, pullRequest.getMergeCommitSha());
        outputParams.put(BK_REPO_GIT_WEBHOOK_MR_MERGE_TYPE, pullRequest.getMergeType());
        String sourceRepoUrl = pullRequest.getSourceRepo().getHttpUrl();
        String targetRepoUrl = pullRequest.getTargetRepo().getHttpUrl();
        outputParams.put(BK_REPO_GIT_WEBHOOK_MR_SOURCE_URL, sourceRepoUrl);
        outputParams.put(BK_REPO_GIT_WEBHOOK_MR_TARGET_URL, targetRepoUrl);
        outputParams.put(BK_REPO_GIT_WEBHOOK_MR_URL, pullRequest.getLink());
        // 当extras相关变量获取失败时，尝试使用webhook body中的信息进行填充
        outputParams.putIfAbsent(BK_REPO_GIT_WEBHOOK_MR_ID, pullRequest.getId());
        outputParams.putIfAbsent(BK_REPO_GIT_WEBHOOK_MR_NUMBER, pullRequest.getNumber());
        outputParams.putIfAbsent(
                BK_REPO_GIT_WEBHOOK_MR_DESCRIPTION,
                StringUtils.substring(
                        StringUtils.defaultString(pullRequest.getDescription(), ""),
                        0,
                        PR_DESC_MAX_LENGTH
                )
        );
        outputParams.putIfAbsent(BK_REPO_GIT_WEBHOOK_MR_SOURCE_BRANCH, pullRequest.getSourceRef().getName());

        outputParams.put(PIPELINE_GIT_REPO, repo.getFullName());
        outputParams.put(PIPELINE_GIT_REPO_NAME, repo.getName());
        outputParams.put(PIPELINE_GIT_REPO_GROUP, repo.getGroup());
        outputParams.put(PIPELINE_GIT_ACTION, action);
        outputParams.put(PIPELINE_GIT_BASE_REF, pullRequest.getSourceRef().getName());
        outputParams.put(PIPELINE_GIT_BASE_REPO_URL, sourceRepoUrl);
        outputParams.put(PIPELINE_GIT_COMMIT_AUTHOR, commit.getAuthor().getName());
        outputParams.put(PIPELINE_GIT_COMMIT_MESSAGE, commit.getMessage());
        outputParams.put(PIPELINE_GIT_EVENT, eventType.toLowerCase());
        outputParams.put(PIPELINE_GIT_EVENT_URL, pullRequest.getLink());
        outputParams.put(PIPELINE_GIT_HEAD_REPO_URL, targetRepoUrl);
        outputParams.put(PIPELINE_GIT_MR_ACTION, action.value);
        outputParams.put(PIPELINE_GIT_MR_DESC, pullRequest.getDescription());
        outputParams.put(PIPELINE_GIT_MR_URL, pullRequest.getLink());
        outputParams.put(PIPELINE_GIT_REPO_ID, repo.getId());
        outputParams.put(PIPELINE_GIT_REPO_URL, targetRepoUrl);
        outputParams.put(PIPELINE_GIT_SHA, commit.getSha());
        outputParams.put(PIPELINE_REPO_NAME, repo.getFullName());
        outputParams.put(PIPELINE_START_WEBHOOK_USER_ID, sender.getUsername());
        outputParams.put(PIPELINE_WEBHOOK_BRANCH, pullRequest.getTargetRef().getName());
        outputParams.put(PIPELINE_WEBHOOK_COMMIT_MESSAGE, pullRequest.getTitle());
        outputParams.put(PIPELINE_WEBHOOK_EVENT_TYPE, eventType);
        outputParams.put(PIPELINE_WEBHOOK_REVISION, commit.getSha());
        outputParams.put(PIPELINE_WEBHOOK_SOURCE_REPO_NAME, pullRequest.getSourceRepo().getFullName());
        outputParams.put(PIPELINE_WEBHOOK_SOURCE_URL, sourceRepoUrl);
        outputParams.put(PIPELINE_WEBHOOK_TARGET_REPO_NAME, pullRequest.getTargetRepo().getFullName());
        outputParams.put(PIPELINE_WEBHOOK_TARGET_URL, targetRepoUrl);
        if (extras != null) {
            outputParams.putAll(extras);
        }
        return outputParams;
    }

    private String getI18Code() {
        String code;
        switch (action) {
            case OPEN:
                code = GIT_PR_CREATED_EVENT_DESC;
                break;
            case CLOSE:
                code = GIT_PR_CLOSED_EVENT_DESC;
                break;
            case EDIT:
                code = GIT_PR_UPDATED_EVENT_DESC;
                break;
            case PUSH_UPDATE:
                code = GIT_PR_PUSH_UPDATED_EVENT_DESC;
                break;
            case REOPEN:
                code = GIT_PR_REOPENED_EVENT_DESC;
                break;
            case MERGE:
                code = GIT_PR_MERGED_EVENT_DESC;
                break;
            default:
                code = "";
                break;
        }
        return code;
    }
}
