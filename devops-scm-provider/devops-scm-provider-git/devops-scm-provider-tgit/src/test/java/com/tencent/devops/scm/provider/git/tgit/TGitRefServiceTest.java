package com.tencent.devops.scm.provider.git.tgit;

import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tencent.devops.scm.api.pojo.BranchListOptions;
import com.tencent.devops.scm.api.pojo.Commit;
import com.tencent.devops.scm.api.pojo.Reference;
import com.tencent.devops.scm.api.pojo.TagListOptions;
import com.tencent.devops.scm.sdk.tgit.TGitBranchesApi;
import com.tencent.devops.scm.sdk.tgit.TGitCommitsApi;
import com.tencent.devops.scm.sdk.tgit.TGitTagsApi;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitBranch;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitCommit;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitDiff;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitTag;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class TGitRefServiceTest extends AbstractTGitServiceTest {
    private static final String TEST_BRANCH_NAME = "master";
    private static final String TEST_BRANCH_SEARCH_TERM = "mr_test";
    private static final String TEST_TAG_NAME_0 = "v1.0.1";
    private static final String TEST_COMMIT_SHA = "b5f141f9c1b3f87d9b070157097130be7fb7563a";


    private final TGitRefService refService;

    public TGitRefServiceTest() {
        super();
        when(tGitApi.getBranchesApi()).thenReturn(Mockito.mock(TGitBranchesApi.class));
        when(tGitApi.getTagsApi()).thenReturn(Mockito.mock(TGitTagsApi.class));
        when(tGitApi.getCommitsApi()).thenReturn(Mockito.mock(TGitCommitsApi.class));

        TGitBranchesApi branchesApi = tGitApi.getBranchesApi();
        when(branchesApi.getBranch(TEST_PROJECT_NAME, TEST_BRANCH_NAME))
                .thenReturn(read("get_branch.json", TGitBranch.class));
        when(branchesApi.getBranches(TEST_PROJECT_NAME, TEST_BRANCH_SEARCH_TERM, null, null))
                .thenReturn(read("search_branches.json", new TypeReference<List<TGitBranch>>() {
                }));

        TGitTagsApi tagsApi = tGitApi.getTagsApi();
        when(tagsApi.getTag(TEST_PROJECT_NAME, TEST_TAG_NAME_0))
                .thenReturn(read("get_tag.json", TGitTag.class));
        when(tagsApi.getTags(TEST_PROJECT_NAME, null, null, null))
                .thenReturn(read("list_tags.json", new TypeReference<List<TGitTag>>() {
                }));

        TGitCommitsApi commitsApi = tGitApi.getCommitsApi();
        when(commitsApi.getCommit(TEST_PROJECT_NAME, TEST_COMMIT_SHA))
                .thenReturn(read("get_commit.json", TGitCommit.class));
        when(commitsApi.getDiff(TEST_PROJECT_NAME, TEST_COMMIT_SHA))
                .thenReturn(read("get_commit_diff.json", new TypeReference<List<TGitDiff>>() {
                }));

        refService = new TGitRefService(apiFactory);
    }

    @Test
    public void testFindBranch() {
        Reference reference = refService.findBranch(providerRepository, TEST_BRANCH_NAME);
        Assertions.assertEquals(TEST_BRANCH_NAME, reference.getName());
        Assertions.assertEquals("b5f141f9c1b3f87d9b070157097130be7fb7563a", reference.getSha());
    }

    @Test
    public void testListBranches() {
        BranchListOptions opts = BranchListOptions.builder()
                .search(TEST_BRANCH_SEARCH_TERM)
                .build();
        List<Reference> references = refService.listBranches(providerRepository, opts);

        Assertions.assertEquals(1, references.size());

        Reference reference = references.get(0);
        Assertions.assertEquals(TEST_BRANCH_SEARCH_TERM, reference.getName());
        Assertions.assertEquals("9c9f8cc062060fdad67137e5e102689be765b4d4", reference.getSha());
    }

    @Test
    public void testFindTag() {
        Reference reference = refService.findTag(providerRepository, TEST_TAG_NAME_0);
        Assertions.assertEquals(TEST_TAG_NAME_0, reference.getName());
        Assertions.assertEquals("87acd380f4a91ba1eb200a082ad60f394f3062a5", reference.getSha());
    }

    @Test
    public void testListTags() {

        TagListOptions opts = TagListOptions.builder()
                .build();
        List<Reference> references = refService.listTags(providerRepository, opts);
        Assertions.assertFalse(references.isEmpty());

        Reference reference1 = references.get(0);
        Assertions.assertEquals("test", reference1.getName());
        Assertions.assertEquals("b5f141f9c1b3f87d9b070157097130be7fb7563a", reference1.getSha());

        Reference reference2 = references.get(1);
        Assertions.assertEquals("v1.0.1", reference2.getName());
        Assertions.assertEquals("87acd380f4a91ba1eb200a082ad60f394f3062a5", reference2.getSha());
    }

    @Test
    public void testFindCommit() {
        Commit commit = refService.findCommit(providerRepository, TEST_COMMIT_SHA);
        Assertions.assertEquals("b5f141f9c1b3f87d9b070157097130be7fb7563a", commit.getSha());
        Assertions.assertEquals("webhook 10", commit.getMessage());
        Assertions.assertEquals("mingshewhe", commit.getCommitter().getName());
        Assertions.assertEquals("mingshewhe", commit.getAuthor().getName());
    }
}
