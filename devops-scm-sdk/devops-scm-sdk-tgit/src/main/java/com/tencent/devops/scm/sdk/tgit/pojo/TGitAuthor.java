package com.tencent.devops.scm.sdk.tgit.pojo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TGitAuthor extends AbstractUser<TGitAuthor> {
    private static final long serialVersionUID = 1L;
}
