package com.tencent.devops.scm.api.pojo;

import com.tencent.devops.scm.api.enums.ReviewState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Review {

    @NonNull
    private Long id;
    // 工蜂review事件有iid
    private Integer iid;
    private String body;
    private ReviewState state;
    @NonNull
    private String link;
    private User author;
    private Boolean closed;
    private String title;
    private List<User> reviewers;
    // review相关信息
    private String sourceCommit;
    private String sourceBranch;
    private String sourceProjectId;
    private String targetCommit;
    private String targetBranch;
    private String targetProjectId;
}
