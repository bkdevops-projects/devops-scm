package com.tencent.devops.scm.sdk.gitee;

import com.tencent.devops.scm.sdk.common.ScmHttpMethod;
import com.tencent.devops.scm.sdk.common.util.MapBuilder;
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeProjectHook;
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeRepositoryDetail;
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeWebhookConfig;
import java.util.Arrays;
import java.util.List;

public class GiteeProjectApi extends AbstractGiteeApi {

    private static final String REPOSITORY_URI_PATTERN = "repos/:fullName";
    private static final String REPOSITORY_HOOK_URI_PATTERN = "repos/:fullName/hooks";
    private static final String REPOSITORY_HOOK_ID_URI_PATTERN = "repos/:fullName/hooks/:hookId";

    public GiteeProjectApi(GiteeApi tGitApi) {
        super(tGitApi);
    }

    /**
     * 获取用户的某个仓库
     *
     * @param projectIdOrPath 仓库名
     */
    public GiteeRepositoryDetail getProject(Object projectIdOrPath) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        GiteeRepositoryDetail repository = giteeApi.createRequest()
                .method(ScmHttpMethod.GET)
                .withUrlPath(
                        REPOSITORY_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("fullName", repoId)
                                .build()
                )
                .withRepoId(repoId)
                .fetch(GiteeRepositoryDetail.class);
        return repository;
    }


    public GiteeWebhookConfig addHook(Object projectIdOrPath, GiteeProjectHook enabledHooks) {
        return addHook(projectIdOrPath, enabledHooks, null);
    }

    public GiteeWebhookConfig addHook(Object projectIdOrPath, GiteeProjectHook enabledHooks, String secretToken) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        return giteeApi.createRequest()
                .method(ScmHttpMethod.POST)
                .withUrlPath(
                        REPOSITORY_HOOK_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("fullName", repoId)
                                .build()
                )
                .withRepoId(repoId)
                .with("url", enabledHooks.getUrl())
                .with("push_events", enabledHooks.getPushEvents())
                .with("tag_push_events", enabledHooks.getTagPushEvents())
                .with("issues_events", enabledHooks.getIssuesEvents())
                .with("merge_requests_events", enabledHooks.getMergeRequestsEvents())
                .with("note_events", enabledHooks.getNoteEvents())
                .with("review_events", enabledHooks.getReviewEvents())
                .with("encryption_type", 0)
                .with("password", secretToken)
                .fetch(GiteeWebhookConfig.class);
    }

    public GiteeWebhookConfig updateHook(
            Object projectIdOrPath,
            Long hookId,
            GiteeProjectHook enabledHooks,
            String secretToken
    ) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        return giteeApi.createRequest()
                .method(ScmHttpMethod.PATCH)
                .withUrlPath(
                        REPOSITORY_HOOK_ID_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("fullName", repoId)
                                .add("hookId", hookId.toString())
                                .build()
                )
                .withRepoId(repoId)
                .with("url", enabledHooks.getUrl())
                .with("push_events", enabledHooks.getPushEvents())
                .with("tag_push_events", enabledHooks.getTagPushEvents())
                .with("issues_events", enabledHooks.getIssuesEvents())
                .with("merge_requests_events", enabledHooks.getMergeRequestsEvents())
                .with("note_events", enabledHooks.getNoteEvents())
                .with("review_events", enabledHooks.getReviewEvents())
                .with("token", secretToken)
                .fetch(GiteeWebhookConfig.class);
    }

    public GiteeWebhookConfig getHook(Object projectIdOrPath, Long hookId) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        return giteeApi.createRequest()
                .method(ScmHttpMethod.GET)
                .withUrlPath(
                        REPOSITORY_HOOK_ID_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("fullName", repoId)
                                .add("hookId", hookId.toString())
                                .build()
                )
                .withRepoId(repoId)
                .fetch(GiteeWebhookConfig.class);
    }

    public List<GiteeWebhookConfig> getHooks(Object projectIdOrPath) {
        return getHooks(projectIdOrPath, DEFAULT_PAGE, DEFAULT_PER_PAGE);
    }

    public List<GiteeWebhookConfig> getHooks(Object projectIdOrPath,
            Integer page, Integer perPage) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        GiteeWebhookConfig[] data = giteeApi.createRequest()
                .method(ScmHttpMethod.GET)
                .withUrlPath(
                        REPOSITORY_HOOK_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("fullName", repoId)
                                .build()
                )
                .withRepoId(repoId)
                .with(PAGE_PARAM, page)
                .with(PER_PAGE_PARAM, perPage)
                .fetch(GiteeWebhookConfig[].class);
        return Arrays.asList(data);
    }

    public void deleteHook(Object projectIdOrPath, Long hookId) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        giteeApi.createRequest()
                .method(ScmHttpMethod.DELETE)
                .withUrlPath(
                        REPOSITORY_HOOK_ID_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("fullName", repoId)
                                .add("hookId", hookId.toString())
                                .build()
                )
                .withRepoId(repoId)
                .send();
    }
}
