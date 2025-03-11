package com.tencent.devops.scm.provider.svn.common;

import org.tmatesoft.svn.core.auth.BasicAuthenticationManager;
import org.tmatesoft.svn.core.auth.SVNAuthentication;
import org.tmatesoft.svn.core.io.SVNRepository;

public class TmatesoftBasicAuthenticationManager extends BasicAuthenticationManager {

    public TmatesoftBasicAuthenticationManager(SVNAuthentication[] authentications) {
        super(authentications);
    }

    @Override
    public int getConnectTimeout(SVNRepository repository) {
        int connectTimeout = super.getConnectTimeout(repository);
        connectTimeout = Math.min(connectTimeout, 30 * 1000);
        if (connectTimeout <= 0) {
            connectTimeout = 30 * 1000;
        }
        return connectTimeout;
    }

    @Override
    public int getReadTimeout(SVNRepository repository) {
        int readTimeout = super.getReadTimeout(repository);
        readTimeout = Math.min(readTimeout, 1800 * 1000);
        if (readTimeout <= 0) {
            readTimeout = 1800 * 1000;
        }
        return readTimeout;
    }
}
