package com.tencent.devops.scm.api.pojo;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 代表源代码平台用户信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private Long id;
    // 登录用户名
    private String username;
    // 名称
    private String name;
    private String email;
    private String avatar;
    private Date created;
    private Date updated;
}
