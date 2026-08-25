package com.tencent.devops.scm.sdk.gitlab;

import com.tencent.devops.scm.sdk.common.Requester;
import com.tencent.devops.scm.sdk.common.ScmHttpMethod;
import com.tencent.devops.scm.sdk.gitlab.pojo.GitlabProjectHook;
import java.util.Arrays;
import java.util.List;

public class GitlabProjectHooksApi extends AbstractGitlabApi {
    GitlabProjectHooksApi(GitlabApi api) {
        super(api);
    }

    public List<GitlabProjectHook> getHooks(Object project, Integer page, Integer perPage) {
        GitlabProjectHook[] result = request(project, "projects/%s/hooks").method(ScmHttpMethod.GET)
                .with(PAGE_PARAM, page(page)).with(PER_PAGE_PARAM, perPage(perPage)).fetch(GitlabProjectHook[].class);
        return Arrays.asList(result);
    }

    public GitlabProjectHook getHook(Object project, long hookId) {
        return request(project, "projects/%s/hooks/" + hookId).method(ScmHttpMethod.GET)
                .fetch(GitlabProjectHook.class);
    }

    public GitlabProjectHook addHook(Object project, GitlabProjectHook hook, String token) {
        return fill(request(project, "projects/%s/hooks").method(ScmHttpMethod.POST), hook, token)
                .fetch(GitlabProjectHook.class);
    }

    public GitlabProjectHook updateHook(Object project, long hookId, GitlabProjectHook hook, String token) {
        return fill(request(project, "projects/%s/hooks/" + hookId).method(ScmHttpMethod.PUT), hook, token)
                .fetch(GitlabProjectHook.class);
    }

    public void deleteHook(Object project, long hookId) {
        request(project, "projects/%s/hooks/" + hookId).method(ScmHttpMethod.DELETE).send();
    }

    private Requester fill(Requester request, GitlabProjectHook hook, String token) {
        return request.with("url", hook.getUrl()).with("push_events", hook.isPushEvents())
                .with("tag_push_events", hook.isTagPushEvents()).with("issues_events", hook.isIssuesEvents())
                .with("merge_requests_events", hook.isMergeRequestsEvents()).with("note_events", hook.isNoteEvents())
                .with("enable_ssl_verification", hook.isEnableSslVerification()).with("token", token);
    }
}
