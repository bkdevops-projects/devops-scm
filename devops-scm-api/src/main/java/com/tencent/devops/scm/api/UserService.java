package com.tencent.devops.scm.api;

import com.tencent.devops.scm.api.pojo.User;
import com.tencent.devops.scm.api.pojo.auth.IScmAuth;

public interface UserService {

    User find(IScmAuth auth);
}
