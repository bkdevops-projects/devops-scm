package com.tencent.devops.scm.provider.svn.common;

import com.tencent.devops.scm.api.FileService;
import com.tencent.devops.scm.api.IssueService;
import com.tencent.devops.scm.api.PullRequestService;
import com.tencent.devops.scm.api.RefService;
import com.tencent.devops.scm.api.ScmCommand;
import com.tencent.devops.scm.api.ScmProvider;
import com.tencent.devops.scm.api.TokenService;
import com.tencent.devops.scm.api.UserService;
import com.tencent.devops.scm.api.WebhookEnricher;

public abstract class AbstractSvnScmProvider implements ScmProvider {

    @Override
    public RefService refs() {
        return new SvnRefService();
    }

    @Override
    public IssueService issues() {
        throw new UnsupportedOperationException("svn not support issue service");
    }

    @Override
    public UserService users() {
        throw new UnsupportedOperationException("svn not support user service");
    }

    @Override
    public FileService files() {
        return new SvnFileService();
    }

    @Override
    public WebhookEnricher webhookEnricher() {
        return new SvnWebhookEnricher();
    }

    @Override
    public PullRequestService pullRequests() {
        throw new UnsupportedOperationException("svn not support pull request service");
    }

    @Override
    public TokenService token() {
        throw new UnsupportedOperationException("svn not support token service");
    }

    @Override
    public ScmCommand command() {
        return new SvnScmCommand();
    }
}
