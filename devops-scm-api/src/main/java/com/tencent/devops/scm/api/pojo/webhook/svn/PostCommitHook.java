package com.tencent.devops.scm.api.pojo.webhook.svn;

import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_SVN_WEBHOOK_COMMIT_TIME;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_SVN_WEBHOOK_REVERSION;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_SVN_WEBHOOK_USERNAME;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_WEBHOOK_COMMIT_MESSAGE;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_WEBHOOK_EVENT_TYPE;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_WEBHOOK_REVISION;

import com.tencent.devops.scm.api.constant.WebhookI18Code;
import com.tencent.devops.scm.api.pojo.Change;
import com.tencent.devops.scm.api.pojo.ScmI18Variable;
import com.tencent.devops.scm.api.pojo.User;
import com.tencent.devops.scm.api.pojo.repository.ScmServerRepository;
import com.tencent.devops.scm.api.pojo.repository.svn.SvnScmServerRepository;
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

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostCommitHook implements Webhook {

    public static final String CLASS_TYPE = "post_commit";
    private SvnScmServerRepository repository;
    // 变更的文件路径
    private List<Change> changes;
    private String message;
    private User sender;

    @NonNull
    private String eventType;
    // 扩展属性,提供者额外补充需要输出的变量
    private Map<String, Object> extras;
    private Long revision;
    private Long commitTime;
    private boolean skipCi;

    @Override
    public ScmServerRepository repository() {
        return repository;
    }

    @Override
    public String getUserName() {
        return sender.getName();
    }

    @Override
    public ScmI18Variable getEventDesc() {
        return ScmI18Variable.builder()
                .code(WebhookI18Code.SVN_POST_COMMIT)
                .params(Arrays.asList(revision.toString(), sender.getName()))
                .build();
    }

    @Override
    public Map<String, Object> outputs() {
        Map<String, Object> outputParams = new HashMap<>();
        outputParams.put(BK_REPO_SVN_WEBHOOK_REVERSION, revision);
        outputParams.put(BK_REPO_SVN_WEBHOOK_USERNAME, sender.getName());
        outputParams.put(BK_REPO_SVN_WEBHOOK_COMMIT_TIME, commitTime);
        outputParams.put(PIPELINE_WEBHOOK_COMMIT_MESSAGE, message);
        outputParams.put(PIPELINE_WEBHOOK_REVISION, revision);
        outputParams.put(PIPELINE_WEBHOOK_EVENT_TYPE, eventType);
        if (extras != null) {
            outputParams.putAll(extras);
        }
        return outputParams;
    }

    @Override
    public Boolean skipCi() {
        return skipCi;
    }
}
