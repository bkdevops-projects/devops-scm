package com.tencent.devops.scm.sdk.gitlab;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.tencent.devops.scm.sdk.common.GitOauth2ClientProperties;
import com.tencent.devops.scm.sdk.common.connector.ScmConnectorRequest;
import com.tencent.devops.scm.sdk.gitlab.auth.GitlabTokenAuthProvider;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

class GitlabAuthAndOauthTest {
    @Test
    void oauthAccessTokenUsesBearerHeader() {
        TestConnector connector = new TestConnector().reply(200, "{\"id\":1}");
        GitlabApi api = new GitlabApi("https://gitlab.example/api/v4", connector,
                GitlabTokenAuthProvider.oauthAccessToken("oauth-token"));

        api.getUsersApi().getCurrentUser();

        assertEquals("Bearer oauth-token", connector.lastRequest().header("Authorization"));
        assertNull(connector.lastRequest().header("PRIVATE-TOKEN"));
    }

    @Test
    void personalAndPrivateTokensUsePrivateTokenHeader() {
        TestConnector connector = new TestConnector().reply(200, "{\"id\":1}").reply(200, "{\"id\":1}");
        new GitlabApi("https://gitlab.example/api/v4", connector,
                GitlabTokenAuthProvider.personalAccessToken("pat")).getUsersApi().getCurrentUser();
        new GitlabApi("https://gitlab.example/api/v4", connector,
                GitlabTokenAuthProvider.privateToken("provider-token")).getUsersApi().getCurrentUser();

        assertEquals("pat", connector.requests().get(0).header("PRIVATE-TOKEN"));
        assertEquals("provider-token", connector.requests().get(1).header("PRIVATE-TOKEN"));
        assertNull(connector.requests().get(1).header("Authorization"));
    }

    @Test
    void rejectsNullAndBlankOauthTokensBeforeAddingBearerPrefix() {
        assertThrows(IllegalArgumentException.class, () -> GitlabTokenAuthProvider.oauthAccessToken(null));
        assertThrows(IllegalArgumentException.class, () -> GitlabTokenAuthProvider.oauthAccessToken("  "));
    }

    @Test
    void authorizationUrlNormalizesTrailingSlashes() {
        GitOauth2ClientProperties properties = GitOauth2ClientProperties.builder()
                .webUrl("https://gitlab.example///").clientId("client").clientSecret("secret")
                .redirectUri("https://app.example/callback").build();

        String url = new GitlabOauth2Api(properties, new TestConnector()).authorizationUrl("state");

        assertEquals("https://gitlab.example/oauth/authorize", url.substring(0, url.indexOf('?')));
    }

    @Test
    void oauthTokenExchangeUsesFormBodyWithoutSecretsInUrl() throws IOException {
        TestConnector connector = new TestConnector().reply(200,
                "{\"access_token\":\"result\",\"token_type\":\"Bearer\"}");
        GitOauth2ClientProperties properties = GitOauth2ClientProperties.builder()
                .webUrl("https://gitlab.example").clientId("client id").clientSecret("s&cret")
                .redirectUri("https://app.example/callback?a=1").build();

        new GitlabOauth2Api(properties, connector).callback("code/value");

        ScmConnectorRequest request = connector.lastRequest();
        String body = IOUtils.toString(request.body(), StandardCharsets.UTF_8);
        assertEquals("https://gitlab.example/oauth/token", request.url().toString());
        assertEquals("application/x-www-form-urlencoded", request.contentType());
        assertEquals("grant_type=authorization_code&client_id=client+id&client_secret=s%26cret"
                + "&redirect_uri=https%3A%2F%2Fapp.example%2Fcallback%3Fa%3D1&code=code%2Fvalue", body);
        assertFalse(request.url().toString().contains("cret"));
    }
}
