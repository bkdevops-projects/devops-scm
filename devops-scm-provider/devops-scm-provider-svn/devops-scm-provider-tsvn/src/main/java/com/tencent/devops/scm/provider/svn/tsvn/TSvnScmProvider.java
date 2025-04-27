package com.tencent.devops.scm.provider.svn.tsvn;

import com.tencent.devops.scm.api.RefService;
import com.tencent.devops.scm.api.RepositoryService;
import com.tencent.devops.scm.api.WebhookParser;
import com.tencent.devops.scm.provider.svn.common.AbstractSvnScmProvider;
import com.tencent.devops.scm.sdk.common.connector.ScmConnector;
import com.tencent.devops.scm.sdk.tsvn.TSvnApiFactory;

public class TSvnScmProvider extends AbstractSvnScmProvider {
    private final TSvnApiFactory apiFactory;

    public TSvnScmProvider(String apiUrl, ScmConnector connector) {
        this(new TSvnApiFactory(apiUrl, connector));
    }

    public TSvnScmProvider(TSvnApiFactory apiFactory) {
        this.apiFactory = apiFactory;
    }

    @Override
    public RepositoryService repositories() {
        return new TSvnRepositoryService(apiFactory);
    }

    @Override
    public RefService refs() {
        return new TSvnRefService(apiFactory);
    }

    @Override
    public WebhookParser webhookParser() {
        return new TSvnWebhookParser();
    }
}
