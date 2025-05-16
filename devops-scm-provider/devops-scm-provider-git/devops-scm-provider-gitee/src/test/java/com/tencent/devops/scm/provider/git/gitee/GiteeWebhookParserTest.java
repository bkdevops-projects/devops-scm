package com.tencent.devops.scm.provider.git.gitee;

import com.tencent.devops.scm.api.enums.EventAction;
import com.tencent.devops.scm.api.pojo.HookRequest;
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
public class GiteeWebhookParserTest extends AbstractGiteeServiceTest {

    private final GiteeWebhookParser webhookParser = new GiteeWebhookParser();

    private final GiteeWebhookEnricher webhookEnricher = new GiteeWebhookEnricher(giteeApiFactory);

    @Test
    public void testGitPullRequestHook() throws IOException {
        String filePath = GiteeWebhookParserTest.class.getClassLoader()
                .getResource("pull_request_webhook.json")
                .getFile();
        String payload = FileUtils.readFileToString(new File(filePath), "UTF-8");

        Map<String, String> headers = new HashMap<>();
        headers.put("X-Gitee-Event", "Merge Request Hook");

        HookRequest request = HookRequest.builder()
                .headers(headers)
                .body(payload)
                .build();

        PullRequestHook webhook = (PullRequestHook) webhookParser.parse(request);
        Assertions.assertEquals(1, webhook.getPullRequest().getNumber());
        Assertions.assertEquals(EventAction.OPEN, webhook.getAction());
    }
}
