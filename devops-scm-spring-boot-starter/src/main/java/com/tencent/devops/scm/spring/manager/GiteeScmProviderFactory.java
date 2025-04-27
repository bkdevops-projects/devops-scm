package com.tencent.devops.scm.spring.manager;

import com.tencent.devops.scm.api.ScmProvider;
import com.tencent.devops.scm.api.enums.ScmProviderCodes;
import com.tencent.devops.scm.provider.git.gitee.GiteeScmProvider;
import com.tencent.devops.scm.sdk.common.connector.ScmConnector;
import com.tencent.devops.scm.sdk.common.GitOauth2ClientProperties;
import com.tencent.devops.scm.spring.properties.HttpClientProperties;
import com.tencent.devops.scm.spring.properties.Oauth2ClientProperties;
import com.tencent.devops.scm.spring.properties.ScmProviderProperties;

public class GiteeScmProviderFactory implements ScmProviderFactory {

    private final ScmConnectorFactory connectorFactory;

    public GiteeScmProviderFactory(ScmConnectorFactory connectorFactory) {
        this.connectorFactory = connectorFactory;
    }

    @Override
    public Boolean support(ScmProviderProperties properties) {
        return ScmProviderCodes.GITEE.name().equals(properties.getProviderCode());
    }

    @Override
    public ScmProvider build(ScmProviderProperties properties, boolean tokenApi) {
        HttpClientProperties httpClientProperties = properties.getHttpClientProperties();
        ScmConnector connector = connectorFactory.create(httpClientProperties);
        GitOauth2ClientProperties tgitOauth2ClientProperties = gettGitOauth2ClientProperties(properties, tokenApi);

        if (tgitOauth2ClientProperties != null) {
            return new GiteeScmProvider(httpClientProperties.getApiUrl(), connector, tgitOauth2ClientProperties);
        } else {
            return new GiteeScmProvider(httpClientProperties.getApiUrl(), connector);
        }
    }

    private GitOauth2ClientProperties gettGitOauth2ClientProperties(ScmProviderProperties properties,
            boolean tokenApi) {
        GitOauth2ClientProperties giteeOauth2ClientProperties = null;
        if (tokenApi && properties.getOauth2Enabled() && properties.getOauth2ClientProperties() != null) {
            Oauth2ClientProperties oauth2ClientProperties = properties.getOauth2ClientProperties();
            giteeOauth2ClientProperties = new GitOauth2ClientProperties(
                    oauth2ClientProperties.getWebUrl(),
                    oauth2ClientProperties.getClientId(),
                    oauth2ClientProperties.getClientSecret(),
                    oauth2ClientProperties.getRedirectUri()
            );
        }
        return giteeOauth2ClientProperties;
    }
}
