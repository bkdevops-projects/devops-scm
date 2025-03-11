package com.tencent.devops.scm.provider.git.command;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;
import com.tencent.devops.scm.api.pojo.auth.IScmAuth;
import com.tencent.devops.scm.api.pojo.auth.SshPrivateKeyScmAuth;
import com.tencent.devops.scm.api.pojo.auth.TokenSshPrivateKeyScmAuth;
import com.tencent.devops.scm.api.pojo.repository.git.GitScmProviderRepository;
import java.nio.charset.StandardCharsets;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.transport.JschConfigSessionFactory;
import org.eclipse.jgit.transport.OpenSshConfig;
import org.eclipse.jgit.transport.OpenSshConfig.Host;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.transport.SshTransport;
import org.eclipse.jgit.transport.Transport;
import org.eclipse.jgit.util.FS;

public class JGitTransportConfigCallback implements TransportConfigCallback {

    private SshSessionFactory sshSessionFactory = null;

    public JGitTransportConfigCallback(GitScmProviderRepository repo) {
        IScmAuth auth = repo.getAuth();
        String privateKey = null;
        String passphrase = null;
        if (auth instanceof TokenSshPrivateKeyScmAuth) {
            TokenSshPrivateKeyScmAuth sshAuth = (TokenSshPrivateKeyScmAuth) auth;
            privateKey = sshAuth.getPrivateKey();
            passphrase = sshAuth.getPassphrase();
        } else if (auth instanceof SshPrivateKeyScmAuth) {
            SshPrivateKeyScmAuth sshAuth = (SshPrivateKeyScmAuth) auth;
            privateKey = sshAuth.getPrivateKey();
            passphrase = sshAuth.getPassphrase();
        }

        if (StringUtils.isNotEmpty(privateKey) && passphrase == null) {
            sshSessionFactory = new UnprotectedPrivateKeySessionFactory(privateKey);
        } else if (StringUtils.isNotEmpty(privateKey) && passphrase != null) {
            sshSessionFactory = new ProtectedPrivateKeyFileSessionFactory(privateKey, passphrase);
        } else {
            sshSessionFactory = new SimpleSessionFactory();
        }
    }

    @Override
    public void configure(Transport transport) {
        if (transport instanceof SshTransport) {
            SshTransport sshTransport = (SshTransport) transport;
            sshTransport.setSshSessionFactory(sshSessionFactory);
        }
    }

    private static class SimpleSessionFactory extends JschConfigSessionFactory {

        @Override
        protected void configure(OpenSshConfig.Host host, Session session) {
        }
    }

    @Getter
    private abstract static class PrivateKeySessionFactory extends SimpleSessionFactory {

        private final String privateKey;
        private final String passphrase;

        PrivateKeySessionFactory(String privateKey, String passphrase) {
            this.privateKey = privateKey;
            this.passphrase = passphrase;
        }

        @Override
        protected void configure(Host host, Session session) {
            session.setConfig("StrictHostKeyChecking", "no");
            session.setUserInfo(new UserInfo() {
                @Override
                public String getPassphrase() {
                    return passphrase;
                }

                @Override
                public String getPassword() {
                    return "";
                }

                @Override
                public boolean promptPassword(String s) {
                    return false;
                }

                @Override
                public boolean promptPassphrase(String s) {
                    return true;
                }

                @Override
                public boolean promptYesNo(String s) {
                    return false;
                }

                @Override
                public void showMessage(String s) {

                }
            });
        }
    }

    private static class UnprotectedPrivateKeySessionFactory extends PrivateKeySessionFactory {

        UnprotectedPrivateKeySessionFactory(String privateKey) {
            super(privateKey, null);
        }

        @Override
        protected JSch createDefaultJSch(FS fs) throws JSchException {
            JSch defaultJSch = super.createDefaultJSch(fs);
            defaultJSch.addIdentity(
                    "TempIdentity",
                    getPrivateKey().getBytes(StandardCharsets.UTF_8),
                    null,
                    null
            );
            return defaultJSch;
        }
    }

    private static class ProtectedPrivateKeyFileSessionFactory extends PrivateKeySessionFactory {

        ProtectedPrivateKeyFileSessionFactory(String privateKey, String passphrase) {
            super(privateKey, passphrase);
        }

        @Override
        protected JSch createDefaultJSch(FS fs) throws JSchException {
            JSch defaultJSch = super.createDefaultJSch(fs);
            defaultJSch.addIdentity(
                    "TempIdentity",
                    getPrivateKey().getBytes(StandardCharsets.UTF_8),
                    null,
                    getPassphrase().getBytes(StandardCharsets.UTF_8)
            );
            return defaultJSch;
        }
    }
}
