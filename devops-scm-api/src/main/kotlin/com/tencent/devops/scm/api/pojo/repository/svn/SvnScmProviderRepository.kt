package com.tencent.devops.scm.api.pojo.repository.svn

import com.tencent.devops.scm.api.pojo.auth.IScmAuth
import com.tencent.devops.scm.api.pojo.repository.ScmProviderRepository
import com.tencent.devops.scm.api.util.SvnUtils

data class SvnScmProviderRepository(
    val url: String,
    val userName: String? = null,
    val auth: IScmAuth? = null
) : ScmProviderRepository {
    val projectIdOrPath: Any = SvnUtils.getSvnProjectName(url)

    companion object {
        const val CLASS_TYPE = "SVN"
    }
}
