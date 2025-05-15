package com.tencent.devops.scm.sdk.tgit.pojo;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TGitTapdWorkItem {
    @JsonProperty("workspace_id")
    private Long workspace_id;
    @JsonProperty("status_zh")
    private String statusZh;
    @JsonProperty("id_str")
    private String idStr;
    private String name;
    @JsonProperty("source_type")
    private String sourceType;
    private Long id;
    @JsonProperty("source_project_id")
    private Long sourceProjectId;
    @JsonProperty("source_id")
    private Long sourceId;
    @JsonProperty("tapd_id")
    private Long tapdId;
    @JsonProperty("tapd_type")
    private String tapdType;
    @JsonProperty("tapd_id_str")
    private String tapdIdStr;
    private String status;
}
