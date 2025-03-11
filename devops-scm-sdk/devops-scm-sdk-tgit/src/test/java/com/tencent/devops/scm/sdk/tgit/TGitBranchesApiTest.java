package com.tencent.devops.scm.sdk.tgit;

import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitBranch;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitCommit;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class TGitBranchesApiTest extends AbstractTGitTest {

    private static final String TEST_BRANCH_NAME = "master";
    private static final String TEST_BRANCH_SEARCH_TERM = "mr_test";
    private static TGitApi tGitApi;

    public TGitBranchesApiTest() {
        super();
    }

    @BeforeAll
    public static void setup() {
        tGitApi = mockTGitApi();
        when(tGitApi.getBranchesApi()).thenReturn(Mockito.mock(TGitBranchesApi.class));
        when(tGitApi.getBranchesApi().getBranch(TEST_PROJECT_NAME, TEST_BRANCH_NAME))
                .thenReturn(read("get_branch.json", TGitBranch.class));
        when(tGitApi.getBranchesApi().getBranches(TEST_PROJECT_NAME, TEST_BRANCH_SEARCH_TERM))
                .thenReturn(read("search_branches.json", new TypeReference<List<TGitBranch>>() {
                }));
    }

    @Test
    public void testGetBranch() {
        TGitBranch branch = tGitApi.getBranchesApi().getBranch(TEST_PROJECT_NAME, TEST_BRANCH_NAME);
        Assertions.assertEquals(TEST_BRANCH_NAME, branch.getName());
        Assertions.assertTrue(branch.getProtected());
        Assertions.assertTrue(branch.getDevelopersCanPush());
        Assertions.assertFalse(branch.getDevelopersCanMerge());

        TGitCommit commit = branch.getCommit();
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
    public void testSearchBranch() {
        List<TGitBranch> branches = tGitApi.getBranchesApi().getBranches(TEST_PROJECT_NAME, TEST_BRANCH_SEARCH_TERM);
        Assertions.assertEquals(1, branches.size());

        TGitBranch branch = branches.get(0);
        Assertions.assertEquals(TEST_BRANCH_SEARCH_TERM, branch.getName());
        Assertions.assertFalse(branch.getProtected());
        Assertions.assertFalse(branch.getDevelopersCanPush());
        Assertions.assertFalse(branch.getDevelopersCanMerge());

        TGitCommit commit = branch.getCommit();
        Assertions.assertNotNull(commit);
        Assertions.assertEquals("9c9f8cc062060fdad67137e5e102689be765b4d4", commit.getId());
        Assertions.assertEquals("9c9f8cc0", commit.getShortId());
        Assertions.assertEquals("mr 19", commit.getMessage());
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
}
