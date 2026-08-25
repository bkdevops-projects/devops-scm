package com.tencent.devops.scm.sdk.gitlab;

import com.tencent.devops.scm.sdk.common.ScmHttpMethod;
import com.tencent.devops.scm.sdk.common.exception.ScmHttpRetryException;
import com.tencent.devops.scm.sdk.gitlab.pojo.GitlabCommitStatus;
import java.util.Arrays;
import java.util.List;

public class GitlabCommitStatusesApi extends AbstractGitlabApi {
    GitlabCommitStatusesApi(GitlabApi api) {
        super(api);
    }

    public GitlabCommitStatus create(Object project, String sha, String state, String name, String ref,
            String targetUrl, String description) {
        requireSha(sha);
        try {
            return request(project, "projects/%s/statuses/" + segment(sha)).method(ScmHttpMethod.POST)
                    .with("state", state).with("name", name).with("ref", ref).with("target_url", targetUrl)
                    .with("description", description).fetch(GitlabCommitStatus.class);
        } catch (GitlabApiException error) {
            if (error.getStatusCode() == 409) {
                throw new ScmHttpRetryException();
            }
            throw error;
        }
    }

    public List<GitlabCommitStatus> getStatuses(Object project, String sha, String ref, String name,
            Integer page, Integer perPage) {
        requireSha(sha);
        GitlabCommitStatus[] result = request(project,
                "projects/%s/repository/commits/" + segment(sha) + "/statuses").method(ScmHttpMethod.GET)
                .with("ref", ref).with("name", name).with(PAGE_PARAM, page(page))
                .with(PER_PAGE_PARAM, perPage(perPage)).fetch(GitlabCommitStatus[].class);
        return Arrays.asList(result);
    }

    private void requireSha(String sha) {
        if (sha == null || sha.trim().isEmpty()) {
            throw new IllegalArgumentException("sha cannot be blank");
        }
    }
}
