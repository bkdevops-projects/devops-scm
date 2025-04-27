package com.tencent.devops.scm.sdk.tsvn;

import com.tencent.devops.scm.sdk.common.Requester;
import com.tencent.devops.scm.sdk.common.auth.HttpAuthProvider;
import com.tencent.devops.scm.sdk.common.connector.ScmConnector;
import com.tencent.devops.scm.sdk.tsvn.auth.TSvnUserPassAuthProvider;
import com.tencent.devops.scm.sdk.tsvn.pojo.TSvnCommit;
import lombok.Getter;

public class TSvnApi {

    @Getter
    private final TSvnApiClient client;
    private volatile TSvnLockApi lockApi;
    private volatile TSvnWebhookApi webhookApi;
    private volatile TSvnCommitApi commitApi;

    public TSvnApi(String apiUrl, ScmConnector connector, HttpAuthProvider authorizationProvider) {
        if (authorizationProvider instanceof TSvnUserPassAuthProvider) {
            ((TSvnUserPassAuthProvider) authorizationProvider).bind(this);
        }
        this.client = new TSvnApiClient(apiUrl, connector, authorizationProvider);
    }

    public TSvnApi(TSvnApiClient client) {
        this.client = client;
    }

    Requester createRequest() {
        return new Requester(client);
    }

    public TSvnLockApi getLockApi() {
        if (lockApi == null) {
            synchronized (this) {
                if (lockApi == null) {
                    lockApi = new TSvnLockApi(this);
                }
            }
        }
        return lockApi;
    }

    public TSvnWebhookApi getWebhookApi() {
        if (webhookApi == null) {
            synchronized (this) {
                if (webhookApi == null) {
                    webhookApi = new TSvnWebhookApi(this);
                }
            }
        }
        return webhookApi;
    }

    public TSvnCommitApi getCommitApi() {
        if (commitApi == null) {
            synchronized (this) {
                if (commitApi == null) {
                    commitApi = new TSvnCommitApi(this);
                }
            }
        }
        return commitApi;
    }
}
