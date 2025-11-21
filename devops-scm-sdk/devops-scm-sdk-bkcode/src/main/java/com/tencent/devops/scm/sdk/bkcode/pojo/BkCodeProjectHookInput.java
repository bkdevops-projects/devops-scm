package com.tencent.devops.scm.sdk.bkcode.pojo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BkCodeProjectHookInput {
    private String name;
    private String url;
    private String token;
    private Boolean sslVerificationEnabled;
    private String[] events;
    private String branchPattern;
    private Boolean enabled;
    private String description;
}
