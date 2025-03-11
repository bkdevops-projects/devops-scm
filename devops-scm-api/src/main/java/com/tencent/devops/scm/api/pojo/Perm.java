package com.tencent.devops.scm.api.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户拥有的代码库权限
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Perm {
    private Boolean pull;
    private Boolean push;
    private Boolean admin;
}
