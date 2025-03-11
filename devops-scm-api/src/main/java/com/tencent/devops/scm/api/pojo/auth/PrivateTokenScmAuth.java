package com.tencent.devops.scm.api.pojo.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrivateTokenScmAuth implements IScmAuth {
    public static final String CLASS_TYPE = "PRIVATE_TOKEN";

    private String privateToken;
}
