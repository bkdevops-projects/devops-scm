package com.tencent.devops.scm.sdk.gitee.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GiteeCheckRunResult {
    @JsonProperty("total_count")
    private Integer totalCount;

    @JsonProperty("check_runs")
    private List<GiteeCheckRun> checkRuns;
}
