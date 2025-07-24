package com.tencent.devops.scm.sdk.tsvn;


import static org.mockito.ArgumentMatchers.any;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tencent.devops.scm.sdk.tsvn.pojo.TSvnWebHookConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class TSvnWebhookApiTest extends AbstractTSvnTest {

    private static TSvnApi tSvnApi;

    public TSvnWebhookApiTest() {
        super();
    }

    @BeforeAll
    public static void setup() {
        tSvnApi = createTSvnApi();
        mockData();
    }

    public static void mockData() {
        tSvnApi = Mockito.mock(TSvnApi.class);
        Mockito.when(tSvnApi.getWebhookApi()).thenReturn(Mockito.mock(TSvnWebhookApi.class));
        Mockito.when(tSvnApi.getWebhookApi().addHook(any(), any()))
                .thenReturn(
                        read("add_hook.json", new TypeReference<TSvnWebHookConfig>() {})
                );
    }

    @Test
    public void addHook() {
        String hookUrl = "http://template.hookServer/external/scm/codesvn/commit";
        TSvnWebHookConfig tSvnWebHookConfig = tSvnApi.getWebhookApi().addHook(
                TEST_PROJECT_NAME,
                TSvnWebHookConfig.builder()
                        .url(hookUrl)
                        .svnPostCommitEvents(true)
                        .path("/")
                        .build()
        );
        Assertions.assertEquals(hookUrl, tSvnWebHookConfig.getUrl());
    }
}
