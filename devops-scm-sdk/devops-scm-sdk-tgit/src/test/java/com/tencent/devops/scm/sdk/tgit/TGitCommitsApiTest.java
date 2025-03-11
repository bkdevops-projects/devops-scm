package com.tencent.devops.scm.sdk.tgit;

import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitCommit;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitDiff;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class TGitCommitsApiTest extends AbstractTGitTest {
    private static final String TEST_COMMIT_SHA = "b5f141f9c1b3f87d9b070157097130be7fb7563a";
    private static TGitApi tGitApi;

    public TGitCommitsApiTest() {
        super();
    }

    @BeforeAll
    public static void testSetup() {

        tGitApi = mockTGitApi();
        when(tGitApi.getCommitsApi()).thenReturn(Mockito.mock(TGitCommitsApi.class));
        when(tGitApi.getCommitsApi().getCommit(TEST_PROJECT_NAME, TEST_COMMIT_SHA))
                .thenReturn(read("get_commit.json", TGitCommit.class));
        when(tGitApi.getCommitsApi().getDiff(TEST_PROJECT_NAME, TEST_COMMIT_SHA))
                .thenReturn(read("get_commit_diff.json", new TypeReference<List<TGitDiff>>() {
                }));
    }

    @Test
    public void testGetCommit() {
        TGitCommit commit = tGitApi.getCommitsApi().getCommit(TEST_PROJECT_NAME, TEST_COMMIT_SHA);
        Assertions.assertNotNull(commit);
        Assertions.assertEquals("b5f141f9c1b3f87d9b070157097130be7fb7563a", commit.getId());
        Assertions.assertEquals("b5f141f9", commit.getShortId());
        Assertions.assertEquals("webhook 10", commit.getMessage());
        Assertions.assertEquals("mingshewhe", commit.getCommitterName());
        Assertions.assertEquals(
                "wx_a56dc86ef0f74feda385d4818e7c5cda@git.code.tencent.com",
                commit.getCommitterEmail()
        );
        Assertions.assertEquals("mingshewhe", commit.getAuthorName());
        Assertions.assertEquals(
                "wx_a56dc86ef0f74feda385d4818e7c5cda@git.code.tencent.com",
                commit.getAuthorEmail()
        );
    }

    @Test
    public void testGetDiff() {
        List<TGitDiff> diffs = tGitApi.getCommitsApi().getDiff(TEST_PROJECT_NAME, TEST_COMMIT_SHA);
        Assertions.assertEquals(2, diffs.size());

        TGitDiff diff1 = diffs.get(0);
        Assertions.assertEquals("webhook.txt", diff1.getOldPath());
        Assertions.assertEquals("webhook.txt", diff1.getNewPath());
        Assertions.assertEquals("33188", diff1.getAMode());
        Assertions.assertEquals("33188", diff1.getBMode());
        Assertions.assertFalse(diff1.getNewFile());
        Assertions.assertFalse(diff1.getDeletedFile());
        Assertions.assertFalse(diff1.getRenamedFile());
        Assertions.assertFalse(diff1.getTooLarge());
        Assertions.assertFalse(diff1.getCollapse());
        Assertions.assertEquals(2, diff1.getAdditions());
        Assertions.assertEquals(1, diff1.getDeletions());

        TGitDiff diff2 = diffs.get(1);
        Assertions.assertEquals("/dev/null", diff2.getOldPath());
        Assertions.assertEquals("README.md", diff2.getNewPath());
        Assertions.assertEquals("0", diff2.getAMode());
        Assertions.assertEquals("33188", diff2.getBMode());
        Assertions.assertTrue(diff2.getNewFile());
        Assertions.assertFalse(diff2.getDeletedFile());
        Assertions.assertFalse(diff2.getRenamedFile());
        Assertions.assertFalse(diff2.getTooLarge());
        Assertions.assertFalse(diff2.getCollapse());
        Assertions.assertEquals(1, diff2.getAdditions());
        Assertions.assertEquals(0, diff2.getDeletions());
    }
}
