package com.tencent.devops.scm.sdk.gitee;

import com.tencent.devops.scm.sdk.common.ScmHttpMethod;
import com.tencent.devops.scm.sdk.common.ScmRequest.Builder;
import com.tencent.devops.scm.sdk.common.enums.SortOrder;
import com.tencent.devops.scm.sdk.common.util.MapBuilder;
import com.tencent.devops.scm.sdk.gitee.enums.GiteeBranchOrderBy;
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeTag;
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeTagDetail;
import java.util.Arrays;
import java.util.List;

public class GiteeTagApi extends AbstractGiteeApi {

    /**
     * 分支请求uri
     * 作用: 组装请求连接，后续会携带到请求头[URI_PATTERN]里面，集成springboot后可用于上报度量信息
     * 参考：
     * @see Builder#withUriPattern(String)
     * com.tencent.devops.scm.spring.config.ScmConnectorConfiguration#okHttpMetricsEventListener
     * io.micrometer.core.instrument.binder.okhttp3.OkHttpMetricsEventListener.Builder#uriMapper
     */
    private static final String TAGS_URI_PATTERN = "repos/:id/tags";
    private static final String TAGS_ID_URI_PATTERN = "repos/:id/releases/tags/:tag";

    public GiteeTagApi(GiteeApi giteeApi) {
        super(giteeApi);
    }

    /**
     * 获取分支列表
     * @param projectIdOrPath 仓库名
     */
    public List<GiteeTag> getTags(Object projectIdOrPath) {
        return getTags(projectIdOrPath, null, null);
    }

    /**
     * 获取Tag列表
     * @param projectIdOrPath 仓库名
     * @param page 当前的页码
     * @param perPage 每页的数量，最大为 100
     */
    public List<GiteeTag> getTags(
            Object projectIdOrPath,
            Integer page,
            Integer perPage
    ) {
        return getTags(projectIdOrPath, page, perPage, null, null);
    }

    /**
     * 获取Tag列表
     * @param projectIdOrPath 仓库名
     * @param page 当前的页码
     * @param perPage 每页的数量，最大为 100
     * @param orderBy 排序字段
     * @param sort 排序方向
     */
    public List<GiteeTag> getTags(
            Object projectIdOrPath,
            Integer page,
            Integer perPage,
            GiteeBranchOrderBy orderBy,
            SortOrder sort
    ) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        GiteeTag[] branches = giteeApi.createRequest()
                .method(ScmHttpMethod.GET)
                .withUrlPath(
                        TAGS_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .build()
                )
                .withRepoId(repoId)
                .with(PAGE_PARAM, page)
                .with(PER_PAGE_PARAM, perPage)
                .with("sort", orderBy != null ? orderBy.toValue() : null)
                .with("direction", sort != null ? sort.getValue() : null)
                .fetch(GiteeTag[].class);
        return Arrays.asList(branches);
    }

    /**
     * 获取指定tag详情
     * @param projectIdOrPath 仓库名
     */
    public GiteeTagDetail getTags(
            Object projectIdOrPath,
            String tagName
    ) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        return giteeApi.createRequest()
                .method(ScmHttpMethod.GET)
                .withUrlPath(
                        TAGS_ID_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .add("tag", tagName)
                                .build()
                )
                .withRepoId(repoId)
                .fetch(GiteeTagDetail.class);
    }
}
