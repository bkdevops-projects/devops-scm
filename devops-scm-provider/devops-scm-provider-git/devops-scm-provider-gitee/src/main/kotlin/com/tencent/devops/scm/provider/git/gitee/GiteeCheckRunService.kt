package com.tencent.devops.scm.provider.git.gitee

import com.tencent.devops.scm.api.CheckRunService
import com.tencent.devops.scm.api.RefService
import com.tencent.devops.scm.api.pojo.BranchListOptions
import com.tencent.devops.scm.api.pojo.Change
import com.tencent.devops.scm.api.pojo.CheckRun
import com.tencent.devops.scm.api.pojo.CheckRunInput
import com.tencent.devops.scm.api.pojo.CheckRunListOptions
import com.tencent.devops.scm.api.pojo.Commit
import com.tencent.devops.scm.api.pojo.CommitListOptions
import com.tencent.devops.scm.api.pojo.ListOptions
import com.tencent.devops.scm.api.pojo.Reference
import com.tencent.devops.scm.api.pojo.ReferenceInput
import com.tencent.devops.scm.api.pojo.TagListOptions
import com.tencent.devops.scm.api.pojo.repository.ScmProviderRepository
import com.tencent.devops.scm.sdk.common.enums.SortOrder
import com.tencent.devops.scm.sdk.gitee.GiteeApiFactory
import com.tencent.devops.scm.sdk.gitee.enums.GiteeBranchOrderBy
import com.tencent.devops.scm.sdk.gitee.enums.GiteeCheckRunStatus
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeCheckRun
import com.tencent.devops.scm.sdk.gitee.pojo.GiteeCheckRunOutput
import java.util.Date
// :TODO 增加测试类
class GiteeCheckRunService(private val apiFactory: GiteeApiFactory) : CheckRunService {
    override fun create(repository: ScmProviderRepository, input: CheckRunInput): CheckRun {
        return GiteeApiTemplate.execute(repository, apiFactory) { repo, giteeApi ->
            val checkRun = giteeApi.checkRunApi.create(
                repo.projectIdOrPath,
                GiteeObjectConverter.convertCheckRunInput(input)
            )
            GiteeObjectConverter.convertCheckRun(checkRun)
        }
    }

    override fun update(repository: ScmProviderRepository, input: CheckRunInput): CheckRun {
        input.id ?: throw IllegalArgumentException("check_run_id is required")
        return GiteeApiTemplate.execute(repository, apiFactory) { repo, giteeApi ->
            val checkRun = giteeApi.checkRunApi.update(
                repo.projectIdOrPath,
                input.id,
                GiteeObjectConverter.convertCheckRunInput(input)
            )
            GiteeObjectConverter.convertCheckRun(checkRun)
        }
    }

    override fun getCheckRuns(repository: ScmProviderRepository, opts: CheckRunListOptions): List<CheckRun> {
        return GiteeApiTemplate.execute(repository, apiFactory) { repo, giteeApi ->
            val checkRuns = giteeApi.checkRunApi.getCheckRuns(
                repo.projectIdOrPath,
                opts.ref,
                opts.pullRequestId,
                opts.page,
                opts.pageSize
            )
            checkRuns.map {  GiteeObjectConverter.convertCheckRun(it) }
        }
    }
}
