package com.tencent.devops.scm.sdk.tgit;

import static com.tencent.devops.scm.sdk.tgit.util.TGitStringHelper.formatDate;
import static com.tencent.devops.scm.sdk.tgit.util.TGitStringHelper.join;

import com.tencent.devops.scm.sdk.common.Requester;
import com.tencent.devops.scm.sdk.common.ScmHttpMethod;
import com.tencent.devops.scm.sdk.common.util.MapBuilder;
import com.tencent.devops.scm.sdk.tgit.enums.TGitMergeRequestState;
import com.tencent.devops.scm.sdk.tgit.enums.TGitStateEvent;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitCommit;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitMergeRequest;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitMergeRequestFilter;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitMergeRequestParams;
import java.util.Arrays;
import java.util.List;

/**
 * merge request接口
 * <pre>接口文档: <a href="https://code.tencent.com/help/api/mergeRequest">merge request接口说明</a></pre>
 */
public class TGitMergeRequestApi extends AbstractTGitApi {

    // merge_request请求uri
    private static final String MERGE_REQUEST_ID_URI_PATTERN =
            "projects/:id/merge_request/:merge_request_id";
    private static final String MERGE_REQUEST_IID_URI_PATTERN =
            "projects/:id/merge_request/iid/:merge_request_iid";
    private static final String MERGE_REQUEST_CHANGES_URI_PATTERN =
            "projects/:id/merge_request/:merge_request_id/changes";
    private static final String MERGE_REQUEST_COMMITS_URI_PATTERN =
            "projects/:id/merge_request/:merge_request_id/commits";
    private static final String MERGE_REQUESTS_URI_PATTERN =
            "projects/:id/merge_requests";
    private static final String MERGE_REQUESTS_ACTION_MERGE_URI_PATTERN =
            "projects/:id/merge_request/:merge_request_id/merge";

    public TGitMergeRequestApi(TGitApi tGitApi) {
        super(tGitApi);
    }

