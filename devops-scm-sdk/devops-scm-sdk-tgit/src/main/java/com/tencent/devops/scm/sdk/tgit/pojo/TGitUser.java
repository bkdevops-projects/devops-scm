package com.tencent.devops.scm.sdk.tgit.pojo;

import java.util.Date;
import java.util.List;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TGitUser extends AbstractUser<TGitUser> {

    private static final long serialVersionUID = 1L;

    private String bio;
    @Getter(AccessLevel.NONE)
    private Boolean isAdmin;
    private Date currentSignInAt;
    private Integer projectsLimit;
    private String skype;
    private String linkedin;
    private String twitter;
    private Integer themeId;
    private String state;
    private Integer colorSchemeId;
    private String websiteUrl;
    private List<TGitIdentity> identities;
    private Boolean canCreateProject;

    public Boolean getAdmin() {
        return isAdmin;
    }
}
