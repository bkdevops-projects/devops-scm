package com.tencent.devops.scm.provider.svn.tsvn;

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
import com.tencent.devops.scm.api.pojo.repository.svn.SvnScmProviderRepository;
import com.tencent.devops.scm.provider.svn.tsvn.auth.TSvnAuthProviderAdapter;
import com.tencent.devops.scm.sdk.tsvn.TSvnApi;
import com.tencent.devops.scm.sdk.tsvn.TSvnApiFactory;
import com.tencent.devops.scm.sdk.tsvn.TSvnWebhookApi;
import com.tencent.devops.scm.sdk.tsvn.pojo.TSvnWebHookConfig;
import java.util.List;
import java.util.stream.Collectors;

public class TSvnRepositoryService implements RepositoryService {
    private final TSvnApiFactory apiFactory;

    public TSvnRepositoryService(TSvnApiFactory apiFactory) {
        this.apiFactory = apiFactory;
    }

    @Override
    public ScmServerRepository find(ScmProviderRepository repository) {
        return null;
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
        SvnScmProviderRepository repo = (SvnScmProviderRepository) repository;
        TSvnApi tSvnApi = apiFactory.fromAuthProvider(TSvnAuthProviderAdapter.get(repo.getAuth()));
        // 构建请求接口类
        TSvnWebhookApi webhookApi = tSvnApi.getWebhookApi();
        List<TSvnWebHookConfig> projectApiHooks = webhookApi.listHook(
                repo.getProjectIdOrPath(),
                opts.getPage(),
                opts.getPageSize()
        );
        return projectApiHooks.stream()
                .map(TSvnObjectConverter::convertHook)
                .collect(Collectors.toList());
    }

    @Override
    public Hook createHook(ScmProviderRepository repository, HookInput input) {
        SvnScmProviderRepository repo = (SvnScmProviderRepository) repository;
        TSvnApi tSvnApi = apiFactory.fromAuthProvider(TSvnAuthProviderAdapter.get(repo.getAuth()));
        // 构建请求接口类
        TSvnWebhookApi projectApi = tSvnApi.getWebhookApi();
        TSvnWebHookConfig projectApiHook = projectApi.addHook(
                repo.getProjectIdOrPath(),
                convertFromHookInput(input)
        );
        return TSvnObjectConverter.convertHook(projectApiHook);
    }

    @Override
    public Hook updateHook(ScmProviderRepository repository, Long hookId, HookInput input) {
        SvnScmProviderRepository repo = (SvnScmProviderRepository) repository;
        TSvnApi tSvnApi = apiFactory.fromAuthProvider(TSvnAuthProviderAdapter.get(repo.getAuth()));
        // 构建请求接口类
        TSvnWebhookApi webhookApi = tSvnApi.getWebhookApi();
        TSvnWebHookConfig projectApiHook = webhookApi.editHook(
                repo.getProjectIdOrPath(),
                hookId,
                convertFromHookInput(input)
        );
        return TSvnObjectConverter.convertHook(projectApiHook);
    }

    @Override
    public Hook getHook(ScmProviderRepository repository, Long hookId) {
        SvnScmProviderRepository repo = (SvnScmProviderRepository) repository;
        TSvnApi tSvnApi = apiFactory.fromAuthProvider(TSvnAuthProviderAdapter.get(repo.getAuth()));
        // 构建请求接口类
        TSvnWebhookApi webhookApi = tSvnApi.getWebhookApi();
        TSvnWebHookConfig projectApiHook = webhookApi.getHook(
                repo.getProjectIdOrPath(),
                hookId
        );
        return TSvnObjectConverter.convertHook(projectApiHook);
    }

    @Override
    public void deleteHook(ScmProviderRepository repository, Long hookId) {
        SvnScmProviderRepository repo = (SvnScmProviderRepository) repository;
        TSvnApi tSvnApi = apiFactory.fromAuthProvider(TSvnAuthProviderAdapter.get(repo.getAuth()));
        // 构建请求接口类
        TSvnWebhookApi webhookApi = tSvnApi.getWebhookApi();
        webhookApi.delHook(
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

    private TSvnWebHookConfig convertFromHookInput(HookInput input) {
        TSvnWebHookConfig.TSvnWebHookConfigBuilder builder = TSvnWebHookConfig.builder();
        builder.url(input.getUrl());
        builder.path(input.getPath());
        HookEvents events = input.getEvents();
        if (events.getSvnPreLockEvents() != null && events.getSvnPreLockEvents()) {
            builder.svnPreLockEvents(true);
        }
        if (events.getSvnPostLockEvents() != null && events.getSvnPostLockEvents()) {
            builder.svnPostLockEvents(true);
        }
        if (events.getSvnPreCommitEvents() != null && events.getSvnPreCommitEvents()) {
            builder.svnPreCommitEvents(true);
        }
        if (events.getSvnPostCommitEvents() != null && events.getSvnPostCommitEvents()) {
            builder.svnPostCommitEvents(true);
        }
        return builder.build();
    }
}
