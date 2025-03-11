package com.tencent.devops.scm.provider.svn.common;

import org.junit.jupiter.api.Test;

public class SvnScmCommandTest extends AbstractSvnServiceTest {
    private final SvnScmCommand scmCommand;

    public SvnScmCommandTest() {
        super();
        scmCommand = new SvnScmCommand();
    }

    @Test
    public void testRemoteInfo() {
        scmCommand.remoteInfo(providerRepository);
    }
}
