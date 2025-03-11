package com.tencent.devops.scm.sdk.tgit.pojo;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TGitProjectHook {

    private Long id;
    private String url;
    private Long projectId;
    @Builder.Default
    private Boolean pushEvents = false;
    @Builder.Default
    private Boolean tagPushEvents = false;
    @Builder.Default
    private Boolean issuesEvents = false;
    @Builder.Default
    private Boolean mergeRequestsEvents = false;
    @Builder.Default
    private Boolean noteEvents = false;
    @Builder.Default
    private Boolean reviewEvents = false;
    private Date createdAt;
}
