package com.tencent.devops.scm.sdk.tgit;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.tencent.devops.scm.sdk.common.util.ScmJsonUtil;
import com.tencent.devops.scm.sdk.gitee.GiteeApi;
import com.tencent.devops.scm.sdk.gitee.GiteeProjectApi;
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeBaseRepository;
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeProjectHook;
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeWebhookConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class GiteeProjectApiTest extends AbstractGiteeTest {

    private static GiteeApi giteeApi;

    private static final GiteeProjectHook HOOK_CONFIG = GiteeProjectHook.builder().pushEvents(true)
                        .issuesEvents(true)
                        .mergeRequestsEvents(true)
                        .noteEvents(true)
                        .tagPushEvents(true)
                        .url("https://www.baidu.com")
                        .build();

    private static final String SECRET_TOKEN = "zhangsan";

    private static final Long HOOK_ID = 12345L;

    public GiteeProjectApiTest() {
        super();
    }

    @BeforeAll
    public static void setup() {
        giteeApi = createGiteeApi();
        // 模拟数据，真实测试时需要注释掉
        mockData();
    }

    public static void mockData() {
        giteeApi = Mockito.mock(GiteeApi.class);
        Mockito.when(giteeApi.getProjectApi()).thenReturn(Mockito.mock(GiteeProjectApi.class));
        Mockito.when(giteeApi.getProjectApi().getProject(TEST_PROJECT_NAME))
                .thenReturn(
                        read("get_project.json", new TypeReference<>() {})
                );
        Mockito.when(giteeApi.getProjectApi().addHook(TEST_PROJECT_NAME, HOOK_CONFIG, SECRET_TOKEN))
                .thenReturn(
                        read("add_hook.json", new TypeReference<>() {
                        })
                );
        Mockito.when(giteeApi.getProjectApi().getHook(TEST_PROJECT_NAME, HOOK_ID))
                .thenReturn(
                        read("get_hook.json", new TypeReference<>() {
                        })
                );
    }

    @Test
    public void getProject() throws JsonProcessingException {
        GiteeBaseRepository repository = giteeApi.getProjectApi().getProject(
                TEST_PROJECT_NAME
        );
        System.out.println(ScmJsonUtil.getJsonFactory().toJson(repository));
    }

    @Test
    public void addHook() throws JsonProcessingException {
        GiteeWebhookConfig hook = giteeApi.getProjectApi().addHook(
                TEST_PROJECT_NAME,
                HOOK_CONFIG,
                SECRET_TOKEN
        );
        Assertions.assertEquals(true, hook.getMergeRequestsEvents());
        Assertions.assertEquals(HOOK_CONFIG.getUrl(), hook.getUrl());
        Assertions.assertEquals(SECRET_TOKEN, hook.getPassword());
    }

    @Test
    public void getHook() throws JsonProcessingException {
        GiteeWebhookConfig hook = giteeApi.getProjectApi().getHook(
                TEST_PROJECT_NAME,
                HOOK_ID
        );
        Assertions.assertEquals(SECRET_TOKEN, hook.getPassword());
    }
}
