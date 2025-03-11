package com.tencent.devops.scm.provider.svn.common;

import com.tencent.devops.scm.api.pojo.Tree;
import java.util.List;
import org.junit.jupiter.api.Test;

public class SvnFileServiceTest extends AbstractSvnServiceTest {

    private static final String TEST_REVISION = "222";
    private static final String TEST_FILE_TREE = "trunk";
    private static final String TEST_FILE_PATH = "trunk/test.py";

    private final SvnFileService fileService;

    public SvnFileServiceTest() {
        super();
        fileService = new SvnFileService();
    }

    @Test
    public void testFind() {
        fileService.find(providerRepository, TEST_FILE_PATH, TEST_REVISION);
    }

    @Test
    public void testListTree() {
        List<Tree> trees =
                fileService.listTree(providerRepository, TEST_FILE_TREE, TEST_REVISION, true);
        System.out.println(trees);
    }
}
