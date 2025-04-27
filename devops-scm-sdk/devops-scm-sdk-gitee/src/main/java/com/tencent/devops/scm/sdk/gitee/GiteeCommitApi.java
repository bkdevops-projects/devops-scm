package com.tencent.devops.scm.sdk.gitee;

import com.tencent.devops.scm.sdk.common.ScmHttpMethod;
import com.tencent.devops.scm.sdk.common.ScmRequest.Builder;
import com.tencent.devops.scm.sdk.common.enums.SortOrder;
import com.tencent.devops.scm.sdk.common.util.MapBuilder;
import com.tencent.devops.scm.sdk.gitee.enums.GiteeBranchOrderBy;
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeBranch;
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeCommitDetail;
import java.util.Arrays;
import java.util.List;

public class GiteeCommitApi extends AbstractGiteeApi {
    private static final String BRANCHES_URI_PATTERN = "repos/:fullName/commits/:sha";

    public GiteeCommitApi(GiteeApi tGitApi) {
        super(tGitApi);
    }

    /**
     * 获取分支列表
     * @param projectIdOrPath 仓库名
     */
    public GiteeCommitDetail getCommit(Object projectIdOrPath, String sha) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        return giteeApi.createRequest()
                .method(ScmHttpMethod.GET)
                .withUrlPath(
                        BRANCHES_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("fullName", repoId)
                                .add("sha", urlEncode(sha))
                                .build()
                )
                .withRepoId(repoId)
                .fetch(GiteeCommitDetail.class);
    }
}
