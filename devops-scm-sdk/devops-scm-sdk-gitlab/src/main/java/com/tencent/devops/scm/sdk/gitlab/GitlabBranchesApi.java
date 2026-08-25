package com.tencent.devops.scm.sdk.gitlab;

import com.tencent.devops.scm.sdk.common.ScmHttpMethod;
import com.tencent.devops.scm.sdk.gitlab.pojo.GitlabBranch;
import java.util.Arrays;
import java.util.List;

public class GitlabBranchesApi extends AbstractGitlabApi {
    GitlabBranchesApi(GitlabApi api) {
        super(api);
    }

    public List<GitlabBranch> getBranches(Object project, String search, Integer page, Integer perPage) {
        GitlabBranch[] result = request(project, "projects/%s/repository/branches").method(ScmHttpMethod.GET)
                .with("search", search).with(PAGE_PARAM, page(page)).with(PER_PAGE_PARAM, perPage(perPage))
                .fetch(GitlabBranch[].class);
        return Arrays.asList(result);
    }

    public GitlabBranch getBranch(Object project, String branch) {
        return request(project, "projects/%s/repository/branches/" + segment(branch)).method(ScmHttpMethod.GET)
                .fetch(GitlabBranch.class);
    }

    public GitlabBranch createBranch(Object project, String branch, String ref) {
        return request(project, "projects/%s/repository/branches").method(ScmHttpMethod.POST)
                .with("branch", branch).with("ref", ref).fetch(GitlabBranch.class);
    }

    public void deleteBranch(Object project, String branch) {
        request(project, "projects/%s/repository/branches/" + segment(branch)).method(ScmHttpMethod.DELETE).send();
    }
}
