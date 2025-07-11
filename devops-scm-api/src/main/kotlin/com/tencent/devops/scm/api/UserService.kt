package com.tencent.devops.scm.api

import com.tencent.devops.scm.api.pojo.User
import com.tencent.devops.scm.api.pojo.auth.IScmAuth

/**
 * 用户服务接口
 */
interface UserService {
    /**
     * 查找用户信息
     * @param auth 认证信息
     * @return 用户对象
     */
    fun find(auth: IScmAuth): User
}
