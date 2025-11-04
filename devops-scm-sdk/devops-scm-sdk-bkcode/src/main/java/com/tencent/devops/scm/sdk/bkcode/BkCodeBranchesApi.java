package com.tencent.devops.scm.sdk.bkcode;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodePage;
import com.tencent.devops.scm.sdk.common.ScmHttpMethod;
import com.tencent.devops.scm.sdk.common.ScmRequest.Builder;
import com.tencent.devops.scm.sdk.common.enums.SortOrder;
import com.tencent.devops.scm.sdk.common.util.MapBuilder;
import com.tencent.devops.scm.sdk.bkcode.enums.BkCodeBranchOrderBy;
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeBranch;
import java.util.List;

public class BkCodeBranchesApi extends AbstractBkCodeApi {

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

    public BkCodeBranchesApi(BkCodeApi tGitApi) {
        super(tGitApi);
    }

    /**
     * 获取分支列表
     * @param projectIdOrPath 仓库名
     */
    public BkCodePage<BkCodeBranch> getBranches(Object projectIdOrPath) {
        return getBranches(projectIdOrPath, null, null, null);
    }

    /**
     * 获取分支列表
     * @param projectIdOrPath 仓库名
     * @param page 当前的页码
     * @param perPage 每页的数量，最大为 100
     */
    public BkCodePage<BkCodeBranch> getBranches(
            Object projectIdOrPath,
            String search,
            Integer page,
            Integer perPage
    ) {
        return getBranches(projectIdOrPath, search, page, perPage, null, null);
    }

    /**
     * 获取分支列表
     * @param projectIdOrPath 仓库名
     * @param page 当前的页码
     * @param perPage 每页的数量，最大为 100
     * @param orderBy 排序字段
     * @param sort 排序方向
     */
    public BkCodePage<BkCodeBranch> getBranches(
            Object projectIdOrPath,
            String search,
            Integer page,
            Integer perPage,
            BkCodeBranchOrderBy orderBy,
            SortOrder sort
    ) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        return  bkCodeApi.createRequest()
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
                .with("keyword", search)
                .with("sort", orderBy != null ? orderBy.toValue() : null)
                .with("direction", sort != null ? sort.getValue() : null)
                .fetch(new TypeReference<>() {});
    }

    /**
     * 获取分支详细信息
     * @param projectIdOrPath 仓库名
     */
    public BkCodeBranch getBranch(
            Object projectIdOrPath,
            String branch
    ) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        return bkCodeApi.createRequest()
                .method(ScmHttpMethod.GET)
                .withUrlPath(
                        BRANCHES_ID_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .add("branch", branch)
                                .build()
                )
                .withRepoId(repoId)
                .fetch(new TypeReference<>() {});
    }



    /**
     * 删除分支
     * @param projectIdOrPath 仓库名
     * @param branch 分支名
     */
    public void delBranch(
            Object projectIdOrPath,
            String branch
    ) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        bkCodeApi.createRequest()
                .method(ScmHttpMethod.DELETE)
                .withUrlPath(
                        BRANCHES_ID_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .add("branch", branch)
                                .build()
                )
                .withRepoId(repoId)
                .send();
    }

    /**
     * 创建分支
     * @param projectIdOrPath 仓库名
     */
    public BkCodeBranch createBranch(
            Object projectIdOrPath,
            String name,
            String ref,
            String desc
    ) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        return bkCodeApi.createRequest()
                .method(ScmHttpMethod.POST)
                .withUrlPath(
                        BRANCHES_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .build()
                )
                .withRepoId(repoId)
                .with("ref", ref)
                .with("name", name)
                .with("description", desc)
                .fetch(new TypeReference<>() {});
    }
}
