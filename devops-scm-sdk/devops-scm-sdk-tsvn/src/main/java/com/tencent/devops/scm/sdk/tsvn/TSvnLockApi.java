package com.tencent.devops.scm.sdk.tsvn;

import com.tencent.devops.scm.sdk.common.ScmHttpMethod;
import com.tencent.devops.scm.sdk.common.util.MapBuilder;
import com.tencent.devops.scm.sdk.tsvn.pojo.TSvnLockConfig;
import java.util.Arrays;
import java.util.List;

public class TSvnLockApi extends AbstractTSvnApi {

    // 请求uri
    private static final String LOCK_URI_PATTERN = "svn/projects/:id/locks";
    private static final String ADD_LOCK_URI_PATTERN = "svn/projects/:id/locks/lock";
    private static final String UNLOCK_URI_PATTERN = "svn/projects/:id/locks";

    public TSvnLockApi(TSvnApi tSvnApi) {
        super(tSvnApi);
    }

    /**
     * 获取分支列表
     */
    public List<TSvnLockConfig> getLocks(
            Object projectIdOrPath,
            String path,
            String user,
            Integer page,
            Integer perPage
    ) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        TSvnLockConfig[] lockConfigs = tSvnApi.createRequest()
                .method(ScmHttpMethod.GET)
                .withUrlPath(
                        LOCK_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .build()
                )
                .withRepoId(repoId)
                .with("path", path)
                .with("user", user)
                .with(PAGE_PARAM, page)
                .with(PER_PAGE_PARAM, perPage)
                .fetch(TSvnLockConfig[].class);
        return Arrays.asList(lockConfigs);
    }

    public Boolean lock(Object projectIdOrPath, String path, String comment, Boolean force) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        return tSvnApi.createRequest()
                .method(ScmHttpMethod.PUT)
                .withUrlPath(
                        ADD_LOCK_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .build()
                )
                .withRepoId(repoId)
                .with("path", path)
                .with("comment", comment)
                .with("force", force)
                .fetch(Boolean.class);
    }

    public Boolean unLock(Object projectIdOrPath, String path, String comment, Boolean force) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        return tSvnApi.createRequest()
                .method(ScmHttpMethod.PUT)
                .withUrlPath(
                        UNLOCK_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .build()
                )
                .withRepoId(repoId)
                .with("path", path)
                .with("force", force)
                .fetch(Boolean.class);
    }
}
