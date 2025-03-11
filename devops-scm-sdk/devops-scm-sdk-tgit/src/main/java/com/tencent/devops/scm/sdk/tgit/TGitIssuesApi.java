package com.tencent.devops.scm.sdk.tgit;

import static com.tencent.devops.scm.sdk.tgit.util.TGitStringHelper.formatDate;

import com.tencent.devops.scm.sdk.common.Requester;
import com.tencent.devops.scm.sdk.common.ScmHttpMethod;
import com.tencent.devops.scm.sdk.common.util.MapBuilder;
import com.tencent.devops.scm.sdk.tgit.enums.TGitIssueState;
import com.tencent.devops.scm.sdk.tgit.enums.TGitStateEvent;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitIssue;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitIssueFilter;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitIssueParams;
import com.tencent.devops.scm.sdk.tgit.util.TGitStringHelper;
import java.util.Arrays;
import java.util.List;

/**
 * issue 接口
 * <pre>接口文档: <a href="https://code.tencent.com/help/api/issue">工蜂issue接口文档</a></pre>
 */
public class TGitIssuesApi extends AbstractTGitApi {

    private static final String ISSUES_URI_PATTERN = "projects/:id/issues";
    private static final String ISSUES_ID_URI_PATTERN = "projects/:id/issues/:issue_id";
    private static final String ISSUES_IDD_URI_PATTERN = "projects/:id/issues/iid/:issue_iid";

    public TGitIssuesApi(TGitApi tGitApi) {
        super(tGitApi);
    }

    public List<TGitIssue> getIssues(Object projectIdOrPath, TGitIssueState state) {
        TGitIssueFilter filter = TGitIssueFilter.builder().state(state).build();
        return getIssues(projectIdOrPath, filter);
    }

    public List<TGitIssue> getIssues(Object projectIdOrPath, TGitIssueState state, Integer page, Integer perPage) {
        TGitIssueFilter filter = TGitIssueFilter.builder()
                .state(state)
                .page(page)
                .perPage(perPage)
                .build();
        return getIssues(projectIdOrPath, filter);
    }

    public List<TGitIssue> getIssues(Object projectIdOrPath, TGitIssueFilter filter) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        Requester requester = tGitApi.createRequest()
                .method(ScmHttpMethod.GET)
                .withUrlPath(
                        ISSUES_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .build()
                )
                .withRepoId(repoId);
        fillQueryParams(requester, filter);
        TGitIssue[] issues = requester.fetch(TGitIssue[].class);
        return Arrays.asList(issues);
    }

    public TGitIssue createIssue(Object projectIdOrPath, String title, String description) {
        TGitIssueParams params = TGitIssueParams.builder()
                .title(title)
                .description(description)
                .build();
        return createIssue(projectIdOrPath, params);
    }

    public TGitIssue createIssue(Object projectIdOrPath, TGitIssueParams params) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        Requester requester = tGitApi.createRequest()
                .method(ScmHttpMethod.POST)
                .withUrlPath(
                        ISSUES_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .build()
                )
                .withRepoId(repoId);
        fillBody(requester, params, true);
        return requester.fetch(TGitIssue.class);
    }

    public TGitIssue updateIssue(Object projectIdOrPath, Long issueId, TGitIssueParams params) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        Requester requester = tGitApi.createRequest()
                .method(ScmHttpMethod.PUT)
                .withUrlPath(
                        ISSUES_ID_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .add("issue_id", issueId.toString())
                                .build()
                )
                .withRepoId(repoId);
        fillBody(requester, params, false);
        return requester.fetch(TGitIssue.class);
    }

    public TGitIssue updateIssueByIid(Object projectIdOrPath, Integer issueIid, TGitIssueParams params) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        Requester requester = tGitApi.createRequest()
                .method(ScmHttpMethod.PUT)
                .withUrlPath(
                        ISSUES_IDD_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .add("issue_iid", issueIid.toString())
                                .build()
                )
                .withRepoId(repoId);
        fillBody(requester, params, false);
        return requester.fetch(TGitIssue.class);
    }

    public TGitIssue closeIssue(Object projectIdOrPath, Long issueId) {
        TGitIssueParams params = TGitIssueParams.builder()
                .stateEvent(TGitStateEvent.CLOSE)
                .build();
        return updateIssue(projectIdOrPath, issueId, params);
    }

    public TGitIssue getIssue(Object projectIdOrPath, Long issueId) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        Requester requester = tGitApi.createRequest()
                .method(ScmHttpMethod.GET)
                .withUrlPath(
                        ISSUES_ID_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .add("issue_id", issueId.toString())
                                .build()
                )
                .withRepoId(repoId);
        return requester.fetch(TGitIssue.class);
    }

    public TGitIssue getIssueByIid(Object projectIdOrPath, Integer issueIid) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        Requester requester = tGitApi.createRequest()
                .method(ScmHttpMethod.GET)
                .withUrlPath(
                        ISSUES_IDD_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .add("issue_iid", issueIid.toString())
                                .build()
                )
                .withRepoId(repoId);
        return requester.fetch(TGitIssue.class);
    }

    private void fillQueryParams(Requester requester, TGitIssueFilter filter) {
        requester.with("iid", filter.getIid())
                .with("state", filter.getState() == null ? null : filter.getState().toValue())
                .with("labels", TGitStringHelper.join(filter.getLabels()))
                .with("milestone", filter.getMilestone())
                .with("created_before", formatDate(filter.getCreatedBefore()))
                .with("created_after", formatDate(filter.getCreatedAfter()))
                .with("order_by", filter.getOrderBy() == null ? null : filter.getOrderBy().toValue())
                .with("sort", filter.getSort() == null ? null : filter.getSort().toValue())
                .with(TGitConstants.PAGE_PARAM, filter.getPage())
                .with(TGitConstants.PER_PAGE_PARAM, filter.getPerPage());
    }

    private void fillBody(Requester requester, TGitIssueParams params, boolean isCreate) {
        requester.with("title", params.getTitle())
                .with("grade", params.getGrade())
                .with("description", params.getDescription())
                .with("assignee_ids", TGitStringHelper.join(params.getAssigneeIds()))
                .with("milestone_id", params.getMilestoneId())
                .with("labels", TGitStringHelper.join(params.getLabels()));
        if (!isCreate) {
            requester.with("resolve_state", params.getResolveState() == null ? null : params.getResolveState());
            requester.with("state_event", params.getStateEvent() == null ? null : params.getStateEvent());

        }
    }
}
