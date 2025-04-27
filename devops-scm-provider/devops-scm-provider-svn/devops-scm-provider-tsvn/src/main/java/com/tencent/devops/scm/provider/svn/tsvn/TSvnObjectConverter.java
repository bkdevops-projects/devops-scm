package com.tencent.devops.scm.provider.svn.tsvn;

import com.tencent.devops.scm.api.pojo.Change;
import com.tencent.devops.scm.api.pojo.Change.ChangeBuilder;
import com.tencent.devops.scm.api.pojo.Hook;
import com.tencent.devops.scm.api.pojo.HookEvents;
import com.tencent.devops.scm.api.pojo.HookEvents.HookEventsBuilder;
import com.tencent.devops.scm.api.pojo.Reference;
import com.tencent.devops.scm.api.pojo.repository.svn.SvnScmServerRepository;
import com.tencent.devops.scm.sdk.tsvn.pojo.TSvnCommit;
import com.tencent.devops.scm.sdk.tsvn.pojo.TSvnEventFile;
import com.tencent.devops.scm.sdk.tsvn.pojo.TSvnEventRepository;
import com.tencent.devops.scm.sdk.tsvn.pojo.TSvnWebHookConfig;
import org.apache.commons.lang3.StringUtils;

public class TSvnObjectConverter {

    /*========================================hook====================================================*/
    public static Hook convertHook(TSvnWebHookConfig webhookConfig) {
        return Hook.builder()
                .id(webhookConfig.getId())
                .url(webhookConfig.getUrl())
                .events(convertEvents(webhookConfig))
                .active(true)
                .path(webhookConfig.getPath())
                .build();
    }

    private static HookEvents convertEvents(TSvnWebHookConfig from) {
        HookEventsBuilder builder = HookEvents.builder();
        if (from.getSvnPreCommitEvents()) {
            builder.svnPreCommitEvents(true);
        }
        if (from.getSvnPostCommitEvents()) {
            builder.svnPostCommitEvents(true);
        }
        if (from.getSvnPreLockEvents()) {
            builder.svnPreLockEvents(true);
        }
        if (from.getSvnPostLockEvents()) {
            builder.svnPostLockEvents(true);
        }
        return builder.build();
    }

    /*========================================hook====================================================*/
    public static Change convertChange(TSvnEventFile file) {
        ChangeBuilder changeBuilder = Change.builder()
                .path(file.getFile());
        switch (StringUtils.defaultIfBlank(file.getType(), "modified")) {
            case "D":
                changeBuilder.deleted(true);
                break;
            case "A":
                changeBuilder.added(true);
                break;
            default:
                break;
        }
        return changeBuilder
                .sha("")
                .blobId("")
                .oldPath("")
                .build();
    }

    /*========================================repository====================================================*/
    public static SvnScmServerRepository convertRepository(
            TSvnEventRepository eventRepository,
            Long projectId,
            String fullName
    ) {
        String group = StringUtils.substringBefore(fullName, eventRepository.getName())
                .replaceAll("/", "");
        return SvnScmServerRepository.builder()
                .id(projectId.toString())
                .name(eventRepository.getName())
                .group(group)
                .fullName(fullName)
                .httpUrl(eventRepository.getSvnHttpUrl())
                .sshUrl(eventRepository.getSvnSshUrl())
                .webUrl(eventRepository.getHomepage())
                .build();
    }

    /*========================================ref====================================================*/
    public static Reference convertReference(
            TSvnCommit commit
    ) {
        return Reference.builder()
                .name("")
                .sha(commit.getId())
                .linkUrl("")
                .path("")
                .build();
    }
}
