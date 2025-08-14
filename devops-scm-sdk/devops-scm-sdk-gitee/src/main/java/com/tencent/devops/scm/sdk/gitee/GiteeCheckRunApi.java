package com.tencent.devops.scm.sdk.gitee;

import static com.tencent.devops.scm.sdk.common.util.ScmJsonUtil.getJsonFactory;

import com.tencent.devops.scm.sdk.common.ScmHttpMethod;
import com.tencent.devops.scm.sdk.common.util.MapBuilder;
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeCheckRun;
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeCheckRunResult;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class GiteeCheckRunApi extends AbstractGiteeApi {
    private static final String CHECK_RUN_URI_PATTERN = "repos/:id/check-runs";
    private static final String CHECK_RUN_ID_URI_PATTERN = "repos/:id/check-runs/:check_run_id";
    private static final String COMMIT_CHECK_RUN_URI_PATTERN = "repos/:id/commits/:ref/check-runs";

    public GiteeCheckRunApi(GiteeApi tGitApi) {
        super(tGitApi);
    }

    /**
     * 创建检查项
     * @param projectIdOrPath 仓库名
     */
    public GiteeCheckRun create(Object projectIdOrPath, GiteeCheckRun checkRun) throws IOException {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        return giteeApi.createRequest()
                .method(ScmHttpMethod.POST)
                .withUrlPath(
                        CHECK_RUN_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .build()
                )
                .withRepoId(repoId)
                .with(getJsonFactory().toJson(checkRun))
                .fetch(GiteeCheckRun.class);
    }

    /**
     * 更新检查项
     * @param projectIdOrPath 仓库名
     */
    public GiteeCheckRun update(
            Object projectIdOrPath,
            Long checkRunId,
            GiteeCheckRun checkRun
    ) throws IOException {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        return giteeApi.createRequest()
                .method(ScmHttpMethod.PATCH)
                .withUrlPath(
                        CHECK_RUN_ID_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .add("check_run_id", checkRunId.toString())
                                .build()
                )
                .withRepoId(repoId)
                .with(new ByteArrayInputStream(getJsonFactory().writeValueAsBytes(checkRun)))
                .fetch(GiteeCheckRun.class);
    }

    /**
     * 更新检查项
     * @param projectIdOrPath 仓库名
     */
    public GiteeCheckRun getCheckRun(
            Object projectIdOrPath,
            Long checkRunId
    ) throws IOException {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        return giteeApi.createRequest()
                .method(ScmHttpMethod.GET)
                .withUrlPath(
                        CHECK_RUN_ID_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .add("check_run_id", checkRunId.toString())
                                .build()
                )
                .withRepoId(repoId)
                .fetch(GiteeCheckRun.class);
    }

    /**
     * 获取某个提交的检查项
     * @param projectIdOrPath 仓库名
     * @param ref commit sha
     * @param pullRequestId 创建check-run时若没有指定PrId，则获取列表时也无需携带PrId，反之则必须携带PrId
     */
    public List<GiteeCheckRun> getCheckRuns(
            Object projectIdOrPath,
            String ref,
            Long pullRequestId,
            Integer page,
            Integer perPage
    ) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        GiteeCheckRunResult checkRunsResult = giteeApi.createRequest()
                .method(ScmHttpMethod.GET)
                .withUrlPath(
                        COMMIT_CHECK_RUN_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .add("ref", ref)
                                .build()
                )
                .withRepoId(repoId)
                .with("pull_request_id", pullRequestId)
                .with(PAGE_PARAM, page)
                .with(PER_PAGE_PARAM, perPage)
                .fetch(GiteeCheckRunResult.class);
        return checkRunsResult.getCheckRuns();
    }
}
