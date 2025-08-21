package com.tencent.devops.scm.sdk.tgit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tencent.devops.scm.sdk.tgit.enums.TGitMergeRequestState;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitAuthor;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitDiff;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitMergeRequest;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitMergeRequestFilter;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitMergeRequestParams;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitMilestone;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class TGitMergeRequestApiTest extends AbstractTGitTest {
    private static final Long TEST_MERGE_REQUEST_ID = 290966L;
    private static final Integer TEST_MERGE_REQUEST_IID = 9;
    private static final TGitMergeRequestFilter allFilter = TGitMergeRequestFilter.builder().build();

    private static TGitApi tGitApi;
    private static TGitMergeRequestApi mergeRequestApi;

    public TGitMergeRequestApiTest() {
        super();
    }

    @BeforeAll
    public static void testSetup() {
        tGitApi = mockTGitApi();
        when(tGitApi.getMergeRequestApi()).thenReturn(Mockito.mock(TGitMergeRequestApi.class));
        mergeRequestApi = tGitApi.getMergeRequestApi();
        when(mergeRequestApi.getMergeRequestById(TEST_PROJECT_NAME, TEST_MERGE_REQUEST_ID))
                .thenReturn(read("get_merge_request.json", TGitMergeRequest.class));
        when(mergeRequestApi.getMergeRequestByIid(TEST_PROJECT_NAME, TEST_MERGE_REQUEST_IID))
                .thenReturn(read("get_merge_request.json", TGitMergeRequest.class));
        when(mergeRequestApi.getMergeRequestChanges(TEST_PROJECT_NAME, TEST_MERGE_REQUEST_ID))
                .thenReturn(read("get_merge_request_change.json", TGitMergeRequest.class));
        when(mergeRequestApi.getMergeRequestChanges(TEST_PROJECT_NAME, TEST_MERGE_REQUEST_ID))
                .thenReturn(read("get_merge_request_change.json", TGitMergeRequest.class));
        when(mergeRequestApi.getMergeRequests(TEST_PROJECT_NAME, allFilter))
                .thenReturn(read("list_merge_request_all.json", new TypeReference<List<TGitMergeRequest>>() {
                }));
        when(mergeRequestApi.getMergeRequests(TEST_PROJECT_NAME, TGitMergeRequestState.OPENED))
                .thenReturn(
                        read("list_merge_request_opened.json", new TypeReference<List<TGitMergeRequest>>() {
                        }));
        when(mergeRequestApi.createMergeRequest(anyString(), any()))
                .thenReturn(
                        read("create_merge_request.json", new TypeReference<TGitMergeRequest>() {
                        }));
    }

    @Test
    public void testGetMergeRequestById() {
        TGitMergeRequest mergeRequest = mergeRequestApi.getMergeRequestById(TEST_PROJECT_NAME, TEST_MERGE_REQUEST_ID);
        assertMergeRequest(mergeRequest);
        Assertions.assertEquals("6b5485509c502d34b0eaa773423156585b411eb8", mergeRequest.getBaseCommit());
        Assertions.assertEquals("87acd380f4a91ba1eb200a082ad60f394f3062a5", mergeRequest.getTargetCommit());
        Assertions.assertEquals("9c9f8cc062060fdad67137e5e102689be765b4d4", mergeRequest.getSourceCommit());
    }

    @Test
    public void testGetMergeRequestByIid() {
        TGitMergeRequest mergeRequest = mergeRequestApi.getMergeRequestByIid(TEST_PROJECT_NAME, TEST_MERGE_REQUEST_IID);
        assertMergeRequest(mergeRequest);
        Assertions.assertEquals("6b5485509c502d34b0eaa773423156585b411eb8", mergeRequest.getBaseCommit());
        Assertions.assertEquals("87acd380f4a91ba1eb200a082ad60f394f3062a5", mergeRequest.getTargetCommit());
        Assertions.assertEquals("9c9f8cc062060fdad67137e5e102689be765b4d4", mergeRequest.getSourceCommit());
    }

    @Test
    public void testGetMergeRequests() {
        List<TGitMergeRequest> mergeRequests = mergeRequestApi.getMergeRequests(TEST_PROJECT_NAME, allFilter);
        Assertions.assertEquals(9, mergeRequests.size());
        assertMergeRequest(mergeRequests.get(0));
        Assertions.assertEquals("merged", mergeRequests.get(8).getState());

        mergeRequests = tGitApi.getMergeRequestApi().getMergeRequests(TEST_PROJECT_NAME, TGitMergeRequestState.OPENED);
        assertMergeRequest(mergeRequests.get(0));
    }

    @Test
    public void testGetMergeRequestChanges() {
        TGitMergeRequest mergeRequest = mergeRequestApi.getMergeRequestChanges(TEST_PROJECT_NAME,
                TEST_MERGE_REQUEST_ID);
        assertMergeRequest(mergeRequest);

        List<TGitDiff> files = mergeRequest.getFiles();
        Assertions.assertFalse(files.isEmpty());

        TGitDiff diff1 = files.get(0);
        Assertions.assertEquals("mr.txt", diff1.getOldPath());
        Assertions.assertEquals("mr.txt", diff1.getNewPath());
        Assertions.assertEquals("33188", diff1.getAMode());
        Assertions.assertEquals("33188", diff1.getBMode());
        Assertions.assertFalse(diff1.getNewFile());
        Assertions.assertFalse(diff1.getDeletedFile());
        Assertions.assertFalse(diff1.getRenamedFile());
        Assertions.assertFalse(diff1.getTooLarge());
        Assertions.assertFalse(diff1.getCollapse());
        Assertions.assertEquals(15, diff1.getAdditions());
        Assertions.assertEquals(1, diff1.getDeletions());
    }

    @Test
    public void createMergeRequest() {
        TGitMergeRequestParams requestParams = TGitMergeRequestParams.builder()
                .sourceBranch("feat_101")
                .targetBranch("master")
                .title("devops触发调试")
                .description("description")
                .build();
        TGitMergeRequest mergeRequest = mergeRequestApi.createMergeRequest(
                TEST_PROJECT_NAME,
                requestParams
        );
        Assertions.assertEquals(requestParams.getTitle(), mergeRequest.getTitle());
        Assertions.assertEquals(requestParams.getSourceBranch(), mergeRequest.getSourceBranch());
        Assertions.assertEquals(requestParams.getTargetBranch(), mergeRequest.getTargetBranch());
        Assertions.assertEquals(requestParams.getDescription(), mergeRequest.getDescription());
    }

    private static void assertMergeRequest(TGitMergeRequest mergeRequest) {
        Assertions.assertEquals(290966, mergeRequest.getId());
        Assertions.assertEquals(9, mergeRequest.getIid());
        Assertions.assertEquals("mr_test", mergeRequest.getTitle());
        Assertions.assertEquals(130762, mergeRequest.getSourceProjectId());
        Assertions.assertEquals(130762, mergeRequest.getTargetProjectId());
        Assertions.assertEquals(130762, mergeRequest.getProjectId());
        Assertions.assertEquals("master", mergeRequest.getTargetBranch());
        Assertions.assertEquals("mr_test", mergeRequest.getSourceBranch());
        Assertions.assertEquals("opened", mergeRequest.getState());
        Assertions.assertEquals("can_be_merged", mergeRequest.getMergeStatus());

        Assertions.assertFalse(mergeRequest.getWorkInProgress());

        TGitAuthor author = mergeRequest.getAuthor();
        Assertions.assertEquals(132102, author.getId());
        Assertions.assertEquals("mingshewhe", author.getUsername());
        Assertions.assertEquals("小明", author.getName());

        TGitMilestone milestone = mergeRequest.getMilestone();
        Assertions.assertEquals(2155, milestone.getId());
        Assertions.assertEquals(1, milestone.getIid());
        Assertions.assertEquals("v1.0", milestone.getTitle());
        Assertions.assertEquals("active", milestone.getState());
        Assertions.assertEquals("开源", milestone.getDescription());
    }
}
