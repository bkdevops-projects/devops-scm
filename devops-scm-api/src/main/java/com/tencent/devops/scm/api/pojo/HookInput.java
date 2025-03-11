package com.tencent.devops.scm.api.pojo;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class HookInput {
    private String name;
    // webhook接收的url地址
    private String url;
    private String secret;
    private HookEvents events;
    private Boolean skipVerify;

    // 在HookEvents中不包含的事件类型
    private List<String> nativeEvents;
}
