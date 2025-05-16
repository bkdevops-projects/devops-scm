package com.tencent.devops.scm.sdk.gitee;

import com.tencent.devops.scm.sdk.common.auth.HttpAuthProvider;
import com.tencent.devops.scm.sdk.common.connector.ScmConnector;
import lombok.Getter;

@Getter
public class GiteeApiFactory {
    private final String apiUrl;
    private final ScmConnector connector;

    public GiteeApiFactory(String apiUrl, ScmConnector connector) {
        this.apiUrl = apiUrl;
        this.connector = connector;
    }

    public GiteeApi fromAuthProvider(HttpAuthProvider authorizationProvider) {
        return new GiteeApi(apiUrl, connector, authorizationProvider);
    }
}
