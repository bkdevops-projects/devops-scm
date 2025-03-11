package com.tencent.devops.scm.api;

public interface ScmProvider {

    RepositoryService repositories();

    RefService refs();

    IssueService issues();

    UserService users();

    FileService files();

    WebhookParser webhookParser();

    WebhookEnricher webhookEnricher();

    PullRequestService pullRequests();

    TokenService token();

    /**
     * 命令行操作
     */
    ScmCommand command();
}
