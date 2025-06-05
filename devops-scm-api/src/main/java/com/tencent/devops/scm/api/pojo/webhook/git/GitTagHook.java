package com.tencent.devops.scm.api.pojo.webhook.git;

import static com.tencent.devops.scm.api.constant.WebhookI18Code.GIT_TAG_DELETE_EVENT_DESC;
import static com.tencent.devops.scm.api.constant.WebhookI18Code.GIT_TAG_PUSH_EVENT_DESC;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_BRANCH;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_COMMIT_ID;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_TAG_NAME;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_TAG_USERNAME;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.CI_BRANCH;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_ACTION;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_EVENT;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_EVENT_URL;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_REF;
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

import com.tencent.devops.scm.api.enums.EventAction;
import com.tencent.devops.scm.api.pojo.Commit;
import com.tencent.devops.scm.api.pojo.Reference;
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
 * 代表tag事件
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GitTagHook implements Webhook {
    public static final String CLASS_TYPE = "git_tag";
    private Reference ref;
    private GitScmServerRepository repo;
    @NonNull
    private String eventType;
    private EventAction action;
    private User sender;
    private Commit commit;
    private String createFrom;
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
        String code = GIT_TAG_PUSH_EVENT_DESC;
        if (action == EventAction.DELETE) {
            code = GIT_TAG_DELETE_EVENT_DESC;
        }
        List<String> params = Arrays.asList(
                createFrom,
                ref.getLinkUrl(),
                ref.getName(),
                sender.getName()
        );
        return ScmI18Variable.builder()
                .code(code)
                .params(params)
                .build();
    }

    @Override
    public Map<String, Object> outputs() {
        Map<String, Object> outputParams = new HashMap<>();

        // 通用变量
        outputParams.put(PIPELINE_WEBHOOK_REVISION, commit.getSha());
        outputParams.put(PIPELINE_REPO_NAME, repo.getFullName());
        outputParams.put(PIPELINE_START_WEBHOOK_USER_ID, sender.getUsername());
        outputParams.put(PIPELINE_WEBHOOK_EVENT_TYPE, eventType);
        outputParams.put(PIPELINE_WEBHOOK_COMMIT_MESSAGE, ref.getName());
        outputParams.put(PIPELINE_WEBHOOK_BRANCH, ref.getName());

        // 传统变量
        outputParams.put(BK_REPO_GIT_WEBHOOK_TAG_NAME, ref.getName());
        outputParams.put(BK_REPO_GIT_WEBHOOK_TAG_USERNAME, sender.getName());
        outputParams.put(BK_REPO_GIT_WEBHOOK_BRANCH, ref.getName());
        outputParams.put(BK_REPO_GIT_WEBHOOK_COMMIT_ID, commit.getSha());

        // ci上下文
        outputParams.put(PIPELINE_GIT_REPO_ID, repo.getId());
        outputParams.put(PIPELINE_GIT_REPO_URL, repo.getHttpUrl());
        outputParams.put(PIPELINE_GIT_REPO, repo.getFullName());
        outputParams.put(PIPELINE_GIT_REPO_NAME, repo.getName());
        outputParams.put(PIPELINE_GIT_REPO_GROUP, repo.getGroup());
        outputParams.put(PIPELINE_GIT_REF, "refs/tags/" + ref.getName()); // ci上需要的是refs/tags/xxx
        outputParams.put(PIPELINE_GIT_SHA, commit.getSha());
        outputParams.put(CI_BRANCH, ref.getName());
        if (action == EventAction.DELETE) {
            outputParams.put(PIPELINE_GIT_EVENT, "delete");
        } else {
            outputParams.put(PIPELINE_GIT_EVENT, "tag_push");
        }
        outputParams.put(PIPELINE_GIT_EVENT_URL, ref.getLinkUrl());
        outputParams.put(PIPELINE_GIT_ACTION, action.value);
        if (extras != null) {
            outputParams.putAll(extras);
        }
        return outputParams;
    }
}
