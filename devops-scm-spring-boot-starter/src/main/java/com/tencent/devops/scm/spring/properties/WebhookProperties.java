package com.tencent.devops.scm.spring.properties;

import com.tencent.devops.scm.api.enums.WebhookSecretType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * webhook 属性
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebhookProperties {

    // webhook 鉴权类型
    private WebhookSecretType secretType;
}
