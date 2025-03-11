package com.tencent.devops.scm.api.pojo.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonalAccessTokenScmAuth implements IScmAuth {
    public static final String CLASS_TYPE = "PERSONAL_ACCESS_TOKEN";

    private String personalAccessToken;
}
