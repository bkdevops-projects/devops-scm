package com.tencent.devops.scm.sdk.bkcode.pojo.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BkCodeMergeRequestEvent extends BkCodeEvent {
    @JsonProperty("mergeRequest")
    private BkCodeEventMergeRequest mergeRequest;

    private BkCodeEventReview review;
}
