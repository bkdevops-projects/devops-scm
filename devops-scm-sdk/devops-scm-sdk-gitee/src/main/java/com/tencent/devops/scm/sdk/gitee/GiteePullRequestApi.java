package com.tencent.devops.scm.sdk.gitee;

import com.tencent.devops.scm.sdk.common.ScmHttpMethod;
import com.tencent.devops.scm.sdk.common.ScmRequest.Builder;
import com.tencent.devops.scm.sdk.common.enums.SortOrder;
import com.tencent.devops.scm.sdk.common.util.MapBuilder;
import com.tencent.devops.scm.sdk.gitee.enums.GiteeBranchOrderBy;
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeBranch;
import com.tencent.devops.scm.sdk.gitee.pojo.GiteePullRequest;
import com.tencent.devops.scm.sdk.gitee.pojo.GiteePullRequestDiff;
import java.util.Arrays;
import java.util.List;

public class GiteePullRequestApi extends AbstractGiteeApi {

    private static final String PULL_REQUEST_ID_URI_PATTERN = "repos/:fullName/pulls/:number";
    private static final String PULL_REQUEST_ID_DIFF_URI_PATTERN = "repos/:fullName/pulls/:number/files";

    public GiteePullRequestApi(GiteeApi tGitApi) {
        super(tGitApi);
    }

    /**
     * 根据number获取pull request
     *
     * @param projectIdOrPath 仓库名
     * @param pullRequestNumber pull request number
     * @return
     */
    public GiteePullRequest getPullRequest(
            Object projectIdOrPath,
            Long pullRequestNumber
    ) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        return giteeApi.createRequest()
                .method(ScmHttpMethod.GET)
                .withUrlPath(
                        PULL_REQUEST_ID_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("fullName", repoId)
                                .add("number", pullRequestNumber.toString())
                                .build()
                )
                .withRepoId(repoId)
                .fetch(GiteePullRequest.class);
    }

    /**
     * 根据number获取pull request
     *
     * @param projectIdOrPath 仓库名
     * @param pullRequestNumber pull request number
     * @return
     */
    public GiteePullRequestDiff[] listChanges(
            Object projectIdOrPath,
            Long pullRequestNumber
    ) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        return giteeApi.createRequest()
                .method(ScmHttpMethod.GET)
                .withUrlPath(
                        PULL_REQUEST_ID_DIFF_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("fullName", repoId)
                                .add("number", pullRequestNumber.toString())
                                .build()
                )
                .withRepoId(repoId)
                .fetch(GiteePullRequestDiff[].class);
    }
}
