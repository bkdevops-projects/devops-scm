package com.tencent.devops.scm.api.pojo.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPassScmAuth implements IScmAuth {
    public static final String CLASS_TYPE = "USER_PASS";

    private String username;
    private String password;
}
