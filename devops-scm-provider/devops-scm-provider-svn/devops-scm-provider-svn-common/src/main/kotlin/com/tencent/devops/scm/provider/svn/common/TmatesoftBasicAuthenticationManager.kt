package com.tencent.devops.scm.provider.svn.common

import org.tmatesoft.svn.core.auth.BasicAuthenticationManager
import org.tmatesoft.svn.core.auth.SVNAuthentication
import org.tmatesoft.svn.core.io.SVNRepository
import kotlin.math.min

class TmatesoftBasicAuthenticationManager(authentications: Array<SVNAuthentication>) : 
    BasicAuthenticationManager(authentications) {

    override fun getConnectTimeout(repository: SVNRepository?): Int {
        var connectTimeout = super.getConnectTimeout(repository)
        connectTimeout = min(connectTimeout, 30 * 1000)
        if (connectTimeout <= 0) {
            connectTimeout = 30 * 1000
        }
        return connectTimeout
    }

    override fun getReadTimeout(repository: SVNRepository?): Int {
        var readTimeout = super.getReadTimeout(repository)
        readTimeout = min(readTimeout, 1800 * 1000)
        if (readTimeout <= 0) {
            readTimeout = 1800 * 1000
        }
        return readTimeout
    }
}