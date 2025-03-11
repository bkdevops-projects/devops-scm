package com.tencent.devops.scm.spring.manager;

import com.tencent.devops.scm.api.FileService;
import com.tencent.devops.scm.api.IssueService;
import com.tencent.devops.scm.api.PullRequestService;
import com.tencent.devops.scm.api.RefService;
import com.tencent.devops.scm.api.RepositoryService;
import com.tencent.devops.scm.api.ScmCommand;
import com.tencent.devops.scm.api.ScmProvider;
import com.tencent.devops.scm.api.TokenService;
import com.tencent.devops.scm.api.UserService;
import com.tencent.devops.scm.api.WebhookEnricher;
import com.tencent.devops.scm.api.WebhookParser;
import com.tencent.devops.scm.spring.properties.ScmProviderProperties;
import java.util.List;

public class ScmProviderManager {

    private final List<ScmProviderFactory> providerFactories;

    public ScmProviderManager(List<ScmProviderFactory> providerFactories) {
        this.providerFactories = providerFactories;
    }

    public ScmProvider build(ScmProviderProperties properties) {
        return build(properties, false);
    }

    public ScmProvider build(ScmProviderProperties properties, Boolean useTokenApi) {
        for (ScmProviderFactory factory : providerFactories) {
            if (factory.support(properties)) {
                return factory.build(properties, useTokenApi);
            }
        }
        throw new NoSuchScmProviderException(properties.getProviderCode());
    }

    public RepositoryService repositories(ScmProviderProperties properties) {
        return build(properties).repositories();
    }

    public RefService refs(ScmProviderProperties properties) {
        return build(properties).refs();
    }

    public IssueService issues(ScmProviderProperties properties) {
        return build(properties).issues();
    }

    public UserService users(ScmProviderProperties properties) {
        return build(properties).users();
    }

    public FileService files(ScmProviderProperties properties) {
        return build(properties).files();
    }

    public WebhookParser webhookParser(ScmProviderProperties properties) {
        return build(properties).webhookParser();
    }

    public WebhookEnricher webhookEnricher(ScmProviderProperties properties) {
        return build(properties).webhookEnricher();
    }

    public PullRequestService pullRequests(ScmProviderProperties properties) {
        return build(properties).pullRequests();
    }

    public TokenService token(ScmProviderProperties properties) {
        return build(properties, true).token();
    }

    public ScmCommand command(ScmProviderProperties properties) {
        return build(properties).command();
    }
}
