package com.tencent.devops.scm.sdk.tgit;

import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitNote;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class TGitNotesApiTest extends AbstractTGitTest {

    private static final Long TEST_ISSUE_ID = 58870L;
    private static final Long TEST_ISSUE_NOTE_ID = 5257868L;
    private static final Long TEST_MERGE_REQUEST_ID = 290966L;
    private static final Long TEST_MERGE_REQUEST_NOTE_ID = 5258375L;

    private static TGitApi tGitApi;
    private static TGitNotesApi notesApi;

    public TGitNotesApiTest() {
        super();
    }

    @BeforeAll
    public static void testSetup() {
        tGitApi = mockTGitApi();
        when(tGitApi.getNotesApi()).thenReturn(Mockito.mock(TGitNotesApi.class));
        notesApi = tGitApi.getNotesApi();
        when(notesApi.getIssueNote(TEST_PROJECT_NAME, TEST_ISSUE_ID, TEST_ISSUE_NOTE_ID)).thenReturn(
                read("get_issue_note.json", TGitNote.class));
        when(notesApi.getIssueNotes(TEST_PROJECT_NAME, TEST_ISSUE_ID)).thenReturn(
                read("list_issue_notes.json", new TypeReference<List<TGitNote>>() {
                }));
        when(notesApi.getMergeRequestNote(TEST_PROJECT_NAME, TEST_MERGE_REQUEST_ID, TEST_MERGE_REQUEST_NOTE_ID))
                .thenReturn(read("get_merge_request_note.json", TGitNote.class));
        when(notesApi.getMergeRequestNotes(TEST_PROJECT_NAME, TEST_MERGE_REQUEST_ID)).thenReturn(
                read("list_merge_request_notes.json", new TypeReference<List<TGitNote>>() {
                }));
    }

    @Test
    public void testGetIssueNotes() {
        List<TGitNote> issueNotes = notesApi.getIssueNotes(TEST_PROJECT_NAME, TEST_ISSUE_ID);
        Assertions.assertEquals(12, issueNotes.size());

        TGitNote lastNode = issueNotes.get(11);
        Assertions.assertEquals(5257868L, lastNode.getId());
        Assertions.assertEquals("issue note api", lastNode.getBody());
    }

    @Test
    public void testGetIssueNote() {
        TGitNote issueNote = notesApi.getIssueNote(TEST_PROJECT_NAME, TEST_ISSUE_ID, TEST_ISSUE_NOTE_ID);
        Assertions.assertEquals(5257868L, issueNote.getId());
        Assertions.assertEquals("issue note api", issueNote.getBody());
    }

    @Test
    public void testGetMergeRequestNotes() {
        List<TGitNote> mergeRequestNotes = notesApi.getMergeRequestNotes(TEST_PROJECT_NAME, TEST_MERGE_REQUEST_ID);

        Assertions.assertEquals(5, mergeRequestNotes.size());

        TGitNote lastNode = mergeRequestNotes.get(4);
        Assertions.assertEquals(5258375L, lastNode.getId());
        Assertions.assertEquals("mr note api create", lastNode.getBody());
    }

    @Test
    public void testGetMergeRequestNote() {
        TGitNote mergeRequestNote = notesApi.getMergeRequestNote(TEST_PROJECT_NAME, TEST_MERGE_REQUEST_ID,
                TEST_MERGE_REQUEST_NOTE_ID);

        Assertions.assertEquals(5258375L, mergeRequestNote.getId());
        Assertions.assertEquals("mr note api create", mergeRequestNote.getBody());
    }
}
