package com.tencent.devops.scm.spring.properties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HttpClientProperties {

    /**
     * api url
     */
    private String apiUrl;

    // 连接超时,单位: SECONDS
    @Builder.Default
    private Long connectTimeout = 5L;
    // 读取超时,单位: SECONDS
    @Builder.Default
    private Long readTimeout = 30L;
    // 写超时,单位: SECONDS
    @Builder.Default
    private Long writeTimeout = 30L;
    // 启用度量
    @Builder.Default
    private Boolean metrics = false;
}
