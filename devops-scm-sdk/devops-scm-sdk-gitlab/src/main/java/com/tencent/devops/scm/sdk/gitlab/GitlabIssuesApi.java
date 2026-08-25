package com.tencent.devops.scm.sdk.gitlab;

import com.tencent.devops.scm.sdk.common.Requester;
import com.tencent.devops.scm.sdk.common.ScmHttpMethod;
import com.tencent.devops.scm.sdk.gitlab.pojo.GitlabIssue;
import com.tencent.devops.scm.sdk.gitlab.pojo.GitlabIssueParams;
import java.util.Arrays;
import java.util.List;

public class GitlabIssuesApi extends AbstractGitlabApi {
    GitlabIssuesApi(GitlabApi api) {
        super(api);
    }

    public List<GitlabIssue> getIssues(Object project, String state, String search, Integer page, Integer perPage) {
        GitlabIssue[] result = request(project, "projects/%s/issues").method(ScmHttpMethod.GET)
                .with("state", state).with("search", search).with(PAGE_PARAM, page(page))
                .with(PER_PAGE_PARAM, perPage(perPage)).fetch(GitlabIssue[].class);
        return Arrays.asList(result);
    }

    public GitlabIssue getIssue(Object project, long iid) {
        return request(project, "projects/%s/issues/" + iid).method(ScmHttpMethod.GET).fetch(GitlabIssue.class);
    }

    public GitlabIssue createIssue(Object project, GitlabIssueParams params) {
        return fill(request(project, "projects/%s/issues").method(ScmHttpMethod.POST), params, true)
                .fetch(GitlabIssue.class);
    }

    public GitlabIssue updateIssue(Object project, long iid, GitlabIssueParams params) {
        return fill(request(project, "projects/%s/issues/" + iid).method(ScmHttpMethod.PUT), params, false)
                .fetch(GitlabIssue.class);
    }

    public GitlabIssue closeIssue(Object project, long iid) {
        return updateIssue(project, iid, GitlabIssueParams.builder().stateEvent("close").build());
    }

    private Requester fill(Requester request, GitlabIssueParams params, boolean create) {
        request.with("title", params.getTitle()).with("description", params.getDescription())
                .with("assignee_id", params.getAssigneeId()).with("assignee_ids", params.getAssigneeIds())
                .with("labels", join(params.getLabels())).with("milestone_id", params.getMilestoneId());
        if (!create) {
            request.with("state_event", params.getStateEvent());
        }
        return request;
    }
}
