package com.tencent.devops.scm.sdk.bkcode.pojo.webhook;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BkCodeIssueEvent extends BkCodeEvent {
    private BkCodeEventIssue issue;
    private BkCodeEventIssueChange change;
}
