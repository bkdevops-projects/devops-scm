package com.tencent.devops.scm.provider.svn.tsvn;

import com.tencent.devops.scm.api.RepositoryService;
import com.tencent.devops.scm.api.WebhookParser;
import com.tencent.devops.scm.provider.svn.common.AbstractSvnScmProvider;

public class TSvnScmProvider extends AbstractSvnScmProvider {

    @Override
    public RepositoryService repositories() {
        return null;
    }

    @Override
    public WebhookParser webhookParser() {
        return null;
    }
}
