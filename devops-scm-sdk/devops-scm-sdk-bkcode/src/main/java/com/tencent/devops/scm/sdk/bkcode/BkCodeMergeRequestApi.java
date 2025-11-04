package com.tencent.devops.scm.sdk.bkcode;

import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeMergeRequest;
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeMergeRequestDiff;
import com.tencent.devops.scm.sdk.common.ScmHttpMethod;
import com.tencent.devops.scm.sdk.common.util.MapBuilder;

public class BkCodeMergeRequestApi extends AbstractBkCodeApi {

    private static final String PULL_REQUEST_ID_URI_PATTERN = "repos/:id/merge-request/:merge_request_id";
    private static final String PULL_REQUEST_NUMBER_URI_PATTERN = "repos/:id/merge-request/code/:merge_request_num";

    public BkCodeMergeRequestApi(BkCodeApi tGitApi) {
        super(tGitApi);
    }

    /**
     * 根据number获取merge request
     *
     * @param projectIdOrPath 仓库名
     * @param mergeRequestNumber merge request number
     * @return
     */
    public BkCodeMergeRequest getMergeRequestByNumber(
            Object projectIdOrPath,
            Integer mergeRequestNumber
    ) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        return bkCodeApi.createRequest()
                .method(ScmHttpMethod.GET)
                .withUrlPath(
                        PULL_REQUEST_NUMBER_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .add("merge_request_num", mergeRequestNumber.toString())
                                .build()
                )
                .withRepoId(repoId)
                .fetch(BkCodeMergeRequest.class);
    }

    /**
     * 根据number获取merge request
     *
     * @param projectIdOrPath 仓库名
     * @param mergeRequestId merge request id
     * @return
     */
    public BkCodeMergeRequest getMergeRequestById(
            Object projectIdOrPath,
            Long mergeRequestId
    ) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        return bkCodeApi.createRequest()
                .method(ScmHttpMethod.GET)
                .withUrlPath(
                        PULL_REQUEST_ID_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .add("merge_request_id", mergeRequestId.toString())
                                .build()
                )
                .withRepoId(repoId)
                .fetch(BkCodeMergeRequest.class);
    }
}
