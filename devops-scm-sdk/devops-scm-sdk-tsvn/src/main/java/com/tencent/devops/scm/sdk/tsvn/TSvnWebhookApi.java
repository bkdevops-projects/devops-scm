package com.tencent.devops.scm.sdk.tsvn;

import com.tencent.devops.scm.sdk.common.ScmHttpMethod;
import com.tencent.devops.scm.sdk.common.util.MapBuilder;
import com.tencent.devops.scm.sdk.tsvn.pojo.TSvnWebHookConfig;
import java.util.Arrays;
import java.util.List;

public class TSvnWebhookApi extends AbstractTSvnApi {

    // 请求uri
    private static final String PROJECT_HOOK_URI_PATTERN = "svn/projects/:id/hooks";
    private static final String PROJECT_HOOK_ID_URI_PATTERN = "svn/projects/:id/hooks/:hook_id";

    public TSvnWebhookApi(TSvnApi tSvnApi) {
        super(tSvnApi);
    }

    /**
     * 获取分支列表
     */
    public List<TSvnWebHookConfig> listHook(
            Object projectIdOrPath,
            Integer page,
            Integer perPage
    ) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        TSvnWebHookConfig[] lockConfigs = tSvnApi.createRequest()
                .method(ScmHttpMethod.GET)
                .withUrlPath(
                        PROJECT_HOOK_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .build()
                )
                .withRepoId(repoId)
                .with(PAGE_PARAM, page)
                .with(PER_PAGE_PARAM, perPage)
                .fetch(TSvnWebHookConfig[].class);
        return Arrays.asList(lockConfigs);
    }

    public TSvnWebHookConfig addHook(
            Object projectIdOrPath,
            TSvnWebHookConfig config
    ) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        return tSvnApi.createRequest()
                .method(ScmHttpMethod.POST)
                .withUrlPath(
                        PROJECT_HOOK_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .build()
                )
                .withRepoId(repoId)
                .with("url", config.getUrl())
                .with("path", config.getPath())
                .with("svn_pre_commit_events", config.getSvnPreCommitEvents())
                .with("svn_post_commit_events", config.getSvnPostCommitEvents())
                .with("svn_pre_lock_events", config.getSvnPreLockEvents())
                .with("svn_post_lock_events", config.getSvnPostLockEvents())
                .fetch(TSvnWebHookConfig.class);
    }

    public TSvnWebHookConfig getHook(Object projectIdOrPath, Long hookId) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        return tSvnApi.createRequest()
                .method(ScmHttpMethod.GET)
                .withUrlPath(
                        PROJECT_HOOK_ID_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .add("hook_id", hookId.toString())
                                .build()
                )
                .withRepoId(repoId)
                .fetch(TSvnWebHookConfig.class);
    }

    public void delHook(Object projectIdOrPath, Long hookId) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        tSvnApi.createRequest()
                .method(ScmHttpMethod.DELETE)
                .withUrlPath(
                        PROJECT_HOOK_ID_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .add("hook_id", hookId.toString())
                                .build()
                )
                .withRepoId(repoId)
                .fetch(String.class);
    }

    public TSvnWebHookConfig editHook(Object projectIdOrPath, Long hookId, TSvnWebHookConfig config) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        return tSvnApi.createRequest()
                .method(ScmHttpMethod.PUT)
                .withUrlPath(
                        PROJECT_HOOK_ID_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .add("hook_id", hookId.toString())
                                .build()
                )
                .withRepoId(repoId)
                .with("url", config.getUrl())
                .with("path", config.getPath())
                .with("svn_pre_commit_events", config.getSvnPreCommitEvents())
                .with("svn_post_commit_events", config.getSvnPostCommitEvents())
                .with("svn_pre_lock_events", config.getSvnPreLockEvents())
                .with("svn_post_lock_events", config.getSvnPostLockEvents())
                .fetch(TSvnWebHookConfig.class);
    }
}
