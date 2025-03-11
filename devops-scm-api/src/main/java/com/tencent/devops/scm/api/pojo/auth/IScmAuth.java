package com.tencent.devops.scm.api.pojo.auth;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * scm授权信息
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = AccessTokenScmAuth.class, name = AccessTokenScmAuth.CLASS_TYPE),
        @JsonSubTypes.Type(value = PersonalAccessTokenScmAuth.class, name = PersonalAccessTokenScmAuth.CLASS_TYPE),
        @JsonSubTypes.Type(value = PrivateTokenScmAuth.class, name = PrivateTokenScmAuth.CLASS_TYPE),
        @JsonSubTypes.Type(value = UserPassScmAuth.class, name = UserPassScmAuth.CLASS_TYPE),
        @JsonSubTypes.Type(value = TokenUserPassScmAuth.class, name = TokenUserPassScmAuth.CLASS_TYPE),
        @JsonSubTypes.Type(value = SshPrivateKeyScmAuth.class, name = SshPrivateKeyScmAuth.CLASS_TYPE),
        @JsonSubTypes.Type(value = TokenSshPrivateKeyScmAuth.class, name = TokenSshPrivateKeyScmAuth.CLASS_TYPE),
})
public interface IScmAuth {

}
