package com.tencent.devops.scm.provider.git.gitee;

import com.tencent.devops.scm.api.RefService;
import com.tencent.devops.scm.api.pojo.BranchListOptions;
import com.tencent.devops.scm.api.pojo.Change;
import com.tencent.devops.scm.api.pojo.Commit;
import com.tencent.devops.scm.api.pojo.CommitListOptions;
import com.tencent.devops.scm.api.pojo.ListOptions;
import com.tencent.devops.scm.api.pojo.Reference;
import com.tencent.devops.scm.api.pojo.ReferenceInput;
import com.tencent.devops.scm.api.pojo.TagListOptions;
import com.tencent.devops.scm.api.pojo.repository.ScmProviderRepository;
import com.tencent.devops.scm.api.pojo.repository.git.GitScmProviderRepository;
import com.tencent.devops.scm.provider.git.gitee.auth.GiteeTokenAuthProviderAdapter;
import com.tencent.devops.scm.sdk.common.enums.SortOrder;
import com.tencent.devops.scm.sdk.gitee.GiteeApi;
import com.tencent.devops.scm.sdk.gitee.GiteeApiFactory;
import com.tencent.devops.scm.sdk.gitee.GiteeBranchesApi;
import com.tencent.devops.scm.sdk.gitee.GiteeTagApi;
import com.tencent.devops.scm.sdk.gitee.enums.GiteeBranchOrderBy;
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeBranch;
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeCommitCompare;
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeCommitDetail;
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeTag;
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeTagDetail;
import java.util.List;
import java.util.stream.Collectors;

public class GiteeRefService implements RefService {

    private final GiteeApiFactory apiFactory;

    public GiteeRefService(GiteeApiFactory apiFactory) {
        this.apiFactory = apiFactory;
    }

    @Override
    public void createBranch(ScmProviderRepository repository, ReferenceInput input) {

    }

    @Override
    public Reference findBranch(ScmProviderRepository repository, String name) {
        GitScmProviderRepository repo = (GitScmProviderRepository) repository;
        GiteeApi giteeApi = apiFactory.fromAuthProvider(GiteeTokenAuthProviderAdapter.get(repo.getAuth()));
        // 构建请求接口类
        GiteeBranchesApi branchesApi = giteeApi.getBranchesApi();
        GiteeBranch branch = branchesApi.getBranch(repo.getProjectIdOrPath(), name);
        return GiteeObjectConverter.convertBranches(branch);
    }

    @Override
    public List<Reference> listBranches(ScmProviderRepository repository, BranchListOptions opts) {
        GitScmProviderRepository repo = (GitScmProviderRepository) repository;
        GiteeApi giteeApi = apiFactory.fromAuthProvider(GiteeTokenAuthProviderAdapter.get(repo.getAuth()));
        // 构建请求接口类
        GiteeBranchesApi branchesApi = giteeApi.getBranchesApi();
        List<GiteeBranch> branches = branchesApi.getBranches(repo.getProjectIdOrPath());
        // 结果转化
        return branches.stream().map(GiteeObjectConverter::convertBranches).collect(Collectors.toList());
    }

    @Override
    public void createTag(ScmProviderRepository repository, ReferenceInput input) {

    }

    @Override
    public Reference findTag(ScmProviderRepository repository, String name) {
        GitScmProviderRepository repo = (GitScmProviderRepository) repository;
        GiteeApi giteeApi = apiFactory.fromAuthProvider(GiteeTokenAuthProviderAdapter.get(repo.getAuth()));
        // 构建请求接口类
        GiteeTagApi tagApi = giteeApi.getTagApi();
        GiteeTagDetail tag = tagApi.getTags(repo.getProjectIdOrPath(), name);
        return GiteeObjectConverter.convertTag(tag);
    }

    @Override
    public List<Reference> listTags(ScmProviderRepository repository, TagListOptions opts) {
        GitScmProviderRepository repo = (GitScmProviderRepository) repository;
        GiteeApi giteeApi = apiFactory.fromAuthProvider(GiteeTokenAuthProviderAdapter.get(repo.getAuth()));
        // 构建请求接口类
        GiteeTagApi tagApi = giteeApi.getTagApi();
        List<GiteeTag> tags = tagApi.getTags(
                repo.getProjectIdOrPath(),
                opts.getPage(),
                opts.getPageSize(),
                GiteeBranchOrderBy.valueOf(opts.getOrderBy()),
                SortOrder.valueOf(opts.getSort())
        );
        return tags.stream().map(GiteeObjectConverter::convertTag).collect(Collectors.toList());
    }

    @Override
    public Commit findCommit(ScmProviderRepository repository, String ref) {
        GitScmProviderRepository repo = (GitScmProviderRepository) repository;
        GiteeApi giteeApi = apiFactory.fromAuthProvider(GiteeTokenAuthProviderAdapter.get(repo.getAuth()));
        GiteeCommitDetail commit = giteeApi.getCommitApi().getCommit(repo.getProjectIdOrPath(), ref);
        return GiteeObjectConverter.convertCommit(commit);
    }

    @Override
    public List<Commit> listCommits(ScmProviderRepository repository, CommitListOptions opts) {
        return null;
    }

    @Override
    public List<Change> listChanges(ScmProviderRepository repository, String ref, ListOptions opts) {
        GitScmProviderRepository repo = (GitScmProviderRepository) repository;
        GiteeApi giteeApi = apiFactory.fromAuthProvider(GiteeTokenAuthProviderAdapter.get(repo.getAuth()));
        GiteeCommitDetail commit = giteeApi.getCommitApi().getCommit(
                repo.getProjectIdOrPath(),
                ref
        );
        return GiteeObjectConverter.convertChange(commit);
    }

    @Override
    public List<Change> compareChanges(
            ScmProviderRepository repository,
            String source,
            String target,
            ListOptions opts
    ) {
        GitScmProviderRepository repo = (GitScmProviderRepository) repository;
        GiteeApi giteeApi = apiFactory.fromAuthProvider(GiteeTokenAuthProviderAdapter.get(repo.getAuth()));
        GiteeCommitCompare commitCompare = giteeApi.getFileApi().commitCompare(
                repo.getProjectIdOrPath(),
                source,
                target,
                null
        );
        return GiteeObjectConverter.convertCompare(commitCompare);
    }
}
