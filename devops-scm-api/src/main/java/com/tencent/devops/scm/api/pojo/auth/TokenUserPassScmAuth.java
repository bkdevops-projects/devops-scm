package com.tencent.devops.scm.api.pojo.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenUserPassScmAuth implements IScmAuth {

    public static final String CLASS_TYPE = "TOKEN_USER_PASS";

    private String token;
    private String username;
    private String password;
}
