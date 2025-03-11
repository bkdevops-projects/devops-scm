package com.tencent.devops.scm.api.util;

import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_PUSH_ADD_FILE_COUNT;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_PUSH_ADD_FILE_PREFIX;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_PUSH_COMMIT_AUTHOR_PREFIX;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_PUSH_COMMIT_MSG_PREFIX;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_PUSH_COMMIT_PREFIX;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_PUSH_COMMIT_TIMESTAMP_PREFIX;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_PUSH_DELETE_FILE_COUNT;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_PUSH_DELETE_FILE_PREFIX;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_PUSH_MODIFY_FILE_COUNT;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_PUSH_MODIFY_FILE_PREFIX;
import static com.tencent.devops.scm.api.constant.WebhookOutputConstants.MAX_VARIABLE_COUNT;

import com.tencent.devops.scm.api.pojo.Commit;
import org.apache.commons.lang3.StringUtils;

import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GitUtils {

    public static String expandRef(String name, String prefix) {
        prefix = StringUtils.removeEnd(prefix, "/");
        if (StringUtils.startsWith(name, prefix)) {
            return name;
        }
        return prefix + "/" + name;
    }

    /**
     * 移除分支或tag前缀
     * @param ref 分支或tag
     */
    public static String trimRef(String ref) {
        ref = StringUtils.removeStart(ref, "refs/heads/");
        ref = StringUtils.removeStart(ref, "refs/tags/");
        return ref;
    }

    public static String getShortSha(String commitId) {
        String shortSha = "";
        if (commitId != null && commitId.length() > 8) {
            shortSha = commitId.substring(0, 8);
        }
        return shortSha;
    }

    /**
     * 获取提交变更信息
     * @param commits 提交记录
     * @return
     */
    public static Map<String, Object> getOutputCommitIndexVar(List<Commit> commits) {
        Map<String, Object> startParams = new HashMap<>();
        int addCount = 0;
        int modifyCount = 0;
        int deleteCount = 0;

        for (int index = 0; index < commits.size(); index++) {
            Commit gitCommit = commits.get(index);
            int curIndex = index + 1;
            startParams.put(BK_REPO_GIT_WEBHOOK_PUSH_COMMIT_PREFIX + curIndex, gitCommit.getSha());
            startParams.put(BK_REPO_GIT_WEBHOOK_PUSH_COMMIT_MSG_PREFIX + curIndex, gitCommit.getMessage());
            startParams.put(BK_REPO_GIT_WEBHOOK_PUSH_COMMIT_TIMESTAMP_PREFIX + curIndex,
                    gitCommit.getCommitTime().atZone(ZoneId.systemDefault()).toEpochSecond() * 1000);
            startParams.put(BK_REPO_GIT_WEBHOOK_PUSH_COMMIT_AUTHOR_PREFIX + curIndex, gitCommit.getAuthor());

            addCount += gitCommit.getAdded() != null ? gitCommit.getAdded().size() : 0;
            modifyCount += gitCommit.getModified() != null ? gitCommit.getModified().size() : 0;
            deleteCount += gitCommit.getRemoved() != null ? gitCommit.getRemoved().size() : 0;

            int count = 0;

            if (gitCommit.getAdded() != null) {
                List<String> added = gitCommit.getAdded();
                for (int innerIndex = 0; innerIndex < added.size(); innerIndex++) {
                    startParams.put(BK_REPO_GIT_WEBHOOK_PUSH_ADD_FILE_PREFIX + curIndex + "_" + (innerIndex + 1),
                            added.get(innerIndex));
                    count++;
                    if (count > MAX_VARIABLE_COUNT) {
                        break;
                    }
                }
            }

            if (gitCommit.getModified() != null) {
                List<String> modified = gitCommit.getModified();
                for (int innerIndex = 0; innerIndex < modified.size(); innerIndex++) {
                    startParams.put(BK_REPO_GIT_WEBHOOK_PUSH_MODIFY_FILE_PREFIX + curIndex + "_" + (innerIndex + 1),
                            modified.get(innerIndex));
                    count++;
                    if (count > MAX_VARIABLE_COUNT) {
                        break;
                    }
                }
            }

            if (gitCommit.getRemoved() != null) {
                List<String> removed = gitCommit.getRemoved();
                for (int innerIndex = 0; innerIndex < removed.size(); innerIndex++) {
                    startParams.put(BK_REPO_GIT_WEBHOOK_PUSH_DELETE_FILE_PREFIX + curIndex + "_" + (innerIndex + 1),
                            removed.get(innerIndex));
                    count++;
                    if (count > MAX_VARIABLE_COUNT) {
                        break;
                    }
                }
            }
        }

        startParams.put(BK_REPO_GIT_WEBHOOK_PUSH_ADD_FILE_COUNT, addCount);
        startParams.put(BK_REPO_GIT_WEBHOOK_PUSH_MODIFY_FILE_COUNT, modifyCount);
        startParams.put(BK_REPO_GIT_WEBHOOK_PUSH_DELETE_FILE_COUNT, deleteCount);

        return startParams;
    }
}
