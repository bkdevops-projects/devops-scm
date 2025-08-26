package com.tencent.devops.scm.sdk.tgit;

import com.tencent.devops.scm.sdk.common.ScmHttpMethod;
import com.tencent.devops.scm.sdk.common.util.MapBuilder;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitCheckRun;
import java.util.Arrays;
import java.util.List;

public class TGitCheckRunApi extends AbstractTGitApi {
    public static final String CHECK_RUN_URI_PATTERN = "projects/:id/commit/:sha/statuses";
    public static final String COMMIT_CHECK_RUN_URI_PATTERN = "projects/:id/commits/:sha/statuses";

    public TGitCheckRunApi(TGitApi tGitApi) {
        super(tGitApi);
    }

    public TGitCheckRun create(
            Object projectIdOrPath,
            String sha,
            TGitCheckRun status
    ) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        if (projectIdOrPath == null) {
            throw new IllegalArgumentException("projectIdOrPath cannot be null");
        }

        if (sha == null || sha.trim().isEmpty()) {
            throw new IllegalArgumentException("sha cannot be null");
        }
        return tGitApi.createRequest()
                .method(ScmHttpMethod.POST)
                .withUrlPath(
                        CHECK_RUN_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .add("sha", urlEncode(sha))
                                .build()
                )
                .withRepoId(repoId)
                .with("state", status.getState())
                .with("target_url", status.getTargetUrl())
                .with("description", status.getDescription())
                .with("context", status.getContext())
                .with("detail", status.getDetail())
                .with("block", status.getBlock())
                .with("target_branches", status.getTargetBranches())
                .fetch(TGitCheckRun.class);
    }


    public List<TGitCheckRun> getCheckRuns(
            Object projectIdOrPath,
            String sha,
            Integer page,
            Integer perPage
    ) {
        if (projectIdOrPath == null) {
            throw new IllegalArgumentException("projectIdOrPath cannot be null");
        }

        if (sha == null || sha.trim().isEmpty()) {
            throw new IllegalArgumentException("sha cannot be null");
        }
        String repoId = getProjectIdOrPath(projectIdOrPath);
        TGitCheckRun[] data = tGitApi.createRequest()
                .method(ScmHttpMethod.GET)
                .withUrlPath(
                        COMMIT_CHECK_RUN_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .add("sha", urlEncode(sha))
                                .build()
                )
                .withRepoId(repoId)
                .with(PAGE_PARAM, page)
                .with(PER_PAGE_PARAM, perPage)
                .fetch(TGitCheckRun[].class);
        return Arrays.asList(data);
    }
}
