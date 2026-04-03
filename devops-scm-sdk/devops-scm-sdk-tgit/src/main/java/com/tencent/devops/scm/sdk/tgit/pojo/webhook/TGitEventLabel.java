package com.tencent.devops.scm.sdk.tgit.pojo.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TGitEventLabel {
    private Long id;
    @JsonProperty("title")
    private String title;
}
