package com.tencent.devops.scm.sdk.bkcode.pojo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BkCodeMember extends BkCodeBaseUser {
    private BkCodeUser user;
    private Boolean currentUser;
}
