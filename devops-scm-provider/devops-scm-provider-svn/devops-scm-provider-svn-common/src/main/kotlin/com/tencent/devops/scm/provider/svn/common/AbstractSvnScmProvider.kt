package com.tencent.devops.scm.provider.svn.common

import com.tencent.devops.scm.api.CheckRunService
import com.tencent.devops.scm.api.FileService
import com.tencent.devops.scm.api.IssueService
import com.tencent.devops.scm.api.PullRequestService
import com.tencent.devops.scm.api.RefService
import com.tencent.devops.scm.api.ScmCommand
import com.tencent.devops.scm.api.ScmProvider
import com.tencent.devops.scm.api.TokenService
import com.tencent.devops.scm.api.UserService
import com.tencent.devops.scm.api.WebhookEnricher

abstract class AbstractSvnScmProvider : ScmProvider {

    override fun refs(): RefService {
        return SvnRefService()
    }

    override fun issues(): IssueService {
        throw UnsupportedOperationException("svn not support issue service")
    }

    override fun users(): UserService {
        throw UnsupportedOperationException("svn not support user service")
    }

    override fun files(): FileService {
        return SvnFileService()
    }

    override fun webhookEnricher(): WebhookEnricher {
        return SvnWebhookEnricher()
    }

    override fun pullRequests(): PullRequestService {
        throw UnsupportedOperationException("svn not support pull request service")
    }

    override fun token(): TokenService {
        throw UnsupportedOperationException("svn not support token service")
    }

    override fun command(): ScmCommand {
        return SvnScmCommand()
    }

    override fun checkRun(): CheckRunService {
        throw UnsupportedOperationException("svn not support check run service")
    }
}