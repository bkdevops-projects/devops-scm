package com.tencent.devops.scm.sdk.gitee.pojo;

import java.util.Date;
import lombok.Data;

@Data
public class GiteeTagCommit {
    private Date date;
    private String sha;
}