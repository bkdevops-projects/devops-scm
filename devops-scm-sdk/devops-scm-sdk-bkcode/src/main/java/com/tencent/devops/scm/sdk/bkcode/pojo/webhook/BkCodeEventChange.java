package com.tencent.devops.scm.sdk.bkcode.pojo.webhook;

import lombok.Data;

@Data
public class BkCodeEventChange {

    private ChangeDetail description;
    private ChangeDetail title;

    @Data
    static class ChangeDetail {
        /**
         * 旧值
         */
        private String from;
        /**
         * 新值
         */
        private String to;
    }
}
