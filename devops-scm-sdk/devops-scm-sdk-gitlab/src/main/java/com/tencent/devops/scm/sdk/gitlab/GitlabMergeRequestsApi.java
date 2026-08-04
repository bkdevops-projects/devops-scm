package com.tencent.devops.scm.sdk.gitlab;

import com.tencent.devops.scm.sdk.common.Requester;
import com.tencent.devops.scm.sdk.common.ScmHttpMethod;
import com.tencent.devops.scm.sdk.gitlab.pojo.GitlabCommit;
import com.tencent.devops.scm.sdk.gitlab.pojo.GitlabMergeRequest;
import com.tencent.devops.scm.sdk.gitlab.pojo.GitlabMergeRequestParams;
import java.util.Arrays;
import java.util.List;

public class GitlabMergeRequestsApi extends AbstractGitlabApi {
    GitlabMergeRequestsApi(GitlabApi api) {
        super(api);
    }

    public List<GitlabMergeRequest> getMergeRequests(Object project, String state, String sourceBranch,
            String targetBranch, Integer page, Integer perPage) {
        GitlabMergeRequest[] result = request(project, "projects/%s/merge_requests").method(ScmHttpMethod.GET)
                .with("state", state).with("source_branch", sourceBranch).with("target_branch", targetBranch)
                .with(PAGE_PARAM, page(page)).with(PER_PAGE_PARAM, perPage(perPage))
                .fetch(GitlabMergeRequest[].class);
        return Arrays.asList(result);
    }

    public GitlabMergeRequest getMergeRequest(Object project, long iid) {
        return request(project, "projects/%s/merge_requests/" + iid).method(ScmHttpMethod.GET)
                .fetch(GitlabMergeRequest.class);
    }

    public GitlabMergeRequest createMergeRequest(Object project, GitlabMergeRequestParams params) {
        return fill(request(project, "projects/%s/merge_requests").method(ScmHttpMethod.POST), params, true)
                .fetch(GitlabMergeRequest.class);
    }

    public GitlabMergeRequest updateMergeRequest(Object project, long iid, GitlabMergeRequestParams params) {
        return fill(request(project, "projects/%s/merge_requests/" + iid).method(ScmHttpMethod.PUT), params, false)
                .fetch(GitlabMergeRequest.class);
    }

    public GitlabMergeRequest closeMergeRequest(Object project, long iid) {
        return updateMergeRequest(project, iid, GitlabMergeRequestParams.builder().stateEvent("close").build());
    }

    public GitlabMergeRequest mergeMergeRequest(Object project, long iid, String mergeCommitMessage,
            Boolean shouldRemoveSourceBranch, Boolean squash) {
        return request(project, "projects/%s/merge_requests/" + iid + "/merge").method(ScmHttpMethod.PUT)
                .with("merge_commit_message", mergeCommitMessage)
                .with("should_remove_source_branch", shouldRemoveSourceBranch).with("squash", squash)
                .fetch(GitlabMergeRequest.class);
    }

    public GitlabMergeRequest getMergeRequestChanges(Object project, long iid) {
        return request(project, "projects/%s/merge_requests/" + iid + "/changes").method(ScmHttpMethod.GET)
                .fetch(GitlabMergeRequest.class);
    }

    public List<GitlabCommit> getMergeRequestCommits(Object project, long iid, Integer page, Integer perPage) {
        GitlabCommit[] result = request(project, "projects/%s/merge_requests/" + iid + "/commits")
                .method(ScmHttpMethod.GET).with(PAGE_PARAM, page(page)).with(PER_PAGE_PARAM, perPage(perPage))
                .fetch(GitlabCommit[].class);
        return Arrays.asList(result);
    }

    private Requester fill(Requester request, GitlabMergeRequestParams params, boolean create) {
        request.with("target_branch", params.getTargetBranch()).with("title", params.getTitle())
                .with("description", params.getDescription()).with("assignee_id", params.getAssigneeId())
                .with("assignee_ids", params.getAssigneeIds()).with("labels", join(params.getLabels()))
                .with("milestone_id", params.getMilestoneId())
                .with("remove_source_branch", params.getRemoveSourceBranch())
                .with("squash", params.getSquash());
        if (create) {
            request.with("source_branch", params.getSourceBranch())
                    .with("target_project_id", params.getTargetProjectId());
        } else {
            request.with("state_event", params.getStateEvent());
        }
        return request;
    }
}
