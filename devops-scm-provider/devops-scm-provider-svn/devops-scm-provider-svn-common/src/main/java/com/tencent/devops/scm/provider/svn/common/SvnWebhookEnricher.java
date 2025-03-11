package com.tencent.devops.scm.provider.svn.common;

import com.tencent.devops.scm.api.WebhookEnricher;
import com.tencent.devops.scm.api.exception.UnAuthorizedScmApiException;
import com.tencent.devops.scm.api.pojo.repository.ScmProviderRepository;
import com.tencent.devops.scm.api.pojo.webhook.Webhook;

public class SvnWebhookEnricher implements WebhookEnricher {

    @Override
    public Webhook enrich(ScmProviderRepository repository, Webhook webhook) throws UnAuthorizedScmApiException {
        return webhook;
    }
}
