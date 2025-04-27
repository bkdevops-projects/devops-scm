package com.tencent.devops.scm.provider.gitee.simple;

import com.gitee.sdk.gitee5j.ApiException;
import com.gitee.sdk.gitee5j.api.RepositoriesApi;
import com.gitee.sdk.gitee5j.model.Branch;
import com.gitee.sdk.gitee5j.model.CompleteBranch;
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
import java.util.List;
import java.util.stream.Collectors;

public class GiteeRefService implements RefService {

    private GiteeApiClientFactory giteeApiFactory;

    public GiteeRefService(GiteeApiClientFactory apiFactory) {
        this.giteeApiFactory = apiFactory;
    }

    @Override
    public void createBranch(ScmProviderRepository repository, ReferenceInput input) {

    }

    @Override
    public Reference findBranch(ScmProviderRepository repository, String name) {
        return GiteeApiTemplate.execute(
                repository,
                giteeApiFactory,
                (repoName, client) -> {
                    RepositoriesApi repositoriesApi = new RepositoriesApi(client);
                    CompleteBranch completeBranch = null;
                    try {
                        completeBranch = repositoriesApi.getReposOwnerRepoBranchesBranchWithHttpInfo(
                                repoName.getLeft(),
                                repoName.getRight(),
                                name
                        ).getData();
                    } catch (ApiException e) {
                        throw new RuntimeException(e);
                    }
                    return GiteeObjectConverter.convertBranches(completeBranch);
                }
        );
    }

    @Override
    public List<Reference> listBranches(ScmProviderRepository repository, BranchListOptions opts) {
        return GiteeApiTemplate.execute(
                repository,
                giteeApiFactory,
                (repoName, client) -> {
                    RepositoriesApi repositoriesApi = new RepositoriesApi(client);
                    List<Branch> branches = null;
                    try {
                        branches = repositoriesApi.getReposOwnerRepoBranchesWithHttpInfo(
                                repoName.getLeft(),
                                repoName.getRight(),
                                null,
                                null,
                                opts.getPage(),
                                opts.getPageSize()
                        ).getData();
                    } catch (ApiException e) {
                        throw new RuntimeException(e);
                    }
                    // 结果转化
                    return branches.stream().map(GiteeObjectConverter::convertBranches).collect(Collectors.toList());
                }
        );
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
