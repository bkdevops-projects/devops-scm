package com.tencent.devops.scm.sdk.tsvn;

import com.tencent.devops.scm.sdk.common.ScmHttpMethod;
import com.tencent.devops.scm.sdk.common.util.MapBuilder;
import com.tencent.devops.scm.sdk.tsvn.pojo.TSvnCommit;
import com.tencent.devops.scm.sdk.tsvn.pojo.TSvnLockConfig;
import java.util.Arrays;
import java.util.List;

public class TSvnCommitApi extends AbstractTSvnApi {

    // 请求uri
    private static final String COMMIT_URI_PATTERN = "svn/projects/:id/commits";

    public TSvnCommitApi(TSvnApi tSvnApi) {
        super(tSvnApi);
    }

    /**
     * 获取提交列表
     */
    public List<TSvnCommit> getCommit(
            Object projectIdOrPath,
            String path,
            Integer perPage
    ) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        TSvnCommit[] commits = tSvnApi.createRequest()
                .method(ScmHttpMethod.GET)
                .withUrlPath(
                        COMMIT_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .build()
                )
                .withRepoId(repoId)
                .with("path", path)
                .with(PER_PAGE_PARAM, perPage)
                .fetch(TSvnCommit[].class);
        return Arrays.asList(commits);
    }
}
