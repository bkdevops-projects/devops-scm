package com.tencent.devops.scm.api.pojo.repository.svn;

import com.tencent.devops.scm.api.pojo.repository.ScmServerRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * svn服务端仓库信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SvnScmServerRepository implements ScmServerRepository {
    public static final String CLASS_TYPE = "SVN";

    @NonNull
    private String id;
    // 代码库组
    @NonNull
    private String group;
    // 代码库名
    @NonNull
    private String name;
    // 代码库全名,组+名称
    @NonNull
    private String fullName;
    @NonNull
    private String httpUrl;
    @NonNull
    private String sshUrl;
    private String webUrl;
    private Long revision;
    private String repositoryRoot;

    @Override
    public Object getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getFullName() {
        return fullName;
    }
}
