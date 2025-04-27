package com.tencent.devops.scm.provider.svn.tsvn;

import com.tencent.devops.scm.api.exception.UnAuthorizedScmApiException;
import com.tencent.devops.scm.api.function.TriConsumer;
import com.tencent.devops.scm.api.pojo.repository.ScmProviderRepository;
import com.tencent.devops.scm.api.pojo.repository.svn.SvnScmProviderRepository;
import com.tencent.devops.scm.api.pojo.webhook.Webhook;
import com.tencent.devops.scm.api.pojo.webhook.svn.PostCommitHook;
import com.tencent.devops.scm.provider.svn.common.SvnWebhookEnricher;
import com.tencent.devops.scm.provider.svn.tsvn.auth.TSvnAuthProviderAdapter;
import com.tencent.devops.scm.sdk.tsvn.TSvnApi;
import com.tencent.devops.scm.sdk.tsvn.TSvnApiException;
import com.tencent.devops.scm.sdk.tsvn.TSvnApiFactory;
import java.util.HashMap;
import java.util.Map;

public class TSvnWebhookEnricher extends SvnWebhookEnricher {

    private final Map<Class<? extends Webhook>, TriConsumer<TSvnApi, SvnScmProviderRepository, Webhook>> eventActions;
    private final TSvnApiFactory apiFactory;

    public TSvnWebhookEnricher(TSvnApiFactory apiFactory) {
        this.apiFactory = apiFactory;
        this.eventActions = new HashMap<>();
        eventActions.put(PostCommitHook.class, this::enrichPostCommitHook);
    }

    private void enrichPostCommitHook(TSvnApi giteeApi, SvnScmProviderRepository repository, Webhook hook) {
        System.out.println("enrich post commit hook");
    }

    @Override
    public Webhook enrich(ScmProviderRepository repository, Webhook webhook) {
        SvnScmProviderRepository repo = (SvnScmProviderRepository) repository;
        try {
            TSvnApi tSvnApi = apiFactory.fromAuthProvider(TSvnAuthProviderAdapter.get(repo.getAuth()));
            if (eventActions.containsKey(webhook.getClass())) {
                eventActions.get(webhook.getClass()).accept(tSvnApi, repo, webhook);
            }
            return webhook;
        } catch (TSvnApiException exception) {
            if (exception.getStatusCode() == 401 || exception.getStatusCode() == 403) {
                throw new UnAuthorizedScmApiException(exception.getMessage());
            } else {
                throw exception;
            }
        }
    }
}
