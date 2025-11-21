package com.tencent.devops.scm.sdk.bkcode.pojo.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BkCodeNoteEvent extends BkCodeEvent {
    private BkCodeEventIssue issue;
    private BkCodeEventComment comment;
    @JsonProperty("mergeRequest")
    private BkCodeEventMergeRequest mergeRequest;
}
