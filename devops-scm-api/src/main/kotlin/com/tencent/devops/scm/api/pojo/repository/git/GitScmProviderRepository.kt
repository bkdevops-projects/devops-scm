package com.tencent.devops.scm.api.pojo.repository.git

import com.tencent.devops.scm.api.pojo.auth.IScmAuth
import com.tencent.devops.scm.api.pojo.repository.ScmProviderRepository

/**
 * git提供者代码库信息
 */
data class GitScmProviderRepository(
    val projectIdOrPath: Any,
    val auth: IScmAuth? = null,
    val url: String? = null
) : ScmProviderRepository {
    constructor(url: String, auth: IScmAuth?) : this(
        projectIdOrPath = GitRepositoryUrl(url).fullName,
        auth = auth,
        url = url
    )

    companion object {
        const val CLASS_TYPE = "GIT"
    }
}
