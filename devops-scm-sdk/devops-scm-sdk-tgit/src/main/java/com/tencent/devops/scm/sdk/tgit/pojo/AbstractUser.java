package com.tencent.devops.scm.sdk.tgit.pojo;

import java.io.Serializable;
import lombok.Data;

@Data
public abstract class AbstractUser<U extends AbstractUser<U>> implements Serializable {
    private String avatarUrl;
    private Long id;
    private String name;
    private String email;
    private String state;
    private String username;
    private String webUrl;
}
