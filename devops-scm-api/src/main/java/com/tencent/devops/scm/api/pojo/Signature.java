package com.tencent.devops.scm.api.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 代表代码提交者
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Signature {
    private String name;
    private String email;
    private String avatar;
}
