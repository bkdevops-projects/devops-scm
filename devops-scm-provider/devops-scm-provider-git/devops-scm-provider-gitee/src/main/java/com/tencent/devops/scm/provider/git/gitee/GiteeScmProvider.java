package com.tencent.devops.scm.provider.git.gitee;

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
import com.tencent.devops.scm.sdk.common.GitOauth2ClientProperties;
import com.tencent.devops.scm.sdk.common.connector.ScmConnector;
import com.tencent.devops.scm.sdk.gitee.GiteeApiFactory;

public class GiteeScmProvider extends GitScmProvider {
    private final GiteeApiFactory apiFactory;

    public GiteeScmProvider(String apiUrl, ScmConnector connector) {
        this(new GiteeApiFactory(apiUrl, connector));
    }

    public GiteeScmProvider(String apiUrl, ScmConnector connector, GitOauth2ClientProperties properties) {
        this(apiUrl, connector);
    }

    public GiteeScmProvider(GiteeApiFactory apiFactory) {
        this.apiFactory = apiFactory;
    }

    @Override
    public RepositoryService repositories() {
        return null;
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
        return new GiteeWebhookParser();
    }

    @Override
    public WebhookEnricher webhookEnricher() {
        return new GiteeWebhookEnricher(apiFactory);
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
