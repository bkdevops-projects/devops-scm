package com.tencent.devops.scm.api.pojo.webhook.git;

import com.tencent.devops.scm.api.pojo.Commit;
import java.util.Map;
import java.util.Optional;

import com.tencent.devops.scm.api.pojo.Signature;
import com.tencent.devops.scm.api.util.GitUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_COMMIT_AUTHOR;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_COMMIT_MESSAGE;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_SHA;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_SHA_SHORT;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
public class CommitCommentHook extends AbstractCommentHook {
    public static final String CLASS_TYPE = "commit_comment";
    private Commit commit;

    @Override
    public Map<String, Object> outputs() {
        Map<String, Object> outputs = super.outputs();
        if (commit != null) {
            outputs.put(
                    PIPELINE_GIT_COMMIT_AUTHOR,
                    Optional.ofNullable(commit.getCommitter()).map(Signature::getName).orElse("")
            );
            outputs.put(PIPELINE_GIT_SHA, commit.getSha());
            outputs.put(PIPELINE_GIT_SHA_SHORT, GitUtils.getShortSha(commit.getSha()));
            outputs.put(PIPELINE_GIT_COMMIT_MESSAGE, commit.getMessage());
        }
        if (getExtras() != null) {
            outputs.putAll(getExtras());
        }
        return outputs;
    }
}
