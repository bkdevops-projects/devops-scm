package com.tencent.devops.scm.api.pojo.repository.git;

import com.tencent.devops.scm.api.enums.Visibility;
import com.tencent.devops.scm.api.pojo.repository.ScmServerRepository;
import java.util.Date;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * git服务端仓库信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GitScmServerRepository implements ScmServerRepository {
    public static final String CLASS_TYPE = "GIT";

    @NonNull
    private Long id;
    // 代码库组
    @NonNull
    private String group;
    // 代码库名
    @NonNull
    private String name;
    // 代码库全名,组+名称
    @NonNull
    private String fullName;
    // 默认分支
    private String defaultBranch;
    // 是否已归档
    private Boolean archived;
    // 是否是私有仓库
    @Getter(AccessLevel.NONE)
    private Boolean isPrivate;
    private Visibility visibility;
    @NonNull
    private String httpUrl;
    @NonNull
    private String sshUrl;
    @NonNull
    private String webUrl;
    private Date created;
    private Date updated;

    public Boolean getPrivate() {
        return isPrivate;
    }
}
