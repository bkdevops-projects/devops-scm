package com.tencent.devops.scm.sdk.tgit.pojo.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tencent.devops.scm.sdk.tgit.enums.TGitVisibility;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class TGitEventRepository {
    private String name;
    private String description;
    private String homepage;
    @JsonProperty("git_http_url")
    private String gitHttpUrl;
    @JsonProperty("git_ssh_url")
    private String gitSshUrl;
    private String url;
    @JsonProperty("visibility_level")
    private TGitVisibility visibilityLevel;

    public String getRealHttpUrl() {
        return StringUtils.isNotBlank(gitHttpUrl) ? gitHttpUrl :
                StringUtils.isNotBlank(homepage) ? (homepage + ".git") : url;
    }
}
