package com.tencent.devops.scm.sdk.tgit;

import com.tencent.devops.scm.sdk.common.auth.HttpAuthProvider;
import com.tencent.devops.scm.sdk.common.connector.ScmConnector;
import lombok.Getter;

@Getter
public class TGitApiFactory {
    private final String apiUrl;
    private final ScmConnector connector;

    public TGitApiFactory(String apiUrl, ScmConnector connector) {
        this.apiUrl = apiUrl;
        this.connector = connector;
    }

    public TGitApi fromAuthProvider(HttpAuthProvider authorizationProvider) {
        return new TGitApi(apiUrl, connector, authorizationProvider);
    }
}
