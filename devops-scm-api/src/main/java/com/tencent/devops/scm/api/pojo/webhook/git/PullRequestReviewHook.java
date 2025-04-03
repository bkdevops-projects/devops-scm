package com.tencent.devops.scm.api.pojo.webhook.git;

import static com.tencent.devops.scm.api.constant.WebhookI18Code.GIT_REVIEW_APPROVED_EVENT_DESC;
import static com.tencent.devops.scm.api.constant.WebhookI18Code.GIT_REVIEW_APPROVING_EVENT_DESC;
import static com.tencent.devops.scm.api.constant.WebhookI18Code.GIT_REVIEW_CHANGE_DENIED_EVENT_DESC;
import static com.tencent.devops.scm.api.constant.WebhookI18Code.GIT_REVIEW_CHANGE_REQUIRED_EVENT_DESC;
import static com.tencent.devops.scm.api.constant.WebhookI18Code.GIT_REVIEW_CLOSED_EVENT_DESC;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_REVIEW_ID;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_REVIEW_IID;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_REVIEW_STATE;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_ACTION;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_EVENT;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_EVENT_URL;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_REPO;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_REPO_GROUP;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_REPO_ID;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_REPO_NAME;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_REPO_URL;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_REPO_NAME;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_WEBHOOK_COMMIT_MESSAGE;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_WEBHOOK_EVENT_TYPE;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_WEBHOOK_REVISION;

import com.tencent.devops.scm.api.enums.EventAction;
import com.tencent.devops.scm.api.enums.ReviewState;
import com.tencent.devops.scm.api.pojo.PullRequest;
import com.tencent.devops.scm.api.pojo.Review;
import com.tencent.devops.scm.api.pojo.ScmI18Variable;
import com.tencent.devops.scm.api.pojo.User;
import com.tencent.devops.scm.api.pojo.repository.ScmServerRepository;
import com.tencent.devops.scm.api.pojo.repository.git.GitScmServerRepository;
import com.tencent.devops.scm.api.pojo.webhook.Webhook;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * 代码评审事件
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PullRequestReviewHook implements Webhook {

    public static final String CLASS_TYPE = "pull_request_review";
    private EventAction action;
    private GitScmServerRepository repo;
    @NonNull
    private String eventType;
    private PullRequest pullRequest;
    private Review review;
    private User sender;
    // 扩展属性,提供者额外补充需要输出的变量
    private Map<String, Object> extras;

    @Override
    public ScmServerRepository repository() {
        return repo;
    }

    @Override
    public String getUserName() {
        return sender.getName();
    }

    @Override
    public ScmI18Variable getEventDesc() {
        ReviewState state = review.getState();
        String eventUser = getUserName();
        List<String> params = Arrays.asList(
                review.getLink(),
                review.getIid().toString(),
                eventUser
        );
        return ScmI18Variable.builder()
                .code(getI18Code(state))
                .params(params)
                .build();
    }

    @Override
    public Map<String, Object> outputs() {
        Map<String, Object> outputParams = new HashMap<>();
        if (extras != null) {
            outputParams.putAll(extras);
        }
        outputParams.put(BK_REPO_GIT_WEBHOOK_REVIEW_ID, review.getId().toString());
        outputParams.put(BK_REPO_GIT_WEBHOOK_REVIEW_IID, review.getIid().toString());
        outputParams.put(BK_REPO_GIT_WEBHOOK_REVIEW_STATE, review.getState().value);

        outputParams.put(PIPELINE_WEBHOOK_EVENT_TYPE, eventType);
        outputParams.put(PIPELINE_GIT_EVENT_URL, review.getLink());
        outputParams.put(PIPELINE_GIT_REPO_URL, repo.getHttpUrl());
        outputParams.put(PIPELINE_GIT_EVENT, "review");
        outputParams.put(PIPELINE_GIT_ACTION, action.value);
        outputParams.put(
                PIPELINE_WEBHOOK_COMMIT_MESSAGE,
                Optional.ofNullable(pullRequest)
                        .map(PullRequest::getTitle)
                        .orElse(
                                Optional.of(review)
                                        .map(Review::getTitle)
                                        .orElse("")
                        )
        );
        outputParams.put(PIPELINE_WEBHOOK_REVISION, "");
        outputParams.put(PIPELINE_GIT_REPO_ID, repo.getId());
        outputParams.put(PIPELINE_GIT_REPO, repo.getFullName());
        outputParams.put(PIPELINE_GIT_REPO_NAME, repo.getName());
        outputParams.put(PIPELINE_GIT_REPO_GROUP, repo.getGroup());
        outputParams.put(PIPELINE_REPO_NAME, repo.getName());
        return outputParams;
    }

    private String getI18Code(ReviewState state) {
        switch (state) {
            case APPROVING : return GIT_REVIEW_APPROVING_EVENT_DESC;
            case APPROVED : return GIT_REVIEW_APPROVED_EVENT_DESC;
            case CHANGES_REQUESTED : return GIT_REVIEW_CHANGE_REQUIRED_EVENT_DESC;
            case CHANGE_DENIED : return GIT_REVIEW_CHANGE_DENIED_EVENT_DESC;
            case CLOSED : return GIT_REVIEW_CLOSED_EVENT_DESC;
            default: return "";
        }
    }
}
