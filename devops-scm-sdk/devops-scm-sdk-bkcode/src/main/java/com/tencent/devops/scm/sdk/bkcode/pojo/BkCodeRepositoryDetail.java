package com.tencent.devops.scm.sdk.bkcode.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tencent.devops.scm.sdk.bkcode.enums.BkCodeRepoType;
import java.util.Date;
import lombok.Data;

/**
 * 仓库详情类，用于封装代码仓库的详细信息
 */
@Data
public class BkCodeRepositoryDetail {
    /**
     * 创建人信息，对应仓库的创建者
     */
    private BkCodeUser creator;
    /**
     * 仓库显示名
     */
    @JsonProperty("displayName")
    private String displayName;
    /**
     * 所属仓库组ID
     */
    @JsonProperty("groupId")
    private Long groupId;
    /**
     * 仓库更新时间
     */
    @JsonProperty("updateTime")
    private Date updateTime;
    /**
     * 仓库显示路径
     */
    @JsonProperty("displayPath")
    private String displayPath;
    /**
     * 是否关注该仓库
     */
    private Boolean followed;
    /**
     * 仓库更新人信息
     */
    private BkCodeUser updater;
    /**
     * 仓库路径
     */
    private String path;
    /**
     * 仓库类型，参考[RepoType]
     */
    @JsonProperty("repoType")
    private BkCodeRepoType repoType;
    /**
     * 仓库创建时间
     */
    @JsonProperty("createTime")
    private Date createTime;
    /**
     * 租户ID
     */
    @JsonProperty("tenantId")
    private String tenantId;
    /**
     * 仓库名称
     */
    private String name;
    /**
     * 是否初始化仓库
     */
    private Boolean initialized;
    /**
     * 仓库ID
     */
    private Long id;
    /**
     * 合并请求数
     */
    @JsonProperty("mrCount")
    private Long mrCount;
    /**
     * 仓库描述
     */
    private String desc;
    /**
     * 仓库URL
     */
    @JsonProperty("httpUrl")
    private String httpUrl;
    /**
     * 仓库ssh URL
     */
    @JsonProperty("sshUrl")
    private String sshUrl;
    /**
     * 默认分支
     */
    @JsonProperty("defaultBranch")
    private String defaultBranch;
}
