package com.tencent.devops.scm.provider.svn.tsvn

import com.tencent.devops.scm.api.pojo.Change
import com.tencent.devops.scm.api.pojo.Hook
import com.tencent.devops.scm.api.pojo.HookEvents
import com.tencent.devops.scm.api.pojo.Reference
import com.tencent.devops.scm.api.pojo.repository.svn.SvnScmServerRepository
import com.tencent.devops.scm.sdk.tsvn.pojo.TSvnCommit
import com.tencent.devops.scm.sdk.tsvn.pojo.TSvnEventFile
import com.tencent.devops.scm.sdk.tsvn.pojo.TSvnEventRepository
import com.tencent.devops.scm.sdk.tsvn.pojo.TSvnWebHookConfig
import org.apache.commons.lang3.StringUtils

object TSvnObjectConverter {

    /*========================================hook====================================================*/
    fun convertHook(from: TSvnWebHookConfig) = with(from) {
        Hook(
            id = id,
            url = url,
            events = convertEvents(this),
            active = true,
            path = path,
            name = ""
        )
    }

    private fun convertEvents(from: TSvnWebHookConfig) = with(from) {
        HookEvents(
            svnPreCommitEvents = svnPreCommitEvents,
            svnPostCommitEvents = svnPostCommitEvents,
            svnPreLockEvents = svnPreLockEvents,
            svnPostLockEvents = svnPostLockEvents
        )
    }

    /*========================================change====================================================*/
    fun convertChange(from: TSvnEventFile) = with(from) {
        val changeType = type ?: "modified"
        Change(
            path = file,
            deleted = changeType == "D",
            added = changeType == "A",
            sha = "",
            blobId = "",
            oldPath = ""
        )
    }

    /*========================================repository====================================================*/
    fun convertRepository(
        eventRepository: TSvnEventRepository,
        projectId: Long,
        fullName: String
    ) = with(eventRepository) {
        val group = StringUtils.substringBefore(fullName, name)
                .replace("/", "")
        SvnScmServerRepository(
            id = projectId.toString(),
            name = name,
            group = group,
            fullName = fullName,
            httpUrl = svnHttpUrl,
            sshUrl = svnSshUrl,
            webUrl = homepage,
            repositoryRoot = "",
            revision = 0L
        )
    }

    /*========================================ref====================================================*/
    fun convertReference(from: TSvnCommit) = with(from) {
        Reference(
            name = "",
            sha = id
        )
    }
}