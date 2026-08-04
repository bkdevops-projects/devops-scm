package com.tencent.devops.scm.sdk.gitlab;

import com.tencent.devops.scm.sdk.common.ScmHttpMethod;
import com.tencent.devops.scm.sdk.gitlab.pojo.GitlabCommit;
import com.tencent.devops.scm.sdk.gitlab.pojo.GitlabCompareResults;
import com.tencent.devops.scm.sdk.gitlab.pojo.GitlabDiff;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class GitlabCommitsApi extends AbstractGitlabApi {
    GitlabCommitsApi(GitlabApi api) {
        super(api);
    }

    public List<GitlabCommit> getCommits(Object project, String ref, String path, Date since, Date until,
            Integer page, Integer perPage) {
        GitlabCommit[] result = request(project, "projects/%s/repository/commits").method(ScmHttpMethod.GET)
                .with("ref_name", ref).with("path", path).with("since", iso(since)).with("until", iso(until))
                .with(PAGE_PARAM, page(page)).with(PER_PAGE_PARAM, perPage(perPage)).fetch(GitlabCommit[].class);
        return Arrays.asList(result);
    }

    public GitlabCommit getCommit(Object project, String sha) {
        return request(project, "projects/%s/repository/commits/" + segment(sha)).method(ScmHttpMethod.GET)
                .fetch(GitlabCommit.class);
    }

    public List<GitlabDiff> getDiff(Object project, String sha, Integer page, Integer perPage) {
        GitlabDiff[] result = request(project, "projects/%s/repository/commits/" + segment(sha) + "/diff")
                .method(ScmHttpMethod.GET).with(PAGE_PARAM, page(page)).with(PER_PAGE_PARAM, perPage(perPage))
                .fetch(GitlabDiff[].class);
        return Arrays.asList(result);
    }

    public GitlabCompareResults compare(Object project, String from, String to, Boolean straight) {
        return request(project, "projects/%s/repository/compare").method(ScmHttpMethod.GET)
                .with("from", from).with("to", to).with("straight", straight).fetch(GitlabCompareResults.class);
    }

    private String iso(Date value) {
        return value == null ? null : Instant.ofEpochMilli(value.getTime()).toString();
    }
}
