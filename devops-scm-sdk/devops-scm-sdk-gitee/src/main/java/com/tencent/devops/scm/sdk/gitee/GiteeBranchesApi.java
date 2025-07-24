package com.tencent.devops.scm.sdk.gitee;

import com.tencent.devops.scm.sdk.common.ScmHttpMethod;
import com.tencent.devops.scm.sdk.common.ScmRequest.Builder;
import com.tencent.devops.scm.sdk.common.enums.SortOrder;
import com.tencent.devops.scm.sdk.common.util.MapBuilder;
import com.tencent.devops.scm.sdk.gitee.enums.GiteeBranchOrderBy;
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeBranch;
import java.util.Arrays;
import java.util.List;

public class GiteeBranchesApi extends AbstractGiteeApi {

    /**
     * 分支请求uri
     * 作用: 组装请求连接，后续会携带到请求头[URI_PATTERN]里面，集成springboot后可用于上报度量信息
     * 参考：
     * @see Builder#withUriPattern(String)
     * com.tencent.devops.scm.spring.config.ScmConnectorConfiguration#okHttpMetricsEventListener
     * io.micrometer.core.instrument.binder.okhttp3.OkHttpMetricsEventListener.Builder#uriMapper
     */
    private static final String BRANCHES_URI_PATTERN = "repos/:id/branches";
    private static final String BRANCHES_ID_URI_PATTERN = "repos/:id/branches/:branch";

    public GiteeBranchesApi(GiteeApi tGitApi) {
        super(tGitApi);
    }

    /**
     * 获取分支列表
     * @param projectIdOrPath 仓库名
     */
    public List<GiteeBranch> getBranches(Object projectIdOrPath) {
        return getBranches(projectIdOrPath, null, null);
    }

    /**
     * 获取分支列表
     * @param projectIdOrPath 仓库名
     * @param page 当前的页码
     * @param perPage 每页的数量，最大为 100
     */
    public List<GiteeBranch> getBranches(
            Object projectIdOrPath,
            Integer page,
            Integer perPage
    ) {
        return getBranches(projectIdOrPath, page, perPage, null, null);
    }

    /**
     * 获取分支列表
     * @param projectIdOrPath 仓库名
     * @param page 当前的页码
     * @param perPage 每页的数量，最大为 100
     * @param orderBy 排序字段
     * @param sort 排序方向
     */
    public List<GiteeBranch> getBranches(
            Object projectIdOrPath,
            Integer page,
            Integer perPage,
            GiteeBranchOrderBy orderBy,
            SortOrder sort
    ) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        GiteeBranch[] branches = giteeApi.createRequest()
                .method(ScmHttpMethod.GET)
                .withUrlPath(
                        BRANCHES_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .build()
                )
                .withRepoId(repoId)
                .with(PAGE_PARAM, page)
                .with(PER_PAGE_PARAM, perPage)
                .with("sort", orderBy != null ? orderBy.toValue() : null)
                .with("direction", sort != null ? sort.getValue() : null)
                .fetch(GiteeBranch[].class);
        return Arrays.asList(branches);
    }

    /**
     * 获取分支详细信息
     * @param projectIdOrPath 仓库名
     */
    public GiteeBranch getBranch(
            Object projectIdOrPath,
            String branch
    ) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        return giteeApi.createRequest()
                .method(ScmHttpMethod.GET)
                .withUrlPath(
                        BRANCHES_ID_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .add("branch", branch)
                                .build()
                )
                .withRepoId(repoId)
                .fetch(GiteeBranch.class);
    }
}
