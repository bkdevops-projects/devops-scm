
package com.tencent.devops.scm.sdk.gitee.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
public class GiteeBaseRepository {
    private GiteeBaseUser owner;
    @JsonProperty("human_name")
    private String humanName;
    @JsonProperty("private")
    private Boolean privateRepository;
    @JsonProperty("internal")
    private Boolean internal;
    private GiteeBaseUser assigner;
    @JsonProperty("ssh_url")
    private String sshUrl;
    private String description;
    private String url;
    private String path;
    private Boolean fork;
    @JsonProperty("full_name")
    private String fullName;
    @JsonProperty("public")
    private Boolean publicRepository;
    @JsonProperty("html_url")
    private String htmlUrl;
    private String name;
    private Long id;
}
