package com.tencent.devops.scm.sdk.tgit;

import com.tencent.devops.scm.sdk.common.ScmHttpMethod;
import com.tencent.devops.scm.sdk.common.util.MapBuilder;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitCompareResults;

public class TGitRepositoryApi extends AbstractTGitApi {
    private static final String PROJECT_REPOSITORY_COMPARE_URI_PATTERN = "projects/:id/repository/compare";

    public TGitRepositoryApi(TGitApi tGitApi) {
        super(tGitApi);
    }

    public TGitCompareResults compare(Object projectIdOrPath, String from, String to, boolean straight, String path) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        return tGitApi.createRequest()
                .method(ScmHttpMethod.GET)
                .withUrlPath(
                        PROJECT_REPOSITORY_COMPARE_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .build()
                )
                .withRepoId(repoId)
                .with("from", from)
                .with("to", to)
                .with("straight", straight)
                .with("path", path)
                .fetch(TGitCompareResults.class);
    }
}
