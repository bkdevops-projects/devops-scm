package com.tencent.devops.scm.sdk.gitlab;

import com.tencent.devops.scm.sdk.common.connector.ScmConnector;
import com.tencent.devops.scm.sdk.gitlab.auth.GitlabAuthProvider;
import lombok.Getter;

@Getter
public class GitlabApiFactory {
    private final String apiUrl;
    private final ScmConnector connector;

    public GitlabApiFactory(String apiUrl, ScmConnector connector) {
        this.apiUrl = apiUrl;
        this.connector = connector;
    }

    public GitlabApi fromAuthProvider(GitlabAuthProvider authProvider) {
        return new GitlabApi(apiUrl, connector, authProvider);
    }
}
