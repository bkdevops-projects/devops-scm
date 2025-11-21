package com.tencent.devops.scm.api.enums

/**
 * 源码管理平台提供者
 */
enum class ScmProviderCodes(val value: String) {
    TGIT("tgit"),
    TSVN("tsvn"),
    GITHUB("github"),
    GITLAB("gitlab"),
    GITEE("gitee"),
    BKCODE("bkCode");
}