    public TGitMergeRequest getMergeRequestById(Object projectIdOrPath, Long mergeRequestId) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        return tGitApi.createRequest()
                .method(ScmHttpMethod.GET)
                .withUrlPath(
                        MERGE_REQUEST_ID_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .add("merge_request_id", mergeRequestId.toString())
                                .build()
                )
                .withRepoId(repoId)
                .fetch(TGitMergeRequest.class);
    }

    public TGitMergeRequest getMergeRequestByIid(Object projectIdOrPath, Integer mergeRequestIid) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        return tGitApi.createRequest()
                .method(ScmHttpMethod.GET)
                .withUrlPath(
                        MERGE_REQUEST_IID_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .add("merge_request_iid", mergeRequestIid.toString())
                                .build()
                )
                .withRepoId(repoId)
                .fetch(TGitMergeRequest.class);
    }

    public List<TGitMergeRequest> getMergeRequests(Object projectIdOrPath, TGitMergeRequestState state) {
        TGitMergeRequestFilter filter = TGitMergeRequestFilter.builder()
                .state(state)
                .build();
        return getMergeRequests(projectIdOrPath, filter);
    }

    public List<TGitMergeRequest> getMergeRequests(Object projectIdOrPath, TGitMergeRequestState state, Integer page,
            Integer perPage) {
        TGitMergeRequestFilter filter = TGitMergeRequestFilter.builder()
                .state(state)
                .page(page)
                .perPage(perPage)
                .build();
        return getMergeRequests(projectIdOrPath, filter);
    }

    public List<TGitMergeRequest> getMergeRequests(Object projectIdOrPath, TGitMergeRequestFilter filter) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        Requester requester = tGitApi.createRequest()
                .method(ScmHttpMethod.GET)
                .withUrlPath(
                        MERGE_REQUESTS_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .build()
                )
                .withRepoId(repoId);
        fillQueryParams(requester, filter);
        TGitMergeRequest[] mergeRequests = requester.fetch(TGitMergeRequest[].class);
        return Arrays.asList(mergeRequests);
    }

    public TGitMergeRequest createMergeRequest(Object projectIdOrPath, TGitMergeRequestParams params) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        Requester requester = tGitApi.createRequest()
                .method(ScmHttpMethod.POST)
                .withUrlPath(
                        MERGE_REQUESTS_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .build()
                )
                .withRepoId(repoId);
        fillBody(requester, params, true);
        return requester.fetch(TGitMergeRequest.class);
    }

    public TGitMergeRequest updateMergeRequest(Object projectIdOrPath, Long mergeRequestId,
            TGitMergeRequestParams params) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        Requester requester = tGitApi.createRequest()
                .method(ScmHttpMethod.PUT)
                .withUrlPath(
                        MERGE_REQUEST_ID_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .add("merge_request_id", mergeRequestId.toString())
                                .build()
                )
                .withRepoId(repoId);
        fillBody(requester, params, false);
        return requester.fetch(TGitMergeRequest.class);
    }

    public TGitMergeRequest closeMergeRequest(Object projectIdOrPath, Long mergeRequestId) {
        TGitMergeRequestParams params = TGitMergeRequestParams.builder()
                .stateEvent(TGitStateEvent.CLOSE)
                .build();
        return updateMergeRequest(projectIdOrPath, mergeRequestId, params);
    }

    public TGitMergeRequest mergeMergeRequest(Object projectIdOrPath, Long mergeRequestId) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        return tGitApi.createRequest()
                .method(ScmHttpMethod.PUT)
                .withUrlPath(
                        MERGE_REQUESTS_ACTION_MERGE_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .add("merge_request_id", mergeRequestId.toString())
                                .build()
                )
                .withRepoId(repoId)
                .fetch(TGitMergeRequest.class);
    }

    /**
     * 查询合并请求的代码变更
     */
    public TGitMergeRequest getMergeRequestChanges(Object projectIdOrPath, Long mergeRequestId) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        return tGitApi.createRequest()
                .method(ScmHttpMethod.GET)
                .withUrlPath(
                        MERGE_REQUEST_CHANGES_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .add("merge_request_id", mergeRequestId.toString())
                                .build()
                )
                .withRepoId(repoId)
                .fetch(TGitMergeRequest.class);
    }

    /**
     * 获取合并请求中的提交
     */
    public List<TGitCommit> getMergeRequestCommits(Object projectIdOrPath, Long mergeRequestId,
            Integer page, Integer perPage) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        TGitCommit[] commits = tGitApi.createRequest()
                .method(ScmHttpMethod.GET)
                .withUrlPath(
                        MERGE_REQUEST_COMMITS_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .add("merge_request_id", mergeRequestId.toString())
                                .build()
                )
                .withRepoId(repoId)
                .with(PAGE_PARAM, page)
                .with(PER_PAGE_PARAM, perPage)
                .fetch(TGitCommit[].class);
        return Arrays.asList(commits);
    }

    private void fillQueryParams(Requester requester, TGitMergeRequestFilter filter) {
        requester.with("iid", filter.getIid())
                .with("state", filter.getState() == null ? null : filter.getState().toValue())
                .with("source_branch", filter.getSourceBranch())
                .with("target_branch", filter.getTargetBranch())
                .with("order_by", filter.getOrderBy() == null ? null : filter.getOrderBy().toValue())
                .with("sort", filter.getSort() == null ? null : filter.getSort().toValue())
                .with("created_before", formatDate(filter.getCreatedBefore()))
                .with("created_after", formatDate(filter.getCreatedAfter()))
                .with("updated_after", formatDate(filter.getUpdatedAfter()))
                .with("updated_before", formatDate(filter.getUpdatedBefore()))
                .with(TGitConstants.PAGE_PARAM, filter.getPage())
                .with(TGitConstants.PER_PAGE_PARAM, filter.getPerPage());
    }

    private void fillBody(Requester requester, TGitMergeRequestParams params, boolean isCreate) {
        requester.with("target_branch", params.getTargetBranch())
                .with("title", params.getTitle())
                .with("assignee_id", params.getAssigneeId())
                .with("description", params.getDescription())
                .with("labels", join(params.getLabels()))
                .with("reviewers", join(params.getReviewers()))
                .with("necessary_reviewers", join(params.getNecessaryReviewers()))
                .with("approver_rule", params.getApproverRule())
                .with("necessary_approver_rule", params.getNecessaryApproverRule());

        if (isCreate) {
            requester.with("source_branch", params.getSourceBranch())
                    .with("target_project_id", params.getTargetProjectId());
        } else {
            requester.with("state_event", params.getStateEvent() == null ? null : params.getStateEvent().toValue());
        }
    }
}
