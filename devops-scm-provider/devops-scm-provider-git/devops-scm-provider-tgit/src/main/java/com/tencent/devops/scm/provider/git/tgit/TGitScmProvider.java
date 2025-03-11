package com.tencent.devops.scm.provider.git.tgit;

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
import com.tencent.devops.scm.sdk.common.connector.ScmConnector;
import com.tencent.devops.scm.sdk.tgit.TGitApiFactory;
import com.tencent.devops.scm.sdk.tgit.TGitOauth2Api;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitOauth2ClientProperties;

public class TGitScmProvider extends GitScmProvider {
    private final TGitApiFactory apiFactory;
    private TGitOauth2Api oauth2Api;

    public TGitScmProvider(String apiUrl, ScmConnector connector) {
        this(new TGitApiFactory(apiUrl, connector));
    }

    public TGitScmProvider(String apiUrl, ScmConnector connector, TGitOauth2ClientProperties properties) {
        this(apiUrl, connector);
        this.oauth2Api = new TGitOauth2Api(properties, connector);
    }

    public TGitScmProvider(TGitApiFactory apiFactory) {
        this.apiFactory = apiFactory;
    }

    public RepositoryService repositories() {
        return new TGitRepositoryService(apiFactory);
    }

    @Override
    public RefService refs() {
        return new TGitRefService(apiFactory);
    }

    @Override
    public IssueService issues() {
        return new TGitIssueService(apiFactory);
    }

    @Override
    public UserService users() {
        return new TGitUserService(apiFactory);
    }

    @Override
    public FileService files() {
        return new TGitFileService(apiFactory);
    }

    @Override
    public WebhookParser webhookParser() {
        return new TGitWebhookParser();
    }

    @Override
    public WebhookEnricher webhookEnricher() {
        return new TGitWebhookEnricher(apiFactory);
    }

    @Override
    public PullRequestService pullRequests() {
        return new TGitPullRequestService(apiFactory);
    }

    public TokenService token() {
        return new TGitTokenService(oauth2Api);
    }
}
