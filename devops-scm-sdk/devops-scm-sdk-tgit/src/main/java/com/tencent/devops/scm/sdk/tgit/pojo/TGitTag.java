package com.tencent.devops.scm.sdk.tgit.pojo;

import java.util.Date;
import lombok.Data;

@Data
public class TGitTag {

    private String name;
    private String message;
    private TGitCommit commit;
    private Date createdAt;
    private String description;
}
