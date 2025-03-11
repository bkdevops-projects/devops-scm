package com.tencent.devops.scm.api.pojo;

import com.tencent.devops.scm.api.pojo.repository.git.GitScmServerRepository;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * 表示pr/mr
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PullRequest {
    @NonNull
    private Long id;
    @NonNull
    private Integer number;
    @NonNull
    private String title;
    @NonNull
    private String body;
    private String link;

    private String sha;
    private String ref;
    // 源仓库
    @NonNull
    private GitScmServerRepository sourceRepo;
    // 目标仓库
    @NonNull
    private GitScmServerRepository targetRepo;
    // 目标分支
    @NonNull
    private Reference targetRef;
    // 源分支
    @NonNull
    private Reference sourceRef;

    private Boolean closed;
    private Boolean merged;
    private String mergeType;
    // 合并后的commitId
    private String mergeCommitSha;

    private User author;
    private Date created;
    private Date updated;
    private List<String> labels;
    private String description;
    private Milestone milestone;
    private String baseCommit;
    private List<User> assignee;
}
