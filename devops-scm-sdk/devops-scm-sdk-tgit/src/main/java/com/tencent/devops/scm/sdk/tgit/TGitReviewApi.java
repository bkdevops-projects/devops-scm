package com.tencent.devops.scm.sdk.tgit;

import com.tencent.devops.scm.sdk.common.ScmHttpMethod;
import com.tencent.devops.scm.sdk.common.util.MapBuilder;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitReview;

/**
 * 评审 接口
 * <pre>接口文档: <a href="https://code.tencent.com/help/api/mr_review">MR评审接口文档</a></pre>
 */
public class TGitReviewApi extends AbstractTGitApi {

    private static final String PROJECT_MR_ID_REVIEW_URI_PATTERN =
            "projects/:id/merge_request/:merge_request_id/review";
    private static final String PROJECT_COMMIT_REVIEW_ID_URI_PATTERN =
            "projects/:id/review/:review_id";

    public TGitReviewApi(TGitApi tGitApi) {
        super(tGitApi);
    }

    public TGitReview getMergeRequestReview(Object projectIdOrPath, Long mergeRequestId) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        return tGitApi.createRequest()
                .method(ScmHttpMethod.GET)
                .withUrlPath(
                        PROJECT_MR_ID_REVIEW_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .add("merge_request_id", mergeRequestId.toString())
                                .build()
                )
                .withRepoId(repoId)
                .fetch(TGitReview.class);
    }

    public TGitReview getCommitReview(Object projectIdOrPath, Long reviewId) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        return tGitApi.createRequest()
                .method(ScmHttpMethod.GET)
                .withUrlPath(
                        PROJECT_COMMIT_REVIEW_ID_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .add("review_id", reviewId.toString())
                                .build()
                )
                .withRepoId(repoId)
                .fetch(TGitReview.class);
    }
}
