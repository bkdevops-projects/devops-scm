package com.tencent.devops.scm.api.pojo.webhook.git;

import com.tencent.devops.scm.api.constant.DateFormatConstants;
import com.tencent.devops.scm.api.enums.EventAction;
import com.tencent.devops.scm.api.pojo.Comment;
import com.tencent.devops.scm.api.pojo.ScmI18Variable;
import com.tencent.devops.scm.api.pojo.User;
import com.tencent.devops.scm.api.pojo.repository.git.GitScmServerRepository;
import com.tencent.devops.scm.api.pojo.webhook.Webhook;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

import static com.tencent.devops.scm.api.constant.WebhookI18Code.GIT_NOTE_EVENT_DESC;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_NOTE_AUTHOR_ID;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_NOTE_COMMENT;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_NOTE_CREATED_AT;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_NOTE_ID;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_NOTE_NOTEABLE_TYPE;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_NOTE_UPDATED_AT;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_NOTE_URL;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_EVENT;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_EVENT_URL;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_REPO;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_REPO_GROUP;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_REPO_ID;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_REPO_NAME;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_REPO_URL;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_REPO_NAME;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_START_WEBHOOK_USER_ID;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_WEBHOOK_COMMIT_MESSAGE;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_WEBHOOK_EVENT_TYPE;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_WEBHOOK_NOTE_COMMENT;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_WEBHOOK_NOTE_ID;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_WEBHOOK_REVISION;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public abstract class AbstractCommentHook implements Webhook {

    private EventAction action;
    private GitScmServerRepository repo;
    @NonNull
    private String eventType;
    private Comment comment;
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
                comment.getLink(),
                comment.getId().toString(),
                sender.getName()
        );
        return ScmI18Variable.builder()
                .code(GIT_NOTE_EVENT_DESC)
                .params(params)
                .build();
    }

    @Override
    public Map<String, Object> outputs() {
        Map<String, Object> outputParams = new HashMap<>();
        outputParams.put(PIPELINE_WEBHOOK_EVENT_TYPE, eventType);
        outputParams.put(PIPELINE_WEBHOOK_COMMIT_MESSAGE, comment.getBody());
        outputParams.put(PIPELINE_WEBHOOK_NOTE_COMMENT, comment.getBody());
        outputParams.put(BK_REPO_GIT_WEBHOOK_NOTE_COMMENT, comment.getBody());
        outputParams.put(PIPELINE_REPO_NAME, repo.getFullName());
        outputParams.put(PIPELINE_START_WEBHOOK_USER_ID, sender.getName());

        outputParams.put(BK_REPO_GIT_WEBHOOK_NOTE_AUTHOR_ID, sender.getId());
        outputParams.put(BK_REPO_GIT_WEBHOOK_NOTE_ID, comment.getId());
        outputParams.put(PIPELINE_WEBHOOK_NOTE_ID, comment.getId());
        outputParams.put(BK_REPO_GIT_WEBHOOK_NOTE_NOTEABLE_TYPE, comment.getType());
        outputParams.put(BK_REPO_GIT_WEBHOOK_NOTE_URL, comment.getLink());
        outputParams.put(
                BK_REPO_GIT_WEBHOOK_NOTE_CREATED_AT,
                new SimpleDateFormat(DateFormatConstants.ISO_8601).format(comment.getCreated())
        );
        outputParams.put(
                BK_REPO_GIT_WEBHOOK_NOTE_UPDATED_AT,
                new SimpleDateFormat(DateFormatConstants.ISO_8601).format(comment.getUpdated())
        );
        outputParams.put(PIPELINE_GIT_EVENT, "note");
        outputParams.put(PIPELINE_GIT_EVENT_URL, comment.getLink());
        outputParams.put(PIPELINE_GIT_REPO_ID, repo.getId());
        outputParams.put(PIPELINE_GIT_REPO, repo.getFullName());
        outputParams.put(PIPELINE_GIT_REPO_NAME, repo.getName());
        outputParams.put(PIPELINE_GIT_REPO_GROUP, repo.getGroup());
        outputParams.put(PIPELINE_GIT_REPO_URL, repo.getHttpUrl());
        outputParams.put(PIPELINE_WEBHOOK_REVISION, "");
        return outputParams;
    }
}
