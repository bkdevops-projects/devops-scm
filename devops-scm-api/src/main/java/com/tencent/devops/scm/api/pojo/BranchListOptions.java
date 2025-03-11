package com.tencent.devops.scm.api.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BranchListOptions {
    private String search;
    private Integer page;
    private Integer pageSize;
}
