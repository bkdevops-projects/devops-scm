package com.tencent.devops.scm.sdk.tgit.pojo;

import com.tencent.devops.scm.sdk.tgit.enums.TGitAccessLevel;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TGitMember extends AbstractUser<TGitMember> {
    private TGitAccessLevel accessLevel;
    private Date expiresAt;
}
