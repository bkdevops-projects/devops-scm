package com.tencent.devops.scm.provider.git.gitee;

import com.tencent.devops.scm.api.RepositoryService;
import com.tencent.devops.scm.api.pojo.Hook;
import com.tencent.devops.scm.api.pojo.HookEvents;
import com.tencent.devops.scm.api.pojo.HookInput;
import com.tencent.devops.scm.api.pojo.ListOptions;
import com.tencent.devops.scm.api.pojo.Perm;
import com.tencent.devops.scm.api.pojo.RepoListOptions;
import com.tencent.devops.scm.api.pojo.Status;
import com.tencent.devops.scm.api.pojo.StatusInput;
import com.tencent.devops.scm.api.pojo.auth.IScmAuth;
import com.tencent.devops.scm.api.pojo.repository.ScmProviderRepository;
import com.tencent.devops.scm.api.pojo.repository.ScmServerRepository;
import com.tencent.devops.scm.api.pojo.repository.git.GitScmProviderRepository;
import com.tencent.devops.scm.provider.git.gitee.auth.GiteeTokenAuthProviderAdapter;
import com.tencent.devops.scm.sdk.gitee.GiteeApi;
import com.tencent.devops.scm.sdk.gitee.GiteeApiFactory;
import com.tencent.devops.scm.sdk.gitee.GiteeProjectApi;
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeProjectHook;
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeRepositoryDetail;
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeWebhookConfig;
import java.util.List;
import java.util.stream.Collectors;

public class GiteeRepositoryService implements RepositoryService {
    private final GiteeApiFactory apiFactory;

    public GiteeRepositoryService(GiteeApiFactory apiFactory) {
        this.apiFactory = apiFactory;
    }

    @Override
    public ScmServerRepository find(ScmProviderRepository repository) {
        GitScmProviderRepository repo = (GitScmProviderRepository) repository;
        GiteeApi giteeApi = apiFactory.fromAuthProvider(GiteeTokenAuthProviderAdapter.get(repo.getAuth()));
        // 构建请求接口类
        GiteeProjectApi projectApi = giteeApi.getProjectApi();
        GiteeRepositoryDetail repositoryDetail = projectApi.getProject(repo.getProjectIdOrPath());
        // 结果转化
        return GiteeObjectConverter.convertRepository(repositoryDetail);
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
        GitScmProviderRepository repo = (GitScmProviderRepository) repository;
        GiteeApi giteeApi = apiFactory.fromAuthProvider(GiteeTokenAuthProviderAdapter.get(repo.getAuth()));
        // 构建请求接口类
        GiteeProjectApi projectApi = giteeApi.getProjectApi();
        List<GiteeWebhookConfig> projectApiHooks = projectApi.getHooks(
                repo.getProjectIdOrPath(),
                opts.getPage(),
                opts.getPageSize()
        );
        return projectApiHooks.stream()
                .map(GiteeObjectConverter::convertHook)
                .collect(Collectors.toList());
    }

    @Override
    public Hook createHook(ScmProviderRepository repository, HookInput input) {
        GitScmProviderRepository repo = (GitScmProviderRepository) repository;
        GiteeApi giteeApi = apiFactory.fromAuthProvider(GiteeTokenAuthProviderAdapter.get(repo.getAuth()));
        // 构建请求接口类
        GiteeProjectApi projectApi = giteeApi.getProjectApi();
        GiteeWebhookConfig projectApiHook = projectApi.addHook(
                repo.getProjectIdOrPath(),
                convertFromHookInput(input),
                input.getSecret()
        );
        return GiteeObjectConverter.convertHook(projectApiHook);
    }

    @Override
    public Hook updateHook(ScmProviderRepository repository, Long hookId, HookInput input) {
        GitScmProviderRepository repo = (GitScmProviderRepository) repository;
        GiteeApi giteeApi = apiFactory.fromAuthProvider(GiteeTokenAuthProviderAdapter.get(repo.getAuth()));
        // 构建请求接口类
        GiteeProjectApi projectApi = giteeApi.getProjectApi();
        GiteeWebhookConfig projectApiHook = projectApi.updateHook(
                repo.getProjectIdOrPath(),
                hookId,
                convertFromHookInput(input),
                input.getSecret()
        );
        return GiteeObjectConverter.convertHook(projectApiHook);
    }

    @Override
    public Hook getHook(ScmProviderRepository repository, Long hookId) {
        GitScmProviderRepository repo = (GitScmProviderRepository) repository;
        GiteeApi giteeApi = apiFactory.fromAuthProvider(GiteeTokenAuthProviderAdapter.get(repo.getAuth()));
        // 构建请求接口类
        GiteeProjectApi projectApi = giteeApi.getProjectApi();
        GiteeWebhookConfig projectApiHook = projectApi.getHook(
                repo.getProjectIdOrPath(),
                hookId
        );
        return GiteeObjectConverter.convertHook(projectApiHook);
    }

    @Override
    public void deleteHook(ScmProviderRepository repository, Long hookId) {
        GitScmProviderRepository repo = (GitScmProviderRepository) repository;
        GiteeApi giteeApi = apiFactory.fromAuthProvider(GiteeTokenAuthProviderAdapter.get(repo.getAuth()));
        // 构建请求接口类
        GiteeProjectApi projectApi = giteeApi.getProjectApi();
        projectApi.deleteHook(
                repo.getProjectIdOrPath(),
                hookId
        );
    }

    @Override
    public List<Status> listStatus(ScmProviderRepository repository, String ref, ListOptions opts) {
        return null;
    }

    @Override
    public Status createStatus(ScmProviderRepository repository, String ref, StatusInput input) {
        return null;
    }

    private GiteeProjectHook convertFromHookInput(HookInput input) {
        GiteeProjectHook.GiteeProjectHookBuilder builder = GiteeProjectHook.builder();
        builder.url(input.getUrl());
        HookEvents events = input.getEvents();
        if (events.getPush() != null && events.getPush()) {
            builder.pushEvents(true);
        }
        if (events.getTag() != null && events.getTag()) {
            builder.tagPushEvents(true);
        }
        if (events.getPullRequest() != null && events.getPullRequest()) {
            builder.mergeRequestsEvents(true);
        }
        if (events.getIssue() != null && events.getIssue()) {
            builder.issuesEvents(true);
        }
        if (events.getPullRequestReview() != null && events.getPullRequestReview()) {
            builder.reviewEvents(true);
        }
        return builder.build();
    }
}
