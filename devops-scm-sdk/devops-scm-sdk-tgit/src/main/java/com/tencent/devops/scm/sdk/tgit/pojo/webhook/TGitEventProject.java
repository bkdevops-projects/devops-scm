package com.tencent.devops.scm.sdk.tgit.pojo.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tencent.devops.scm.sdk.tgit.enums.TGitAccessLevel;
import lombok.Data;

@Data
public class TGitEventProject {
    private String name;
    @JsonProperty("ssh_url")
    private String sshUrl;
    @JsonProperty("http_url")
    private String httpUrl;
    @JsonProperty("web_url")
    private String webUrl;
    private String namespace;
    @JsonProperty("visibility_level")
    private TGitAccessLevel visibilityLevel;
}
