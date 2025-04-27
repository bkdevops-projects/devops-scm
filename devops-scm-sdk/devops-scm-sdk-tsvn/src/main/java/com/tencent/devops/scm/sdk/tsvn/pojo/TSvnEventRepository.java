package com.tencent.devops.scm.sdk.tsvn.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class TSvnEventRepository {
    private String name;
    private String description;
    private String homepage;
    @JsonProperty("svn_http_url")
    private String svnHttpUrl;
    @JsonProperty("svn_ssh_url")
    private String svnSshUrl;
    private String url;
    @JsonProperty("visibility_level")
    private Integer visibilityLevel;

    public String getRealHttpUrl() {
        return StringUtils.isNotBlank(svnHttpUrl) ? svnHttpUrl :
                StringUtils.isNotBlank(homepage) ? homepage : url;
    }
}
