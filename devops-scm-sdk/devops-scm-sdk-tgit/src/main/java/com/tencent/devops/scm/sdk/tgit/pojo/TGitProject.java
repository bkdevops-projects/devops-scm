package com.tencent.devops.scm.sdk.tgit.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import java.util.List;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;

@Data
public class TGitProject {
    private Long id;
    private String description;
    @JsonProperty("public")
    @Getter(AccessLevel.NONE)
    private Boolean isPublic;
    private Boolean archived;
    private Integer visibilityLevel;
    private Integer publicVisibility;
    private TGitNamespace namespace;
    private TGitOwner owner;
    private String name;
    private String nameWithNamespace;
    private String path;
    private String pathWithNamespace;
    private String defaultBranch;
    private String sshUrlToRepo;
    private String httpUrlToRepo;
    private String httpsUrlToRepo;
    private String webUrl;
    private List<String> tagList;
    private Boolean issuesEnabled;
    private Boolean mergeRequestsEnabled;
    private Boolean wikiEnabled;
    private Boolean snippetsEnabled;
    private Boolean reviewEnabled;
    private Boolean forkEnabled;
    private String tagNameRegex;
    private Integer tagCreatePushLevel;
    private Date createdAt;
    private Date lastActivityAt;
    private Integer creatorId;
    private String avatarUrl;
    private Integer watchsCount;
    private Integer starsCount;
    private Integer forksCount;
    private TGitConfigStorage configStorage;
    private TGitProjectStatistics statistics;
    private TGitPermissions permissions;
    private String createdFromId;
    /**
     * 项目是否设置为模板仓库，默认:false
     */
    private Boolean templateRepository;
    /**
     * 允许使用 --skip-reviewer 在 MR 创建时跳过评审，默认:false
     */
    private Boolean allowSkipReviewer;
    /**
     * 允许使用 --skip-owner 在 MR 创建时跳过文件评审，默认:false
     */
    private Boolean allowSkipOwner;
    /**
     * 紧急情况时，允许绕过MR的检查结果直接合并，默认:false
     */
    private Boolean allowSkipMrCheck;
    /**
     * 开启 AI 自动评审，默认:false
     */
    private Boolean autoIntelligentReviewEnabled;

    public Boolean getPublic() {
        return isPublic;
    }
}
