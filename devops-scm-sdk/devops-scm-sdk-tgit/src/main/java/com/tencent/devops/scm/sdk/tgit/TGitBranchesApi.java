package com.tencent.devops.scm.sdk.tgit;

import com.tencent.devops.scm.sdk.common.ScmHttpMethod;
import com.tencent.devops.scm.sdk.common.util.MapBuilder;
import com.tencent.devops.scm.sdk.tgit.enums.TGitBranchOrderBy;
import com.tencent.devops.scm.sdk.tgit.enums.TGitSortOrder;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitBranch;
import java.util.Arrays;
import java.util.List;

public class TGitBranchesApi extends AbstractTGitApi {

    // 分支请求uri
    private static final String BRANCHES_URI_PATTERN = "projects/:id/repository/branches";
    private static final String BRANCHES_ID_URI_PATTERN = "projects/:id/repository/branches/:branch";

    public TGitBranchesApi(TGitApi tGitApi) {
        super(tGitApi);
    }

    /**
     * 获取分支列表
     */
    public List<TGitBranch> getBranches(Object projectIdOrPath, String search) {
        return getBranches(projectIdOrPath, search, null, null);
    }

    /**
     * 获取分支列表
     */
    public List<TGitBranch> getBranches(Object projectIdOrPath, String search, Integer page, Integer perPage) {
        return getBranches(projectIdOrPath, search, page, perPage, null, null);
    }

    /**
     * 获取分支列表
     */
    public List<TGitBranch> getBranches(Object projectIdOrPath, String search, Integer page, Integer perPage,
            TGitBranchOrderBy orderBy, TGitSortOrder sort) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        TGitBranch[] branches = tGitApi.createRequest()
                .method(ScmHttpMethod.GET)
                .withUrlPath(
                        BRANCHES_URI_PATTERN,
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
                .fetch(TGitBranch[].class);
        return Arrays.asList(branches);
    }

    /**
     * 获取分支详情
     * @param projectIdOrPath 项目 ID 或 项目全路径 project_full_path
     * @param branch 分支名
     */
    public TGitBranch getBranch(Object projectIdOrPath, String branch) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        return tGitApi.createRequest()
                .method(ScmHttpMethod.GET)
                .withUrlPath(
                        BRANCHES_ID_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .add("branch", urlEncode(branch))
                                .build()
                )
                .withRepoId(repoId)
                .fetch(TGitBranch.class);
    }

    /**
     * 创建分支
     */
    public TGitBranch createBranch(Object projectIdOrPath, String branchName, String ref) {
        return createBranch(projectIdOrPath, branchName, ref, null);
    }

    public TGitBranch createBranch(Object projectIdOrPath, String branchName, String ref, String description) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        return tGitApi.createRequest()
                .method(ScmHttpMethod.POST)
                .withUrlPath(
                        BRANCHES_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .build()
                )
                .withRepoId(repoId)
                .with("branch_name", branchName)
                .with("ref", ref)
                .with("description", description)
                .fetch(TGitBranch.class);
    }

    /**
     * 删除分支
     */
    public void deleteBranch(Object projectIdOrPath, String branch) {
        String repoId = getProjectIdOrPath(projectIdOrPath);
        tGitApi.createRequest()
                .method(ScmHttpMethod.DELETE)
                .withUrlPath(
                        BRANCHES_ID_URI_PATTERN,
                        MapBuilder.<String, String>newBuilder()
                                .add("id", repoId)
                                .add("branch", urlEncode(branch))
                                .build()
                )
                .withRepoId(repoId)
                .send();
    }
}
