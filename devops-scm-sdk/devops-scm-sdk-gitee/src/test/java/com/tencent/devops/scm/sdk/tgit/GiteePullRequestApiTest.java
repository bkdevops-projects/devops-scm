package com.tencent.devops.scm.sdk.tgit;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.tencent.devops.scm.sdk.common.util.ScmJsonUtil;
import com.tencent.devops.scm.sdk.gitee.GiteeApi;
import com.tencent.devops.scm.sdk.gitee.GiteeBranchesApi;
import com.tencent.devops.scm.sdk.gitee.GiteePullRequestApi;
import com.tencent.devops.scm.sdk.gitee.pojo.GiteePullRequest;
import com.tencent.devops.scm.sdk.gitee.pojo.GiteePullRequestDiff;
import com.tencent.devops.scm.sdk.gitee.pojo.GiteePullRequestRef;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class GiteePullRequestApiTest extends AbstractGiteeTest {

    private static GiteeApi giteeApi;

    private static final Long PULL_REQUEST_NUMBER = 2L;

    public GiteePullRequestApiTest() {
        super();
    }

    @BeforeAll
    public static void setup() {
        giteeApi = createGiteeApi();
        mockData();
    }

    public static void mockData() {
        giteeApi = Mockito.mock(GiteeApi.class);
        Mockito.when(giteeApi.getPullRequestApi()).thenReturn(Mockito.mock(GiteePullRequestApi.class));
        Mockito.when(giteeApi.getPullRequestApi().getPullRequest(TEST_PROJECT_NAME, PULL_REQUEST_NUMBER))
                .thenReturn(
                        read("get_pull_request.json", new TypeReference<>() {})
                );
        Mockito.when(giteeApi.getPullRequestApi().listChanges(TEST_PROJECT_NAME, PULL_REQUEST_NUMBER))
                .thenReturn(
                        read("get_pull_request_changes.json", new TypeReference<>() {})
                );
    }

    @Test
    public void testGetPullRequest() throws JsonProcessingException {
        GiteePullRequest pullRequest = giteeApi.getPullRequestApi().getPullRequest(
                TEST_PROJECT_NAME,
                PULL_REQUEST_NUMBER
        );
        GiteePullRequestRef base = pullRequest.getBase();
        GiteePullRequestRef head = pullRequest.getHead();
        Assertions.assertEquals("feat_0", head.getRef());
        Assertions.assertEquals("master", base.getRef());
        String targetRepoRepo = "repo_group/repo_name";
        Assertions.assertEquals(targetRepoRepo, base.getRepo().getFullName());
    }

    @Test
    public void testGetPullRequestDiff() throws JsonProcessingException {
        List<GiteePullRequestDiff> pullRequestDiff = Arrays.asList(giteeApi.getPullRequestApi().listChanges(
                TEST_PROJECT_NAME,
                PULL_REQUEST_NUMBER
        ));
        List<String> files = pullRequestDiff.stream()
                .map(GiteePullRequestDiff::getFilename)
                .collect(Collectors.toList());
        List<String> baseFiles = new ArrayList<>();
        baseFiles.add("files/a/a_1/a_1.txt");
        baseFiles.add("files/a/a_4/a_4.txt");
        baseFiles.forEach(fileName -> Assertions.assertTrue(files.contains(fileName)));
    }
}
