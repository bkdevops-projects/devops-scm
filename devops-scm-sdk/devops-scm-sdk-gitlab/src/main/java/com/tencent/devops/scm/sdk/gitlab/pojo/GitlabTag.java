package com.tencent.devops.scm.sdk.gitlab.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import lombok.Data;

@Data
public class GitlabTag {
    private String name;
    private String message;
    private String target;
    @JsonProperty("protected")
    private boolean protectedTag;
    private Date createdAt;
    private GitlabCommit commit;
}
