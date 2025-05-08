package com.tencent.devops.scm.sdk.tgit;

import static org.mockito.ArgumentMatchers.anyString;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tencent.devops.scm.sdk.gitee.GiteeApi;
import com.tencent.devops.scm.sdk.gitee.GiteeBranchesApi;
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeBranch;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class GiteeBranchesApiTest extends AbstractGiteeTest {

    private static GiteeApi giteeApi;

    public GiteeBranchesApiTest() {
        super();
    }

    @BeforeAll
    public static void setup() {
        giteeApi = createGiteeApi();
        mockData();
    }

    public static void mockData() {
        giteeApi = Mockito.mock(GiteeApi.class);
        Mockito.when(giteeApi.getBranchesApi()).thenReturn(Mockito.mock(GiteeBranchesApi.class));
        Mockito.when(giteeApi.getBranchesApi().getBranches(TEST_PROJECT_NAME))
                .thenReturn(
                        read("get_branch.json", new TypeReference<>() {
                        })
                );
        Mockito.when(giteeApi.getBranchesApi().getBranch(anyString(), anyString()))
                .thenReturn(
                        read("get_branch_detail.json", new TypeReference<>() {
                        })
                );
    }

    @Test
    public void testGetBranches() {
        List<GiteeBranch> branches = giteeApi.getBranchesApi().getBranches(TEST_PROJECT_NAME);
        GiteeBranch giteeBranch = branches.get(0);
        String branchName = "dependabot/go_modules/src/agent/agent/github.com/containerd/containerd-1.6.38";
        Assertions.assertEquals(branchName, giteeBranch.getName());
    }

    @Test
    public void testGetBranch() {
        GiteeBranch branch = giteeApi.getBranchesApi().getBranch(TEST_PROJECT_NAME, "master");
        System.out.println("branch = " + branch);
    }
}
