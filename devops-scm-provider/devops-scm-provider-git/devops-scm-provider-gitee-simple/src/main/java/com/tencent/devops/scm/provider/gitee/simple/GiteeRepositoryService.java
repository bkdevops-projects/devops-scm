package com.tencent.devops.scm.provider.gitee.simple;

import com.gitee.sdk.gitee5j.ApiException;
import com.gitee.sdk.gitee5j.api.RepositoriesApi;
import com.gitee.sdk.gitee5j.model.Project;
import com.tencent.devops.scm.api.RepositoryService;
import com.tencent.devops.scm.api.pojo.Hook;
import com.tencent.devops.scm.api.pojo.HookInput;
import com.tencent.devops.scm.api.pojo.ListOptions;
import com.tencent.devops.scm.api.pojo.Perm;
import com.tencent.devops.scm.api.pojo.RepoListOptions;
import com.tencent.devops.scm.api.pojo.Status;
import com.tencent.devops.scm.api.pojo.StatusInput;
import com.tencent.devops.scm.api.pojo.auth.IScmAuth;
import com.tencent.devops.scm.api.pojo.repository.ScmProviderRepository;
import com.tencent.devops.scm.api.pojo.repository.ScmServerRepository;
import java.util.List;

public class GiteeRepositoryService implements RepositoryService {

    private final GiteeApiClientFactory giteeApiFactory;

    public GiteeRepositoryService(GiteeApiClientFactory apiFactory) {
        this.giteeApiFactory = apiFactory;
    }

    @Override
    public ScmServerRepository find(ScmProviderRepository repository) {
        return GiteeApiTemplate.execute(
                repository,
                giteeApiFactory,
                (repoName, client) -> {
                    RepositoriesApi repositoriesApi = new RepositoriesApi(client);
                    Project project = null;
                    try {
                        project = repositoriesApi.getReposOwnerRepoWithHttpInfo(
                                repoName.getLeft(),
                                repoName.getRight()
                        ).getData();
                    } catch (ApiException e) {
                        throw new RuntimeException(e);
                    }
                    // 结果转化
                    return GiteeObjectConverter.convertRepository(project);
                }
        );
    }

    @Override
    public Perm findPerms(ScmProviderRepository repository, String username) {
        return null;
    }

    @Override
    public List<ScmServerRepository> list(IScmAuth auth, RepoListOptions opts) {
        return null;
    }

    @Override
    public List<Hook> listHooks(ScmProviderRepository repository, ListOptions opts) {
        return null;
    }

    @Override
    public Hook createHook(ScmProviderRepository repository, HookInput input) {
        return null;
    }

    @Override
    public Hook updateHook(ScmProviderRepository repository, Long hookId, HookInput input) {
        return null;
    }

    @Override
    public Hook getHook(ScmProviderRepository repository, Long hookId) {
        return null;
    }

    @Override
    public void deleteHook(ScmProviderRepository repository, Long hookId) {
    }

    @Override
    public List<Status> listStatus(ScmProviderRepository repository, String ref, ListOptions opts) {
        return null;
    }

    @Override
    public Status createStatus(ScmProviderRepository repository, String ref, StatusInput input) {
        return null;
    }
}
