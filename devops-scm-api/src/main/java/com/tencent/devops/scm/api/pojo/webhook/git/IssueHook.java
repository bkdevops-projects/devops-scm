package com.tencent.devops.scm.api.pojo.webhook.git;

import static com.tencent.devops.scm.api.constant.WebhookI18Code.GIT_ISSUE_CLOSED_EVENT_DESC;
import static com.tencent.devops.scm.api.constant.WebhookI18Code.GIT_ISSUE_OPENED_EVENT_DESC;
import static com.tencent.devops.scm.api.constant.WebhookI18Code.GIT_ISSUE_REOPENED_EVENT_DESC;
import static com.tencent.devops.scm.api.constant.WebhookI18Code.GIT_ISSUE_UPDATED_EVENT_DESC;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_ISSUE_ACTION;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_ISSUE_DESCRIPTION;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_ISSUE_ID;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_ISSUE_IID;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_ISSUE_MILESTONE_ID;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_ISSUE_OWNER;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_ISSUE_TITLE;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_ISSUE_URL;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_ACTION;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_COMMIT_MESSAGE;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_EVENT;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_EVENT_URL;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_REPO_ID;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_REPO_URL;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_REPO_NAME;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_START_WEBHOOK_USER_ID;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_WEBHOOK_COMMIT_MESSAGE;

import com.tencent.devops.scm.api.enums.EventAction;
import com.tencent.devops.scm.api.pojo.Issue;
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

/**
 * issue事件
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IssueHook implements Webhook {
    public static final String CLASS_TYPE = "issue";
    private EventAction action;
    private GitScmServerRepository repo;
    @NonNull
    private String eventType;
    private Issue issue;
    private User sender;
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
                issue.getLink(),
                String.valueOf(issue.getNumber()),
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
        if (extras != null) {
            outputParams.putAll(extras);
        }
        outputParams.put(BK_REPO_GIT_WEBHOOK_ISSUE_TITLE, issue.getTitle());
        outputParams.put(BK_REPO_GIT_WEBHOOK_ISSUE_ID, issue.getId().toString());
        outputParams.put(BK_REPO_GIT_WEBHOOK_ISSUE_IID, issue.getNumber().toString());
        outputParams.put(BK_REPO_GIT_WEBHOOK_ISSUE_DESCRIPTION, issue.getBody());
        outputParams.put(BK_REPO_GIT_WEBHOOK_ISSUE_OWNER, issue.getAuthor().getName());
        outputParams.put(BK_REPO_GIT_WEBHOOK_ISSUE_URL, issue.getLink());
        outputParams.put(BK_REPO_GIT_WEBHOOK_ISSUE_ACTION, action.value);

        outputParams.put(PIPELINE_GIT_COMMIT_MESSAGE, issue.getTitle());
        outputParams.put(PIPELINE_GIT_REPO_URL, repo.getHttpUrl());
        outputParams.put(PIPELINE_GIT_EVENT_URL, issue.getLink());
        outputParams.put(PIPELINE_GIT_ACTION, action.value);
        outputParams.put(PIPELINE_WEBHOOK_COMMIT_MESSAGE, issue.getTitle());
        outputParams.put(BK_REPO_GIT_WEBHOOK_ISSUE_MILESTONE_ID, issue.getMilestoneId());
        outputParams.put(PIPELINE_REPO_NAME, repo.getFullName());
        outputParams.put(PIPELINE_GIT_REPO_ID, repo.getId());
        outputParams.put(PIPELINE_START_WEBHOOK_USER_ID, sender.getName());
        outputParams.put(PIPELINE_GIT_EVENT, "issue");
        return outputParams;
    }

    private String getI18Code() {
        String code;
        switch (action) {
            case OPEN:
                code = GIT_ISSUE_OPENED_EVENT_DESC;
                break;
            case CLOSE:
                code = GIT_ISSUE_CLOSED_EVENT_DESC;
                break;
            case UPDATE:
                code = GIT_ISSUE_UPDATED_EVENT_DESC;
                break;
            case REOPEN:
                code = GIT_ISSUE_REOPENED_EVENT_DESC;
                break;
            default:
                code = "";
                break;
        }
        return code;
    }
}
