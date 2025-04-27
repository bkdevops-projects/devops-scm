package com.tencent.devops.scm.sdk.tsvn;

import com.tencent.devops.scm.sdk.common.auth.HttpAuthProvider;
import com.tencent.devops.scm.sdk.common.connector.ScmConnector;
import lombok.Getter;

@Getter
public class TSvnApiFactory {
    private final String apiUrl;
    private final ScmConnector connector;

    public TSvnApiFactory(String apiUrl, ScmConnector connector) {
        this.apiUrl = apiUrl;
        this.connector = connector;
    }

    public TSvnApi fromAuthProvider(HttpAuthProvider authorizationProvider) {
        return new TSvnApi(apiUrl, connector, authorizationProvider);
    }
}
