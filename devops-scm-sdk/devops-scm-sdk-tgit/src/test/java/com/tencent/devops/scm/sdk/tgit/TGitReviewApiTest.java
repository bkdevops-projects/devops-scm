package com.tencent.devops.scm.sdk.tgit;

import static org.mockito.Mockito.when;

import com.tencent.devops.scm.sdk.tgit.pojo.TGitReview;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class TGitReviewApiTest extends AbstractTGitTest {

    private static final Long TEST_MERGE_REQUEST_ID = 290966L;
    private static final Long TEST_COMMIT_REVIEW_IID = 195L;

    private static TGitApi tGitApi;
    private static TGitReviewApi reviewApi;

    public TGitReviewApiTest() {
        super();
    }

    @BeforeAll
    public static void testSetup() {
        tGitApi = mockTGitApi();
        when(tGitApi.getReviewApi()).thenReturn(Mockito.mock(TGitReviewApi.class));
        reviewApi = tGitApi.getReviewApi();
        when(tGitApi.getReviewApi().getMergeRequestReview(TEST_PROJECT_NAME, TEST_MERGE_REQUEST_ID))
                .thenReturn(read("get_merge_request_review.json", TGitReview.class));
        when(tGitApi.getReviewApi().getCommitReview(TEST_PROJECT_NAME, TEST_COMMIT_REVIEW_IID))
                .thenReturn(read("get_commit_review.json", TGitReview.class));
    }

    @Test
    public void testGetMergeRequestReview() {
        TGitReview mergeRequestReview = reviewApi.getMergeRequestReview(TEST_PROJECT_NAME, TEST_MERGE_REQUEST_ID);

        Assertions.assertEquals(336055L, mergeRequestReview.getId());
        Assertions.assertEquals(290966, mergeRequestReview.getReviewableId());
    }

    @Test
    public void testGetCommitReview() {
        TGitReview review = reviewApi.getCommitReview(TEST_PROJECT_NAME, TEST_COMMIT_REVIEW_IID);
        Assertions.assertEquals(108578305, review.getId());
        Assertions.assertEquals("git commit review title", review.getTitle());
        System.out.println(review);
    }
}
