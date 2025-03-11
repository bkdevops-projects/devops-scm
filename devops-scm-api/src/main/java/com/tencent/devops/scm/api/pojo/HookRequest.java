package com.tencent.devops.scm.api.pojo;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HookRequest {
    private Map<String, String> headers;
    private String body;
    private Map<String, String> queryParams;
}
