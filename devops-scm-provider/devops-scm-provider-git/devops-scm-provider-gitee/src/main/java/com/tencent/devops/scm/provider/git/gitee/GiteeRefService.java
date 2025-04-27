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
import com.tencent.devops.scm.sdk.common.connector.okhttp3.OkHttpScmConnector;
import com.tencent.devops.scm.sdk.gitee.GiteeApi;
import com.tencent.devops.scm.sdk.gitee.GiteeApiFactory;
import com.tencent.devops.scm.sdk.gitee.GiteeBranchesApi;
import com.tencent.devops.scm.sdk.gitee.auth.GiteeTokenAuthProvider;
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeBranch;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;

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
        return null;
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
        return null;
    }

    @Override
    public List<Reference> listTags(ScmProviderRepository repository, TagListOptions opts) {
        return null;
    }

    @Override
    public Commit findCommit(ScmProviderRepository repository, String ref) {
        return null;
    }

    @Override
    public List<Commit> listCommits(ScmProviderRepository repository, CommitListOptions opts) {
        return null;
    }

    @Override
    public List<Change> listChanges(ScmProviderRepository repository, String ref, ListOptions opts) {
        return null;
    }

    @Override
    public List<Change> compareChanges(
            ScmProviderRepository repository,
            String source,
            String target,
            ListOptions opts
    ) {
        return null;
    }
}
