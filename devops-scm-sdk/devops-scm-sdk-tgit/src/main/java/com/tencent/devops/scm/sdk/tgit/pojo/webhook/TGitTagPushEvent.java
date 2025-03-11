package com.tencent.devops.scm.sdk.tgit.pojo.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class TGitTagPushEvent extends AbstractPushEvent {
    @JsonProperty("create_from")
    private String createFrom;
    private String message;
}
