package com.tencent.devops.scm.provider.git.tgit

import com.tencent.devops.scm.api.CheckRunService
import com.tencent.devops.scm.api.pojo.CheckRun
import com.tencent.devops.scm.api.pojo.CheckRunInput
import com.tencent.devops.scm.api.pojo.CheckRunListOptions
import com.tencent.devops.scm.api.pojo.repository.ScmProviderRepository
import com.tencent.devops.scm.sdk.common.util.DateUtils
import com.tencent.devops.scm.sdk.tgit.TGitApiFactory
import com.tencent.devops.scm.sdk.tgit.pojo.TGitCheckRun

class TGitCheckRunService(
    private val apiFactory: TGitApiFactory
) : CheckRunService {

    override fun create(repository: ScmProviderRepository, input: CheckRunInput): CheckRun {
        return TGitApiTemplate.execute(repository, apiFactory) { repo, tGitApi ->
            val checkRun = tGitApi.checkRunApi.create(
                repo.projectIdOrPath,
                input.ref,
                input.let {
                    TGitCheckRun.builder()
                            .id(0)
                            .state(
                                TGitObjectConverter.convertCheckRunState(
                                    status = it.status,
                                    conclusion = it.conclusion
                                ).toValue()
                            )
                            .targetUrl(it.detailsUrl)
                            .context(it.name)
                            .detail(it.output?.text)
                            .description(it.output?.summary)
                            .block(it.block)
                            .targetBranches(it.targetBranches ?: listOf(TGitCheckRun.ALL_BRANCH_FLAG))
                            .createdAt(DateUtils.convertLocalDateTimeToDate(it.startedAt))
                            .updatedAt(DateUtils.convertLocalDateTimeToDate(it.completedAt))
                            .build()
                }
            )
            TGitObjectConverter.convertCheckRun(checkRun)
        }
    }

    override fun update(repository: ScmProviderRepository, input: CheckRunInput) : CheckRun {
        // TGIT 暂时没有更新接口，更新操作继续使用创建接口，使用新值覆盖旧值
        return create(repository, input)
    }

    override fun getCheckRuns(repository: ScmProviderRepository, opts: CheckRunListOptions): List<CheckRun> {
        return TGitApiTemplate.execute(repository, apiFactory) { repo, tGitApi ->
            val checkRuns = tGitApi.checkRunApi.getCheckRuns(
                repo.projectIdOrPath,
                opts.ref,
                opts.page,
                opts.pageSize
            )
            checkRuns.map(TGitObjectConverter::convertCheckRun)
        }
    }
}