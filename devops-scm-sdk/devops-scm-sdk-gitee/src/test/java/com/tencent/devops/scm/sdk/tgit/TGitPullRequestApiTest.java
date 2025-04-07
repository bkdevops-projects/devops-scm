package com.tencent.devops.scm.sdk.tgit;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.tencent.devops.scm.sdk.common.util.ScmJsonUtil;
import com.tencent.devops.scm.sdk.gitee.GiteeApi;
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeBranch;
import com.tencent.devops.scm.sdk.gitee.pojo.GiteePullRequest;
import com.tencent.devops.scm.sdk.gitee.pojo.GiteePullRequestDiff;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class TGitPullRequestApiTest extends AbstractGiteeTest {
    private static GiteeApi giteeApi;

    public TGitPullRequestApiTest() {
        super();
    }

    @BeforeAll
    public static void setup() { giteeApi = createTGitApi(); }

    @Test
    public void testGetPullRequest() throws JsonProcessingException {
        GiteePullRequest pullRequest = giteeApi.getPullRequestApi().getPullRequest(TEST_PROJECT_NAME, 2L);
        System.out.println(ScmJsonUtil.getJsonFactory().toJson(pullRequest));
    }

    @Test
    public void testGetPullRequestDiff() throws JsonProcessingException {
        GiteePullRequestDiff[] pullRequestDiff = giteeApi.getPullRequestApi().listChanges(TEST_PROJECT_NAME, 2L);
        System.out.println(ScmJsonUtil.getJsonFactory().toJson(pullRequestDiff));
    }
}
