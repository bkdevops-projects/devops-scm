package com.tencent.devops.scm.api.enums

enum class ContentKind(value: String) {
    FILE("file"),
    DIRECTORY("directory"),
    SYMLINK("symlink"),
    GITLINK("gitlink"),
    UNSUPPORTED("unsupported")
    ;
}
