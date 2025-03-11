package com.tencent.devops.scm.sdk.tgit.pojo.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class TGitPushEvent extends AbstractPushEvent {
    @JsonProperty("diff_files")
    private List<TGitEventDiffFile> diffFiles;
    @JsonProperty("create_and_update")
    private Boolean createAndUpdate;
}
