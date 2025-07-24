package com.tencent.devops.scm.sdk.gitee;

import com.tencent.devops.scm.sdk.common.ScmHttpMethod;
import com.tencent.devops.scm.sdk.common.util.MapBuilder;
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeCommitCompare;

public class GiteeRepositoryFileApi extends AbstractGiteeApi {
    private static final String FILE_CHANGE_LIST_URI_PATTERN = "repos/:id/compare/:diff_ref";

    public GiteeRepositoryFileApi(GiteeApi tGitApi) {
        super(tGitApi);
    }

    /**
     * 对比提交文件变更信息
     * @param projectIdOrPath 仓库名
     */
    public GiteeCommitCompare commitCompare(
            Object projectIdOrPath,
            String to,
            String from,
            Boolean straight
    ) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        GiteeCommitCompare commitCompare = giteeApi.createRequest()
                .method(ScmHttpMethod.GET)
                .withUrlPath(
                        FILE_CHANGE_LIST_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .add("diff_ref", String.format("%s...%s", to, from))
                                .build()
                )
                .withRepoId(repoId)
                .with("straight", straight)
                .fetch(GiteeCommitCompare.class);
        return commitCompare;
    }
}
