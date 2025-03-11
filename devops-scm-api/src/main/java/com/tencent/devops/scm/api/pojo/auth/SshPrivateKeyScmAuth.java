package com.tencent.devops.scm.api.pojo.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SshPrivateKeyScmAuth implements IScmAuth {
    public static final String CLASS_TYPE = "SSH_PRIVATEKEY";

    private String privateKey;
    private String passphrase;
}
