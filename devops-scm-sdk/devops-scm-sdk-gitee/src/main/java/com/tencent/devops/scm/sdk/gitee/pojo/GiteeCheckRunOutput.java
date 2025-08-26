package com.tencent.devops.scm.sdk.gitee.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GiteeCheckRunOutput {

    @JsonProperty("title")
    private String title;

    @JsonProperty("summary")
    private String summary;

    @JsonProperty("text")
    private String text;

    @JsonProperty("annotations_count")
    private Integer annotationsCount;

    @JsonProperty("annotations_url")
    private String annotationsUrl;
}