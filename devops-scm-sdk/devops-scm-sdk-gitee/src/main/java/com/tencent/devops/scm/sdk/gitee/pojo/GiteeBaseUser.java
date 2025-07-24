package com.tencent.devops.scm.sdk.gitee.pojo;

import java.util.Date;
import lombok.Data;

@Data
public class GiteeBaseUser {
    private Long id;
    private String name;
    private String email;
    private Date date;
}
