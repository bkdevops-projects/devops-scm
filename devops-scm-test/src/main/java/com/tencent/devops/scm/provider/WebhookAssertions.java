package com.tencent.devops.scm.provider;

import com.tencent.devops.scm.api.pojo.Commit;
import com.tencent.devops.scm.api.pojo.repository.git.GitScmServerRepository;
import com.tencent.devops.scm.api.pojo.webhook.git.GitPushHook;
import org.junit.jupiter.api.Assertions;

public class WebhookAssertions {

    public static void assertGitPushHook(GitPushHook webhook) {
        Assertions.assertNotNull(webhook.getAction());
        Assertions.assertNotNull(webhook.getRef());
        Assertions.assertNotNull(webhook.getEventType());
        Assertions.assertNotNull(webhook.getSender());

        assertGitScmServerRepository(webhook.getRepo());

        // 判断是否包含.ci文件,如果有ci文件,那么commit不能为空
        boolean changeCiFile = webhook.getChanges()
                .stream()
                .anyMatch(change -> change.getPath().contains(".ci"));
        if (changeCiFile) {
            Commit commit = webhook.getCommit();
            Assertions.assertNotNull(commit);
            Assertions.assertNotNull(commit.getSha());
            Assertions.assertNotNull(commit.getMessage());
            Assertions.assertNotNull(commit.getLink());
            Assertions.assertNotNull(commit.getCommitTime());
            Assertions.assertNotNull(commit.getCommitter());
        }

    }

    public static void assertGitScmServerRepository(GitScmServerRepository repo) {
        Assertions.assertNotNull(repo.getFullName());
        Assertions.assertNotNull(repo.getDefaultBranch());
        Assertions.assertNotNull(repo.getHttpUrl());
    }
}
