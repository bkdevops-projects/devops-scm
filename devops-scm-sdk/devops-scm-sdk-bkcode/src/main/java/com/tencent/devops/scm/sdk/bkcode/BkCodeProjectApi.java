package com.tencent.devops.scm.sdk.bkcode;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tencent.devops.scm.sdk.bkcode.enums.BkCodeRepoOrderField;
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeMember;
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodePage;
import com.tencent.devops.scm.sdk.common.ScmHttpMethod;
import com.tencent.devops.scm.sdk.common.util.MapBuilder;
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeProjectHookInput;
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeRepositoryDetail;
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeWebhookConfig;
import java.util.Arrays;
import java.util.List;

public class BkCodeProjectApi extends AbstractBkCodeApi {

    private static final String REPOSITORY_URI_PATTERN = "user/repos";
    private static final String REPOSITORY_ID_URI_PATTERN = "repos/:id";
    private static final String REPOSITORY_HOOK_URI_PATTERN = "repos/:id/webhooks";
    private static final String REPOSITORY_HOOK_ID_URI_PATTERN = "repos/:id/webhooks/:hook_id";
    private static final String REPOSITORY_MEMBER_ALL_URI_PATTERN = "repos/:id/members/all";

    public BkCodeProjectApi(BkCodeApi tGitApi) {
        super(tGitApi);
    }

    /**
     * 获取用户的某个仓库
     *
     * @param projectIdOrPath 仓库名
     */
    public BkCodeRepositoryDetail getProject(Object projectIdOrPath) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        return bkCodeApi.createRequest()
                .method(ScmHttpMethod.GET)
                .withUrlPath(
                        REPOSITORY_ID_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .build()
                )
                .withRepoId(repoId)
                .fetch(BkCodeRepositoryDetail.class);
    }

    /**
     * 列出授权用户的所有仓库
     */
    public BkCodePage<BkCodeRepositoryDetail> getProjects(
            String search,
            Integer page,
            Integer perPage
    ) {
        return getProjects(search,null, null, null, page, perPage);
    }

    /**
     * 列出授权用户的所有仓库
     */
    public BkCodePage<BkCodeRepositoryDetail> getProjects(
            String search,
            String groupId,
            BkCodeRepoOrderField sortField,
            SortType sortType,
            Integer page,
            Integer perPage
    ) {
        return bkCodeApi.createRequest()
                .method(ScmHttpMethod.GET)
                .withUrlPath(REPOSITORY_URI_PATTERN)
                .with("keyword", search)
                .with("groupId", groupId)
                .with("sortField", sortField != null ? sortField.toValue() : null)
                .with("sortDirection", sortType != null ? sortType.name() : null)
                .with(PAGE_PARAM, page)
                .with(PER_PAGE_PARAM, perPage == null ? DEFAULT_PER_PAGE : perPage)
                .fetch(new TypeReference<>() {});
    }

    public BkCodeWebhookConfig addHook(
            Object projectIdOrPath,
            BkCodeProjectHookInput enabledHooks
    ) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        return bkCodeApi.createRequest()
                .method(ScmHttpMethod.POST)
                .withUrlPath(
                        REPOSITORY_HOOK_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .build()
                )
                .withRepoId(repoId)
                .with("url", enabledHooks.getUrl())
                .with("name", enabledHooks.getName())
                .with("token", enabledHooks.getToken())
                .with("events", enabledHooks.getEvents())
                .with("branchPattern", enabledHooks.getBranchPattern())
                .with("enabled", enabledHooks.getEnabled() != null ? enabledHooks.getEnabled() : true)
                .with("description", enabledHooks.getDescription())
                .fetch(BkCodeWebhookConfig.class);
    }

    public BkCodeWebhookConfig updateHook(
            Object projectIdOrPath,
            Long hookId,
            BkCodeProjectHookInput enabledHooks
    ) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        return bkCodeApi.createRequest()
                .method(ScmHttpMethod.PUT)
                .withUrlPath(
                        REPOSITORY_HOOK_ID_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .add("hook_id", hookId.toString())
                                .build()
                )
                .withRepoId(repoId)
                .with("url", enabledHooks.getUrl())
                .with("name", enabledHooks.getName())
                .with("token", enabledHooks.getToken())
                .with("events", enabledHooks.getEvents())
                .with("branchPattern", enabledHooks.getBranchPattern())
                .with("enabled", enabledHooks.getEnabled())
                .with("description", enabledHooks.getDescription())
                .fetch(BkCodeWebhookConfig.class);
    }

    public BkCodeWebhookConfig getHook(Object projectIdOrPath, Long hookId) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        return bkCodeApi.createRequest()
                .method(ScmHttpMethod.GET)
                .withUrlPath(
                        REPOSITORY_HOOK_ID_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .add("hook_id", hookId.toString())
                                .build()
                )
                .withRepoId(repoId)
                .fetch(BkCodeWebhookConfig.class);
    }

    public BkCodePage<BkCodeWebhookConfig> getHooks(Object projectIdOrPath) {
        return getHooks(projectIdOrPath, DEFAULT_PAGE, DEFAULT_PER_PAGE);
    }

    public BkCodePage<BkCodeWebhookConfig> getHooks(Object projectIdOrPath,
            Integer page, Integer perPage) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        return bkCodeApi.createRequest()
                .method(ScmHttpMethod.GET)
                .withUrlPath(
                        REPOSITORY_HOOK_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .build()
                )
                .withRepoId(repoId)
                .with(PAGE_PARAM, page)
                .with(PER_PAGE_PARAM, perPage)
                .fetch(new TypeReference<>() {});
    }

    public void deleteHook(Object projectIdOrPath, Long hookId) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        bkCodeApi.createRequest()
                .method(ScmHttpMethod.DELETE)
                .withUrlPath(
                        REPOSITORY_HOOK_ID_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .add("hook_id", hookId.toString())
                                .build()
                )
                .withRepoId(repoId)
                .send();
    }

    public List<BkCodeMember> getAllMembers(Object projectIdOrPath, String query, Integer page, Integer perPage) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        BkCodeMember[] data = bkCodeApi.createRequest()
                .method(ScmHttpMethod.GET)
                .withUrlPath(
                        REPOSITORY_MEMBER_ALL_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .build()
                )
                .withRepoId(repoId)
                .with(PAGE_PARAM, page)
                .with(PER_PAGE_PARAM, perPage)
                .with("query", query)
                .fetch(BkCodeMember[].class);
        return Arrays.asList(data);
    }
}
