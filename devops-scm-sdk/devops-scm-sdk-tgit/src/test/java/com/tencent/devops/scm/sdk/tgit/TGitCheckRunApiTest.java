package com.tencent.devops.scm.sdk.tgit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.tencent.devops.scm.sdk.tgit.enums.TGitCheckRunState;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitCheckRun;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class TGitCheckRunApiTest extends AbstractTGitTest {

    private static TGitApi tGitApi;

    private TGitCheckRun checkRun = TGitCheckRun.builder()
            .block(true)
            .context("devops-scm-sdk#test")
            .state(TGitCheckRunState.ERROR.toValue())
            .targetUrl("https://github.com/hejieehe?tab=repositories")
            .description("devops-scm-sdk#desc")
            .detail("devops-scm-sdk#detail")
            .build();

    private static String sha = "534132744e6c9b2a0e7fe32814a7e05f3f827a9e";

    public TGitCheckRunApiTest() {
        super();
    }

    @BeforeAll
    public static void setup() {
        // tGitApi = createTGitApi();
        mockData();
    }

    public static void mockData() {
        tGitApi = mockTGitApi();
        when(tGitApi.getCheckRunApi()).thenReturn(Mockito.mock(TGitCheckRunApi.class));
        when(tGitApi.getCheckRunApi().create(anyString(), anyString(), any(TGitCheckRun.class)))
                .thenReturn(read("create_check_run.json", TGitCheckRun.class));
    }

    @Test
    public void create() {
        TGitCheckRun tGitCheckRun = tGitApi.getCheckRunApi().create(
                TEST_PROJECT_NAME,
                sha,
                checkRun
        );
        System.out.println("tGitCheckRun = " + tGitCheckRun);
    }

    @Test
    public void update() {
        checkRun.setState(TGitCheckRunState.SUCCESS.toValue());
        checkRun.setBlock(false);
        checkRun.setDetail("devops-scm-sdk#success_detail");
        TGitCheckRun tGitCheckRun = tGitApi.getCheckRunApi().create(
                TEST_PROJECT_NAME,
                sha,
                checkRun
        );
        System.out.println("tGitCheckRun = " + tGitCheckRun);
    }


    @Test
    public void getCheckRuns() {
        List<TGitCheckRun> checkRuns = tGitApi.getCheckRunApi().getCheckRuns(
                TEST_PROJECT_NAME,
                sha,
                1,
                100
        );
        checkRuns.forEach(System.out::println);
    }
}
