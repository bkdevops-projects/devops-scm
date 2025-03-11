package com.tencent.devops.scm.sdk.tgit.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;

@Data
public class TGitBranch {

    private String name;
    @JsonProperty("protected")
    @Getter(AccessLevel.NONE)
    private Boolean isProtected;
    private Boolean developersCanMerge;
    private Boolean developersCanPush;
    private TGitCommit commit;
    private String description;
    private Date createdAt;
    private TGitAuthor author;

    public Boolean getProtected() {
        return isProtected;
    }
}
