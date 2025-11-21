package com.tencent.devops.scm.sdk.bkcode;

import com.tencent.devops.scm.sdk.common.auth.HttpAuthProvider;
import com.tencent.devops.scm.sdk.common.connector.ScmConnector;
import lombok.Getter;

@Getter
public class BkCodeApiFactory {
    private final String apiUrl;
    private final ScmConnector connector;

    public BkCodeApiFactory(String apiUrl, ScmConnector connector) {
        this.apiUrl = apiUrl;
        this.connector = connector;
    }

    public BkCodeApi fromAuthProvider(HttpAuthProvider authorizationProvider) {
        return new BkCodeApi(apiUrl, connector, authorizationProvider);
    }
}
