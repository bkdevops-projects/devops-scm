package com.tencent.devops.scm.sdk.tgit;

import com.tencent.devops.scm.sdk.common.ScmHttpMethod;
import com.tencent.devops.scm.sdk.common.util.MapBuilder;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitCommit;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitCommitStatus;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitDiff;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class TGitCommitsApi extends AbstractTGitApi {

    // 分支请求uri
    private static final String COMMITS_URI_PATTERN = "projects/:id/repository/commits";
    private static final String COMMITS_ID_URI_PATTERN = "projects/:id/repository/commits/:sha";
    private static final String COMMITS_DIFF_URI_PATTERN = "projects/:id/repository/commits/:sha/diff";
    private static final String COMMITS_STATUSES_URI_PATTERN = "projects/:id/commits/:sha/statuses";

    public TGitCommitsApi(TGitApi tGitApi) {
        super(tGitApi);
    }

    /**
     * 返回项目版本库某个特定的提交
     *
     * @param projectIdOrPath 项目 ID 或 项目全路径 project_full_path
     * @param sha commit hash 值、分支名或 tag
     */
    public TGitCommit getCommit(Object projectIdOrPath, String sha) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        return tGitApi.createRequest()
                .method(ScmHttpMethod.GET)
                .withUrlPath(
                        COMMITS_ID_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .add("sha", urlEncode(sha))
                                .build()
                )
                .withRepoId(repoId)
                .fetch(TGitCommit.class);
    }

    public List<TGitDiff> getDiff(Object projectIdOrPath, String sha) {
        return getDiff(projectIdOrPath, sha, null);
    }

    public List<TGitDiff> getDiff(Object projectIdOrPath, String sha, String path) {
        return getDiff(projectIdOrPath, sha, path, null);
    }

    /**
     * 取得提交的差异
     *
     * @param projectIdOrPath 项目 ID 或 项目全路径 project_full_path
     * @param sha commit hash 值、分支名或 tag
     * @param path 可选, 文件路径
     * @param ignoreWhiteSpace 有差异的内容是否忽略空白符，默认不忽略
     */
    public List<TGitDiff> getDiff(Object projectIdOrPath, String sha, String path, Boolean ignoreWhiteSpace) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        TGitDiff[] list = tGitApi.createRequest()
                .method(ScmHttpMethod.GET)
                .withUrlPath(
                        COMMITS_DIFF_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .add("sha", urlEncode(sha))
                                .build()
                )
                .withRepoId(repoId)
                .with("path", path)
                .with("ignore_white_space", ignoreWhiteSpace)
                .fetch(TGitDiff[].class);
        return Arrays.asList(list);
    }

    public List<TGitCommit> getCommits(Object projectIdOrPath, String refName, String path,
            Integer page, Integer perPage) {
        return getCommits(projectIdOrPath, refName, path, null, null, page, perPage);
    }

    public List<TGitCommit> getCommits(Object projectIdOrPath, String refName, String path, Date since, Date until,
            Integer page, Integer perPage) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        TGitCommit[] data = tGitApi.createRequest()
                .method(ScmHttpMethod.GET)
                .withUrlPath(
                        COMMITS_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .build()
                )
                .withRepoId(repoId)
                .with("ref_name", refName == null ? null : urlEncode(refName))
                .with("path", path)
                .with("since", since)
                .with("until", until)
                .with(PAGE_PARAM, page)
                .with(PER_PAGE_PARAM, perPage)
                .fetch(TGitCommit[].class);
        return Arrays.asList(data);
    }

    public List<TGitCommitStatus> getCommitStatuses(
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
        TGitCommitStatus[] data = tGitApi.createRequest()
                .method(ScmHttpMethod.GET)
                .withUrlPath(
                        COMMITS_STATUSES_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .add("sha", urlEncode(sha))
                                .build()
                )
                .withRepoId(repoId)
                .with(PAGE_PARAM, page)
                .with(PER_PAGE_PARAM, perPage)
                .fetch(TGitCommitStatus[].class);
        return Arrays.asList(data);
    }

    public TGitCommitStatus addCommitStatus(
            Object projectIdOrPath,
            String sha,
            TGitCommitStatus status
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
                        COMMITS_STATUSES_URI_PATTERN,
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
                .fetch(TGitCommitStatus.class);
    }
}
