package com.tencent.devops.scm.provider.git.bkcode

import com.tencent.devops.scm.api.CheckRunService
import com.tencent.devops.scm.api.pojo.CheckRun
import com.tencent.devops.scm.api.pojo.CheckRunInput
import com.tencent.devops.scm.api.pojo.CheckRunListOptions
import com.tencent.devops.scm.api.pojo.repository.ScmProviderRepository
import com.tencent.devops.scm.sdk.bkcode.BkCodeApiFactory

class BkCodeCheckRunService(private val apiFactory: BkCodeApiFactory) : CheckRunService {

    override fun create(repository: ScmProviderRepository, input: CheckRunInput): CheckRun {
        throw UnsupportedOperationException("bkcode not support create check run")
    }

    override fun update(repository: ScmProviderRepository, input: CheckRunInput): CheckRun {
        throw UnsupportedOperationException("bkcode not support update check run")
    }

    override fun getCheckRuns(repository: ScmProviderRepository, opts: CheckRunListOptions): List<CheckRun> {
        throw UnsupportedOperationException("bkcode not support get check run")
    }
}