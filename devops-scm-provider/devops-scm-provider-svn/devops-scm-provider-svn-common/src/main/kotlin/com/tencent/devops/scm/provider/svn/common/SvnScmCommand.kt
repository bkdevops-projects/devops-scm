package com.tencent.devops.scm.provider.svn.common

import com.tencent.devops.scm.api.ScmCommand
import com.tencent.devops.scm.api.exception.ScmApiException
import com.tencent.devops.scm.api.pojo.repository.ScmProviderRepository
import com.tencent.devops.scm.api.pojo.repository.svn.SvnScmProviderRepository
import org.tmatesoft.svn.core.SVNException

class SvnScmCommand : ScmCommand {

    override fun remoteInfo(repository: ScmProviderRepository) {
        val providerRepository = repository as SvnScmProviderRepository
        try {
            val svnRepository = SvnkitUtils.openRepo(providerRepository)
            svnRepository.info("", -1)
        } catch (e: SVNException) {
            throw ScmApiException(e)
        }
    }
}