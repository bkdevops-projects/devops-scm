package com.tencent.devops.scm.api.pojo;

import com.tencent.devops.scm.api.enums.StatusState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Status {
    private StatusState state;
    private String context;
    private String desc;
    private String targetUrl;
}
