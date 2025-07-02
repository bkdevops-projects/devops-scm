package com.tencent.devops.scm.api.pojo.repository.git

import com.tencent.devops.scm.api.enums.Visibility
import com.tencent.devops.scm.api.pojo.repository.ScmServerRepository
import java.time.LocalDateTime

/**
 * git服务端仓库信息
 */
data class GitScmServerRepository(
    override val id: Long,
    // 代码库组
    val group: String,
    // 代码库名
    override val name: String,
    // 代码库全名,组+名称
    override val fullName: String,
    // 默认分支
    var defaultBranch: String? = null,
    // 是否已归档
    var archived: Boolean? = null,
    // 是否是私有仓库
    val isPrivate: Boolean? = null,
    val visibility: Visibility? = null,
    var httpUrl: String,
    val sshUrl: String,
    val webUrl: String,
    val created: LocalDateTime? = null,
    val updated: LocalDateTime? = null
) : ScmServerRepository {
    companion object {
        const val CLASS_TYPE = "GIT"
    }
}
