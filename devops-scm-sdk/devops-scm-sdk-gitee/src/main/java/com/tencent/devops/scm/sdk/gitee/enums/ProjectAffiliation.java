package com.tencent.devops.scm.sdk.gitee.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ProjectAffiliation {
    // 授权用户拥有的仓库
    OWNER("owner"),
    // 授权用户为仓库成员
    COLLABORATOR("collaborator"),
    // 授权用户为仓库所在组织并有访问仓库权限
    ORGANIZATION_MEMBER("organization_member"),
    // 授权用户所在企业并有访问仓库权限
    ENTERPRISE_MEMBER("enterprise_member"),
    // 所有有权限的，包括所管理的组织中所有仓库、所管理的企业的所有仓库
    ADMIN("admin");

    private final String value;

    ProjectAffiliation(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
