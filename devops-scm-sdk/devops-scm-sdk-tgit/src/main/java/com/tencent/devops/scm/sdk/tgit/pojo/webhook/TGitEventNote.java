package com.tencent.devops.scm.sdk.tgit.pojo.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tencent.devops.scm.sdk.tgit.enums.TGitNoteableType;
import lombok.Data;

import java.util.Date;

@Data
public class TGitEventNote {
    private Long id;
    private String note;
    private String discussionId;
    private String type;
    @JsonProperty("noteable_type")
    private TGitNoteableType noteableType;
    private Long authorId;
    private Date createdAt;
    private Date updatedAt;
    private Long projectId;
    private String attachment;
    private String lineCode;
    private String commitId;
    private Integer noteableId;
    private Boolean system;
    private String url;
    private String action;
}
