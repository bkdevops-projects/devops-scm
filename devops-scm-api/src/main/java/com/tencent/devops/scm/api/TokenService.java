package com.tencent.devops.scm.api;

import com.tencent.devops.scm.api.pojo.Oauth2AccessToken;

public interface TokenService {

    String authorizationUrl(String state);

    Oauth2AccessToken callback(String code);

    Oauth2AccessToken refresh(String refreshToken);
}
