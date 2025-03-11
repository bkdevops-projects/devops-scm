package com.tencent.devops.scm.sdk.tgit;

import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tencent.devops.scm.sdk.tgit.enums.TGitIssueResolveState;
import com.tencent.devops.scm.sdk.tgit.enums.TGitIssueState;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitAuthor;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitIssue;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class TGitIssueApiTest extends AbstractTGitTest {
    private static final Long TEST_ISSUE_ID = 58870L;

    private static TGitApi tGitApi;
    private static TGitIssuesApi issuesApi;

    public TGitIssueApiTest() {
        super();
    }

    @BeforeAll
    public static void testSetup() {
        tGitApi = mockTGitApi();
        when(tGitApi.getIssuesApi()).thenReturn(Mockito.mock(TGitIssuesApi.class));
        issuesApi = tGitApi.getIssuesApi();
        when(issuesApi.getIssue(TEST_PROJECT_NAME, TEST_ISSUE_ID))
                .thenReturn(read("get_issue.json", TGitIssue.class));
        when(issuesApi.getIssues(TEST_PROJECT_NAME, TGitIssueState.OPENED))
                .thenReturn(read("list_issues.json", new TypeReference<List<TGitIssue>>() {
                }));
    }

    @Test
    public void testGetIssue() {
        TGitIssue issue = issuesApi.getIssue(TEST_PROJECT_NAME, TEST_ISSUE_ID);
        assertIssue(issue);
    }

    @Test
    public void testGetIssues() {
        List<TGitIssue> issues = issuesApi.getIssues(TEST_PROJECT_NAME, TGitIssueState.OPENED);

        Assertions.assertEquals(1, issues.size());
        assertIssue(issues.get(0));
    }

    private static void assertIssue(TGitIssue issue) {
        Assertions.assertEquals(58870L, issue.getId());
        Assertions.assertEquals(1, issue.getIid());
        Assertions.assertEquals("issue api测试", issue.getTitle());
        Assertions.assertEquals(TGitIssueState.OPENED, issue.getState());
        Assertions.assertEquals(TGitIssueResolveState.RESOLVED, issue.getResolveState());

        TGitAuthor author = issue.getAuthor();
        Assertions.assertEquals(132102L, author.getId());
        Assertions.assertEquals("mingshewhe", author.getUsername());
    }
}
