package com.tencent.devops.scm.api.pojo.webhook.git;

import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_BRANCH;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_COMMIT_ID;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_PUSH_AFTER_COMMIT;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_PUSH_BEFORE_COMMIT;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_PUSH_PROJECT_ID;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_PUSH_TOTAL_COMMIT;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_PUSH_USERNAME;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.CI_BRANCH;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_ACTION;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_BEFORE_SHA;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_BEFORE_SHA_SHORT;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_COMMIT_AUTHOR;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_COMMIT_MESSAGE;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_EVENT;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_EVENT_URL;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_REF;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_REPO;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_REPO_GROUP;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_REPO_ID;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_REPO_NAME;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_REPO_URL;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_SHA;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_SHA_SHORT;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_REPO_NAME;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_START_WEBHOOK_USER_ID;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_WEBHOOK_BRANCH;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_WEBHOOK_COMMIT_MESSAGE;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_WEBHOOK_EVENT_TYPE;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_WEBHOOK_REVISION;

import com.tencent.devops.scm.api.constant.WebhookI18Code;
import com.tencent.devops.scm.api.enums.EventAction;
import com.tencent.devops.scm.api.pojo.Change;
import com.tencent.devops.scm.api.pojo.Commit;
import com.tencent.devops.scm.api.pojo.ScmI18Variable;
import com.tencent.devops.scm.api.pojo.User;
import com.tencent.devops.scm.api.pojo.repository.git.GitScmServerRepository;
import com.tencent.devops.scm.api.pojo.webhook.Webhook;
import com.tencent.devops.scm.api.util.GitUtils;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.apache.commons.collections4.CollectionUtils;

/**
 * git push事件
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GitPushHook implements Webhook {
    public static final String CLASS_TYPE = "git_push";

    private EventAction action;
    // 分支应该不带refs/heads
    private String ref;
    private String baseRef;
    private GitScmServerRepository repo;
    @NonNull
    private String eventType;
    private String before;
    private String after;
    private Commit commit;
    private User sender;
    private List<Commit> commits;
    // 变更的文件路径
    private List<Change> changes;
    private Integer totalCommitsCount;
    // 扩展属性,提供者额外补充需要输出的变量
    private Map<String, Object> extras;
    // 是否输出commit index变量,如BK_REPO_GIT_WEBHOOK_PUSH_COMMIT_,兼容历史数据,后续接入的都不应该输出
    private Boolean outputCommitIndexVar;
    // 是否跳过
    private boolean skipCi;

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
        String code = WebhookI18Code.GIT_PUSH_EVENT_DESC;
        if (action == EventAction.DELETE) {
            code = WebhookI18Code.GIT_PUSH_DELETE_EVENT_DESC;
        }
        List<String> params = Arrays.asList(
                GitUtils.trimRef(ref),
                commit.getLink(),
                GitUtils.getShortSha(commit.getSha()),
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
        if (extras != null) {
            outputParams.putAll(extras);
        }
        outputParams.put(PIPELINE_WEBHOOK_REVISION, commit.getSha());
        outputParams.put(PIPELINE_REPO_NAME, repo.getFullName());
        outputParams.put(PIPELINE_START_WEBHOOK_USER_ID, sender.getName());
        outputParams.put(PIPELINE_WEBHOOK_EVENT_TYPE, eventType);
        outputParams.put(PIPELINE_WEBHOOK_COMMIT_MESSAGE, commit.getMessage());
        outputParams.put(PIPELINE_WEBHOOK_BRANCH, ref);

        outputParams.put(BK_REPO_GIT_WEBHOOK_PUSH_USERNAME, sender.getName());
        outputParams.put(BK_REPO_GIT_WEBHOOK_PUSH_BEFORE_COMMIT, before);
        outputParams.put(BK_REPO_GIT_WEBHOOK_PUSH_AFTER_COMMIT, after);
        outputParams.put(BK_REPO_GIT_WEBHOOK_PUSH_TOTAL_COMMIT,
                totalCommitsCount != null ? totalCommitsCount : commits.size());
        outputParams.put(BK_REPO_GIT_WEBHOOK_PUSH_PROJECT_ID, repo.getId());
        outputParams.put(BK_REPO_GIT_WEBHOOK_BRANCH, ref);
        outputParams.put(BK_REPO_GIT_WEBHOOK_COMMIT_ID, commit.getSha());

        // ci上下文变量,会转换成ci.xxx
        outputParams.put(PIPELINE_GIT_REPO_ID, repo.getId());
        outputParams.put(PIPELINE_GIT_REPO, repo.getFullName());
        outputParams.put(PIPELINE_GIT_REPO_NAME, repo.getName());
        outputParams.put(PIPELINE_GIT_REPO_GROUP, repo.getGroup());
        outputParams.put(PIPELINE_GIT_REPO_URL, repo.getHttpUrl());
        outputParams.put(PIPELINE_GIT_REF, "refs/heads/" + ref);
        outputParams.put(CI_BRANCH, ref);
        if (action == EventAction.DELETE) {
            outputParams.put(PIPELINE_GIT_EVENT, "delete");
        } else {
            outputParams.put(PIPELINE_GIT_EVENT, "push");
        }
        CollectionUtils.emptyIfNull(commits).stream().filter(commit -> commit.getSha().equals(after))
                .findFirst()
                .ifPresent(
                        commit -> {
                            outputParams.put(PIPELINE_GIT_COMMIT_AUTHOR, commit.getAuthor().getName());
                        }
                );
        outputParams.put(PIPELINE_GIT_BEFORE_SHA, before);
        outputParams.put(PIPELINE_GIT_BEFORE_SHA_SHORT, GitUtils.getShortSha(before));
        outputParams.put(PIPELINE_GIT_ACTION, action.value);
        outputParams.put(PIPELINE_GIT_EVENT_URL, commit.getLink());
        outputParams.put(PIPELINE_GIT_COMMIT_MESSAGE, commit.getMessage());
        outputParams.put(PIPELINE_GIT_SHA_SHORT, GitUtils.getShortSha(commit.getSha()));
        outputParams.put(PIPELINE_GIT_SHA, commit.getSha());
        if (Boolean.TRUE.equals(outputCommitIndexVar)) {
            outputParams.putAll(GitUtils.getOutputCommitIndexVar(commits));
        }
        return outputParams;
    }

    @Override
    public Boolean skipCi() {
        return skipCi;
    }
}
