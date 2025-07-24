package com.tencent.devops.scm.provider.gitee.simple

import com.gitee.sdk.gitee5j.model.Branch
import com.gitee.sdk.gitee5j.model.CompleteBranch
import com.gitee.sdk.gitee5j.model.Project
import com.tencent.devops.scm.api.pojo.Reference
import com.tencent.devops.scm.api.pojo.repository.git.GitScmServerRepository
import com.tencent.devops.scm.sdk.common.util.DateUtils
import java.util.Date

object GiteeObjectConverter {

    /*========================================ref====================================================*/
    fun convertBranches(form: Branch) = with(form) {
        Reference(
            name = name,
            sha = commit.sha,
            linkUrl = protectionUrl
        )
    }

    fun convertBranches(form: CompleteBranch) = with(form) {
        Reference(
            name = name,
            sha = commit.sha,
            linkUrl = protectionUrl
        )
    }

    /*========================================repository====================================================*/
    fun convertRepository(repository: Project) = with(repository) {
        GitScmServerRepository(
            id = id.toLong(),
            name = name,
            group = namespace.path,
            isPrivate = isPrivate,
            defaultBranch = defaultBranch,
            httpUrl = htmlUrl,
            sshUrl = sshUrl,
            webUrl = htmlUrl,
            created = DateUtils.convertDateToLocalDateTime(Date.from(createdAt.toInstant())),
            updated = DateUtils.convertDateToLocalDateTime(Date.from(updatedAt.toInstant())),
            fullName = fullName
        )
    }
}
