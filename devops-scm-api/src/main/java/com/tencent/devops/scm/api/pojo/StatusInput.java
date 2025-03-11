package com.tencent.devops.scm.api.pojo;

import com.tencent.devops.scm.api.enums.StatusState;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatusInput {
    private StatusState state;
    private String label;
    private String desc;
    private String target;

    /* 这两个字段是工蜂特有 */
    private Boolean block;
    private List<String> targetBranches;
}
