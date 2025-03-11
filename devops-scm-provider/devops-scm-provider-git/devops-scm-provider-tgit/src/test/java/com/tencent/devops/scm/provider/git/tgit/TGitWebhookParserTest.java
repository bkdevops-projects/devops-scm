package com.tencent.devops.scm.provider.git.tgit;

import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_BRANCH;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_PUSH_AFTER_COMMIT;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_PUSH_BEFORE_COMMIT;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_PUSH_PROJECT_ID;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.BK_REPO_GIT_WEBHOOK_PUSH_USERNAME;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_BEFORE_SHA;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_BEFORE_SHA_SHORT;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_COMMIT_MESSAGE;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_EVENT_URL;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_REF;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_REPO_URL;
import static com.tencent.devops.scm.api.constant.WebhookOutputCode.PIPELINE_GIT_SHA_SHORT;

import com.tencent.devops.scm.api.pojo.HookRequest;
import com.tencent.devops.scm.api.pojo.webhook.git.GitPushHook;
import com.tencent.devops.scm.api.pojo.webhook.git.PullRequestHook;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
public class TGitWebhookParserTest {

    private final TGitWebhookParser webhookParser = new TGitWebhookParser();

    @Test
    public void testGitPushHook() throws IOException {
        String filePath =
                TGitWebhookParserTest.class.getClassLoader().getResource("tgit_push_event.json").getFile();
        String payload = FileUtils.readFileToString(new File(filePath), "UTF-8");

        Map<String, String> headers = new HashMap<>();
        headers.put("X-Event", "Push Hook");
        headers.put("X-Event-Type", "git_push");
        headers.put("X-Source-Type", "Project");

        HookRequest request = HookRequest.builder()
                .headers(headers)
                .body(payload)
                .build();

        GitPushHook webhook = (GitPushHook)webhookParser.parse(request);
        assertPushOutputs(webhook);

        Assertions.assertEquals(
                "git@git.code.tencent.com:mingshewhe/webhook_test3.git",
                webhook.repository().getSshUrl()
        );
        Assertions.assertEquals(
                "https://git.code.tencent.com/mingshewhe/webhook_test3",
                webhook.repository().getWebUrl()
        );

        Assertions.assertEquals(
                "b8bb3fccda519efb57d2ffab7e7771983e8ef02f",
                webhook.getCommit().getSha());
        Assertions.assertEquals(
                "--task=75397053 【后台】openapi提供接口给安全侧检测到cp离职后剔除云桌面成员权限。\n",
                webhook.getCommit().getMessage()
        );
        Assertions.assertEquals(
                "https://git.code.tencent.com/"
                        + "mingshewhe/webhook_test3/commit/b8bb3fccda519efb57d2ffab7e7771983e8ef02f",
                webhook.getCommit().getLink()
        );
        Assertions.assertEquals(
                "mingshewhe",
                webhook.getCommit().getAuthor().getName()
        );
        Assertions.assertEquals(
                "wx_a56dc86ef0f74feda385d4818e7c5cda@git.code.tencent.com",
                webhook.getCommit().getAuthor().getEmail()
        );
    }

    private static void assertPushOutputs(GitPushHook webhook) {
        Map<String, Object> outputs = webhook.outputs();
        Assertions.assertEquals("xiaoming", outputs.get(BK_REPO_GIT_WEBHOOK_PUSH_USERNAME));
        Assertions.assertEquals(
                "a7a5b0ca41346d91aece508c8aa205b260ab4cbb",
                outputs.get(BK_REPO_GIT_WEBHOOK_PUSH_BEFORE_COMMIT)
        );
        Assertions.assertEquals(
                "8135b27402a35f301dba18a2833205292dd75264",
                outputs.get(BK_REPO_GIT_WEBHOOK_PUSH_AFTER_COMMIT)
        );
        Assertions.assertEquals("story_75397053", outputs.get(BK_REPO_GIT_WEBHOOK_BRANCH));

        Assertions.assertEquals("196676", outputs.get(BK_REPO_GIT_WEBHOOK_PUSH_PROJECT_ID));
        Assertions.assertEquals(
                "http://git.example.com/bkdevops/bk-ci.git",
                outputs.get(PIPELINE_GIT_REPO_URL)
        );
        Assertions.assertEquals(
                "story_75397053",
                outputs.get(PIPELINE_GIT_REF)
        );
        Assertions.assertEquals(
                "a7a5b0ca41346d91aece508c8aa205b260ab4cbb",
                outputs.get(PIPELINE_GIT_BEFORE_SHA)
        );
        Assertions.assertEquals(
                "a7a5b0ca",
                outputs.get(PIPELINE_GIT_BEFORE_SHA_SHORT)
        );
        Assertions.assertEquals(
                "http://git.example.com/bkdevops/bk-ci/commit/8135b27402a35f301dba18a2833205292dd75264",
                outputs.get(PIPELINE_GIT_EVENT_URL)
        );
        Assertions.assertEquals(
                "8135b274",
                outputs.get(PIPELINE_GIT_SHA_SHORT)
        );
        Assertions.assertEquals(
                "http://git.example.com/bkdevops/bk-ci/commit/8135b27402a35f301dba18a2833205292dd75264",
                outputs.get(PIPELINE_GIT_EVENT_URL)
        );

        Assertions.assertEquals(
                "--task=75397053 【后台】openapi提供接口给安全侧检测到cp离职后剔除云桌面成员权限。\n",
                outputs.get(PIPELINE_GIT_COMMIT_MESSAGE)
        );
    }

    @Test
    public void testGitPullRequestHook() throws IOException {
        String filePath =
                TGitWebhookParserTest.class.getClassLoader().getResource("tgit_mr_event.json").getFile();
        String payload = FileUtils.readFileToString(new File(filePath), "UTF-8");

        Map<String, String> headers = new HashMap<>();
        headers.put("X-Event", "Merge Request Hook");
        headers.put("X-Event-Type", "merge_request");
        headers.put("X-Source-Type", "Project");

        HookRequest request = HookRequest.builder()
                .headers(headers)
                .body(payload)
                .build();

        PullRequestHook webhook = (PullRequestHook) webhookParser.parse(request);
        System.out.println(webhook);
    }
}
