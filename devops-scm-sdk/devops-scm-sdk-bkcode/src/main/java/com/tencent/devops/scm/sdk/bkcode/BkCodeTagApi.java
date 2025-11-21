package com.tencent.devops.scm.sdk.bkcode;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodePage;
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeTag;
import com.tencent.devops.scm.sdk.common.ScmHttpMethod;
import com.tencent.devops.scm.sdk.common.ScmRequest.Builder;
import com.tencent.devops.scm.sdk.common.enums.SortOrder;
import com.tencent.devops.scm.sdk.common.util.MapBuilder;
import com.tencent.devops.scm.sdk.bkcode.enums.BkCodeBranchOrderBy;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

public class BkCodeTagApi extends AbstractBkCodeApi {

    /**
     * 分支请求uri
     * 作用: 组装请求连接，后续会携带到请求头[URI_PATTERN]里面，集成springboot后可用于上报度量信息
     * 参考：
     * @see Builder#withUriPattern(String)
     * com.tencent.devops.scm.spring.config.ScmConnectorConfiguration#okHttpMetricsEventListener
     * io.micrometer.core.instrument.binder.okhttp3.OkHttpMetricsEventListener.Builder#uriMapper
     */
    private static final String TAGS_URI_PATTERN = "repos/:id/tags";
    private static final String TAGS_ID_URI_PATTERN = "repos/:id/tags/:tag";

    public BkCodeTagApi(BkCodeApi bkCodeApi) {
        super(bkCodeApi);
    }

    /**
     * 获取分支列表
     * @param projectIdOrPath 仓库名
     */
    public BkCodePage<BkCodeTag> getTags(Object projectIdOrPath) {
        return getTags(projectIdOrPath, null, null, null);
    }

    /**
     * 获取Tag列表
     * @param projectIdOrPath 仓库名
     * @param page 当前的页码
     * @param perPage 每页的数量，最大为 100
     */
    public BkCodePage<BkCodeTag> getTags(
            Object projectIdOrPath,
            String search,
            Integer page,
            Integer perPage
    ) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        return bkCodeApi.createRequest()
                .method(ScmHttpMethod.GET)
                .withUrlPath(
                        TAGS_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .build()
                )
                .withRepoId(repoId)
                .with(PAGE_PARAM, page != null ? page : DEFAULT_PAGE)
                .with(PER_PAGE_PARAM, perPage != null ? perPage : DEFAULT_PER_PAGE)
                .with("tagNameKeyword", search)
                .fetch(new TypeReference<>() {});
    }

    /**
     * 获取指定tag详情
     * @param projectIdOrPath 仓库名
     */
    public BkCodeTag getTag(
            Object projectIdOrPath,
            String tagName
    ) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        return bkCodeApi.createRequest()
                .method(ScmHttpMethod.GET)
                .withUrlPath(
                        TAGS_ID_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .add("tag", tagName)
                                .build()
                )
                .withRepoId(repoId)
                .fetch(BkCodeTag.class);
    }
}
