package com.tencent.devops.scm.spring.manager

import com.tencent.devops.scm.api.exception.ScmApiException
import java.util.Objects.requireNonNull

class NoSuchScmProviderException(private val providerName: String) :
    ScmApiException("No such provider: '$providerName'.") {
    
    init {
        requireNonNull(providerName)
    }
}
