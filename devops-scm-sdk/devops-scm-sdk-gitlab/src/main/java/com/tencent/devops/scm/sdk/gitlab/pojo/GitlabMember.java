package com.tencent.devops.scm.sdk.gitlab.pojo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class GitlabMember extends GitlabUser {
    private Integer accessLevel;
    private String expiresAt;
}
