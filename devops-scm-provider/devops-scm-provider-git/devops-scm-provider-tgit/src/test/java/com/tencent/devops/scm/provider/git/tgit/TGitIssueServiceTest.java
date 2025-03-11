package com.tencent.devops.scm.provider.git.tgit;

import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tencent.devops.scm.api.enums.IssueState;
import com.tencent.devops.scm.api.pojo.Comment;
import com.tencent.devops.scm.api.pojo.Issue;
import com.tencent.devops.scm.api.pojo.IssueListOptions;
import com.tencent.devops.scm.api.pojo.ListOptions;
import com.tencent.devops.scm.api.pojo.User;
import com.tencent.devops.scm.sdk.tgit.TGitIssuesApi;
import com.tencent.devops.scm.sdk.tgit.TGitNotesApi;
import com.tencent.devops.scm.sdk.tgit.enums.TGitIssueState;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitIssue;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitNote;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class TGitIssueServiceTest extends AbstractTGitServiceTest {

    private static final Long TEST_ISSUE_ID = 58870L;
    private static final Integer TEST_ISSUE_NUMBER = 1;
    private static final Long TEST_ISSUE_NOTE_ID = 5257868L;

    private final TGitIssueService issueService;

    public TGitIssueServiceTest() {
        super();
        when(tGitApi.getIssuesApi()).thenReturn(Mockito.mock(TGitIssuesApi.class));
        TGitIssuesApi issuesApi = tGitApi.getIssuesApi();
        when(tGitApi.getNotesApi()).thenReturn(Mockito.mock(TGitNotesApi.class));
        TGitNotesApi notesApi = tGitApi.getNotesApi();

        when(issuesApi.getIssue(TEST_PROJECT_NAME, TEST_ISSUE_ID))
                .thenReturn(read("get_issue.json", TGitIssue.class));
        when(issuesApi.getIssueByIid(TEST_PROJECT_NAME, TEST_ISSUE_NUMBER))
                .thenReturn(read("get_issue.json", TGitIssue.class));
        when(issuesApi.getIssues(TEST_PROJECT_NAME, TGitIssueState.OPENED, null, null))
                .thenReturn(read("list_issues.json", new TypeReference<List<TGitIssue>>() {
                }));
        when(notesApi.getIssueNote(TEST_PROJECT_NAME, TEST_ISSUE_ID, TEST_ISSUE_NOTE_ID)).thenReturn(
                read("get_issue_note.json", TGitNote.class));
        when(notesApi.getIssueNotes(TEST_PROJECT_NAME, TEST_ISSUE_ID, null, null)).thenReturn(
                read("list_issue_notes.json", new TypeReference<List<TGitNote>>() {
                }));

        issueService = new TGitIssueService(apiFactory);
    }

    @Test
    public void testFind() {
        Issue issue = issueService.find(providerRepository, TEST_ISSUE_NUMBER);
        assertIssue(issue);
    }

    @Test
    public void testList() {
        IssueListOptions opts = IssueListOptions.builder()
                .state(IssueState.OPENED)
                .build();
        List<Issue> issues = issueService.list(providerRepository, opts);
        Assertions.assertEquals(1, issues.size());
        assertIssue(issues.get(0));
    }

    @Test
    public void testFindComment() {
        Comment comment = issueService.findComment(providerRepository, TEST_ISSUE_NUMBER,
                TEST_ISSUE_NOTE_ID);

        Assertions.assertEquals(5257868L, comment.getId());
        Assertions.assertEquals("issue note api", comment.getBody());

        User user = comment.getAuthor();
        Assertions.assertEquals(132102L, user.getId());
        Assertions.assertEquals("mingshewhe", user.getUsername());
    }

    @Test
    public void listComments() {
        ListOptions opts = ListOptions.builder().build();
        List<Comment> comments = issueService.listComments(providerRepository,
                TEST_ISSUE_NUMBER, opts);
        Assertions.assertEquals(12, comments.size());

        Comment lastComment = comments.get(11);
        Assertions.assertEquals(5257868L, lastComment.getId());
        Assertions.assertEquals("issue note api", lastComment.getBody());
    }

    private static void assertIssue(Issue issue) {
        Assertions.assertEquals(58870L, issue.getId());
        Assertions.assertEquals(1, issue.getNumber());
        Assertions.assertEquals("issue api测试", issue.getTitle());
        Assertions.assertEquals("", issue.getBody());
        Assertions.assertFalse(issue.getClosed());

        Assertions.assertEquals(1, issue.getLabels().size());

        User user = issue.getAuthor();
        Assertions.assertEquals(132102L, user.getId());
        Assertions.assertEquals("mingshewhe", user.getUsername());
    }
}
