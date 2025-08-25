package com.tencent.devops.scm.sdk.tgit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tencent.devops.scm.sdk.gitee.GiteeApi;
import com.tencent.devops.scm.sdk.gitee.GiteeCheckRunApi;
import com.tencent.devops.scm.sdk.gitee.GiteeTagApi;
import com.tencent.devops.scm.sdk.gitee.enums.GiteeCheckRunConclusion;
import com.tencent.devops.scm.sdk.gitee.enums.GiteeCheckRunStatus;
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeCheckRun;
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeCheckRunOutput;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class GiteeCheckRunApiTest extends AbstractGiteeTest {

    private static GiteeApi giteeApi;

    private GiteeCheckRun checkRun = GiteeCheckRun.builder()
            .pullRequestId(14861176L)
            .name("devops-scm-gitee-check#name#1")
            .status(GiteeCheckRunStatus.IN_PROGRESS)
            .conclusion(GiteeCheckRunConclusion.NEUTRAL)
            .headSha("ddeaf249973c6ea9e02747f642680d05812fd80d")
            .detailsUrl("https://gitee.com/hejieehe")
            .startedAt(new Date())
            .output(
                    GiteeCheckRunOutput.builder()
                            .title("devops-scm-gitee-check#output#title")
                            .text("devops-scm-gitee-check#output#text")
                            .summary("devops-scm-gitee-check#output#summary")
                            .build()
            )
            .build();


    public GiteeCheckRunApiTest() {
        super();
    }

    @BeforeAll
    public static void setup() throws IOException {
        giteeApi = createGiteeApi();
        mockData();
    }

    public static void mockData() throws IOException {
        giteeApi = Mockito.mock(GiteeApi.class);
        Mockito.when(giteeApi.getCheckRunApi()).thenReturn(Mockito.mock(GiteeCheckRunApi.class));
        Mockito.when(giteeApi.getCheckRunApi().create(anyString(), any(GiteeCheckRun.class)))
                .thenReturn(read("create_check_run.json", new TypeReference<>() {}));
        Mockito.when(giteeApi.getCheckRunApi().update(anyString(),anyLong(), any(GiteeCheckRun.class)))
                .thenReturn(read("create_check_run.json", new TypeReference<>() {}));
        Mockito.when(giteeApi.getCheckRunApi().getCheckRuns(anyString(), anyString(), anyLong(), any(), any()))
                .thenReturn(read("get_check_run_list.json", new TypeReference<List<GiteeCheckRun>>() {}));
    }

    @Test
    public void create() throws IOException {
        GiteeCheckRun giteeCheckRun = giteeApi.getCheckRunApi().create(TEST_PROJECT_NAME, checkRun);
        System.out.println("giteeCheckRun = " + giteeCheckRun);

        checkRun.setName("devops-scm-gitee-check#name#2");
        GiteeCheckRun giteeCheckRun2 = giteeApi.getCheckRunApi().create(TEST_PROJECT_NAME, checkRun);
        System.out.println("giteeCheckRun = " + giteeCheckRun2);
    }

    @Test
    public void update() throws IOException {
        checkRun.setStatus(GiteeCheckRunStatus.COMPLETED);
        checkRun.setConclusion(GiteeCheckRunConclusion.FAILURE);
        checkRun.getOutput().setSummary("检查完毕，输出结果");
        checkRun.setCompletedAt(new Date());
        GiteeCheckRun update = giteeApi.getCheckRunApi().update(TEST_PROJECT_NAME, 23032201L, checkRun);
        System.out.println("update = " + update);
    }

    @Test
    public void getCheckRuns() {
        List<GiteeCheckRun> checkRuns = giteeApi.getCheckRunApi()
                .getCheckRuns(
                        TEST_PROJECT_NAME,
                        "feat_0",
                        checkRun.getPullRequestId(),
                        1,
                        100
                );
        checkRuns.forEach(System.out::println);
    }
}
