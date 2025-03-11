package com.tencent.devops.scm.api.pojo.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenSshPrivateKeyScmAuth implements IScmAuth {
    public static final String CLASS_TYPE = "TOKEN_SSH_PRIVATEKEY";

    private String token;
    private String privateKey;
    private String passphrase;
}
