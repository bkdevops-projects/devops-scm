package com.tencent.devops.scm.api.pojo;

import com.tencent.devops.scm.api.enums.IssueState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IssueListOptions {
    private Integer page;
    private Integer pageSize;
    private IssueState state;
}
