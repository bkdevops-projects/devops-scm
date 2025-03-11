package com.tencent.devops.scm.sdk.tgit;

import com.tencent.devops.scm.sdk.common.ScmHttpMethod;
import com.tencent.devops.scm.sdk.common.util.MapBuilder;
import com.tencent.devops.scm.sdk.tgit.enums.TGitSortOrder;
import com.tencent.devops.scm.sdk.tgit.enums.TGitTagOrderBy;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitTag;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class TGitTagsApi extends AbstractTGitApi {

    // 分支请求uri
    private static final String TAGS_URI_PATTERN = "projects/:id/repository/tags";
    private static final String TAGS_ID_URI_PATTERN = "projects/:id/repository/tags/:tag";

    public TGitTagsApi(TGitApi tGitApi) {
        super(tGitApi);
    }

    public List<TGitTag> getTags(Object projectIdOrPath) {
        return getTags(projectIdOrPath, null);
    }

    public List<TGitTag> getTags(Object projectIdOrPath, String search) {
        return getTags(projectIdOrPath, search, null, null);
    }

    public List<TGitTag> getTags(Object projectIdOrPath, String search, Integer page, Integer perPage) {
        return getTags(projectIdOrPath, search, page, perPage, null, null);
    }

    /**
     * 获取tag列表
     */
    public List<TGitTag> getTags(Object projectIdOrPath, String search, Integer page, Integer perPage,
            TGitTagOrderBy orderBy, TGitSortOrder sort) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        TGitTag[] tags = tGitApi.createRequest()
                .method(ScmHttpMethod.GET)
                .withUrlPath(
                        TAGS_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .build()
                )
                .withRepoId(repoId)
                .with("search", search)
                .with(PAGE_PARAM, page)
                .with(PER_PAGE_PARAM, perPage)
                .with("order_by", orderBy != null ? orderBy.toValue() : null)
                .with("sort", sort != null ? sort.toValue() : null)
                .fetch(TGitTag[].class);
        return Arrays.asList(tags);
    }

    /**
     * 获取分支详情
     *
     * @param projectIdOrPath 项目 ID 或 项目全路径 project_full_path
     * @param tagName tag名字
     */
    public TGitTag getTag(Object projectIdOrPath, String tagName) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        return tGitApi.createRequest()
                .method(ScmHttpMethod.GET)
                .withUrlPath(
                        TAGS_ID_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .add("tag", urlEncode(tagName))
                                .build()
                )
                .withRepoId(repoId)
                .fetch(TGitTag.class);
    }

    public Optional<TGitTag> getOptionalTag(Object projectIdOrPath, String tagName) {
        Optional<TGitTag> optionalTag;
        try {
            optionalTag = Optional.ofNullable(getTag(projectIdOrPath, tagName));
        } catch (TGitApiException ignored) {
            optionalTag = Optional.empty();
        }
        return optionalTag;
    }

    public TGitTag createTag(Object projectIdOrPath, String tagName, String ref) {
        return createTag(projectIdOrPath, tagName, ref, null);
    }

    public TGitTag createTag(Object projectIdOrPath, String tagName, String ref, String message) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        return tGitApi.createRequest()
                .method(ScmHttpMethod.POST)
                .withUrlPath(
                        TAGS_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .build()
                )
                .withRepoId(repoId)
                .with("tag_name", tagName)
                .with("ref", ref)
                .with("message", message)
                .fetch(TGitTag.class);
    }

    /**
     * 删除tag
     */
    public void deleteTag(Object projectIdOrPath, String tagName) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        tGitApi.createRequest()
                .method(ScmHttpMethod.DELETE)
                .withUrlPath(
                        TAGS_ID_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .add("tag", urlEncode(tagName))
                                .build()
                )
                .withRepoId(repoId)
                .send();
    }
}
