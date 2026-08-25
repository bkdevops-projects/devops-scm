package com.tencent.devops.scm.sdk.gitlab;

import com.tencent.devops.scm.sdk.common.PagedIterable;
import com.tencent.devops.scm.sdk.common.ScmHttpMethod;
import com.tencent.devops.scm.sdk.gitlab.pojo.GitlabMember;
import com.tencent.devops.scm.sdk.gitlab.pojo.GitlabProject;
import java.util.Arrays;
import java.util.List;

public class GitlabProjectsApi extends AbstractGitlabApi {
    GitlabProjectsApi(GitlabApi api) {
        super(api);
    }

    public GitlabProject getProject(Object project) {
        return request(project, "projects/%s").method(ScmHttpMethod.GET).fetch(GitlabProject.class);
    }

    public List<GitlabProject> getProjects(String search, Integer page, Integer perPage) {
        GitlabProject[] result = gitlabApi.createRequest().method(ScmHttpMethod.GET).withUrlPath("projects")
                .with("membership", true).with("search", search).with(PAGE_PARAM, page(page))
                .with(PER_PAGE_PARAM, perPage(perPage)).fetch(GitlabProject[].class);
        return Arrays.asList(result);
    }

    public PagedIterable<GitlabProject> getProjects(int perPage) {
        return gitlabApi.createRequest().method(ScmHttpMethod.GET).withUrlPath("projects")
                .with("membership", true).with(PER_PAGE_PARAM, perPage(perPage)).toIterable(GitlabProject[].class);
    }

    public List<GitlabMember> getAllMembers(Object project, String query, Integer page, Integer perPage) {
        GitlabMember[] result = request(project, "projects/%s/members/all").method(ScmHttpMethod.GET)
                .with("query", query).with(PAGE_PARAM, page(page)).with(PER_PAGE_PARAM, perPage(perPage))
                .fetch(GitlabMember[].class);
        return Arrays.asList(result);
    }

    public GitlabMember getMember(Object project, String username) {
        PagedIterable<GitlabMember> members = request(project, "projects/%s/members/all")
                .method(ScmHttpMethod.GET).with("query", username).with(PER_PAGE_PARAM, DEFAULT_PER_PAGE)
                .toIterable(GitlabMember[].class);
        for (GitlabMember member : members) {
            if (username.equals(member.getUsername())) {
                return member;
            }
        }
        return null;
    }
}
