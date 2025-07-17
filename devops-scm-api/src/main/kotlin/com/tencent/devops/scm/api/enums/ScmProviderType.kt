package com.tencent.devops.scm.api.enums

/**
 * 提供者类型
 */
enum class ScmProviderType(val value: String) {
    GIT("git"),
    SVN("svn"),
    P4("p4")
    ;
}
