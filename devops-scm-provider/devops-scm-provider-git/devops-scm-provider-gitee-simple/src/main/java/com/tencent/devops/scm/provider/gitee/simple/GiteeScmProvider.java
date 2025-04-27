package com.tencent.devops.scm.provider.gitee.simple;

import com.tencent.devops.scm.api.FileService;
import com.tencent.devops.scm.api.IssueService;
import com.tencent.devops.scm.api.PullRequestService;
import com.tencent.devops.scm.api.RefService;
import com.tencent.devops.scm.api.RepositoryService;
import com.tencent.devops.scm.api.TokenService;
import com.tencent.devops.scm.api.UserService;
import com.tencent.devops.scm.api.WebhookEnricher;
import com.tencent.devops.scm.api.WebhookParser;
import com.tencent.devops.scm.provider.git.command.GitScmProvider;

public class GiteeScmProvider extends GitScmProvider {
    private final GiteeApiClientFactory apiFactory;

    public GiteeScmProvider(String apiUrl) {
        this(new GiteeApiClientFactory(apiUrl));
    }

    public GiteeScmProvider(GiteeApiClientFactory apiFactory) {
        this.apiFactory = apiFactory;
    }

    @Override
    public RepositoryService repositories() {
        return new GiteeRepositoryService(apiFactory);
    }

    @Override
    public RefService refs() {
        return new GiteeRefService(apiFactory);
    }

    @Override
    public IssueService issues() {
        return null;
    }

    @Override
    public UserService users() {
        return null;
    }

    @Override
    public FileService files() {
        return null;
    }

    @Override
    public WebhookParser webhookParser() {
        return null;
    }

    @Override
    public WebhookEnricher webhookEnricher() {
        return null;
    }

    @Override
    public PullRequestService pullRequests() {
        return null;
    }

    @Override
    public TokenService token() {
        return null;
    }
}
