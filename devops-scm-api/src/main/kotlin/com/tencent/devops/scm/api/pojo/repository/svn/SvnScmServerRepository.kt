package com.tencent.devops.scm.api.pojo.repository.svn

import com.tencent.devops.scm.api.pojo.repository.ScmServerRepository

/**
 * svn服务端仓库信息
 */
data class SvnScmServerRepository(
    // 仓库ID
    override val id: String,
    // 代码库组
    val group: String,
    // 代码库名
    override val name: String,
    // 代码库全名(组+名称)
    override val fullName: String,
    // HTTP访问地址
    val httpUrl: String,
    // SSH访问地址
    val sshUrl: String,
    // Web访问地址
    val webUrl: String,
    // 版本号
    val revision: Long,
    // 仓库根路径
    val repositoryRoot: String
) : ScmServerRepository {
    companion object {
        const val CLASS_TYPE = "SVN"
    }
}
