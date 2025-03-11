package com.tencent.devops.scm.sdk.tgit;

import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tencent.devops.scm.sdk.tgit.enums.TGitBlobType;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitRepositoryFile;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitTreeItem;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class TGitRepositoryFileApiTest extends AbstractTGitTest {
    private static final String TEST_BRANCH_NAME = "master";
    private static final String TEST_FILE_PATH = "mr.txt";

    private static TGitApi tGitApi;

    public TGitRepositoryFileApiTest() {
        super();
    }

    @BeforeAll
    public static void setup() {
        tGitApi = mockTGitApi();
        when(tGitApi.getRepositoryFileApi()).thenReturn(Mockito.mock(TGitRepositoryFileApi.class));
        when(tGitApi.getRepositoryFileApi().getFile(TEST_PROJECT_NAME, TEST_FILE_PATH, TEST_BRANCH_NAME))
                .thenReturn(read("get_file.json", TGitRepositoryFile.class));
        when(tGitApi.getRepositoryFileApi().getTree(TEST_PROJECT_NAME, null, TEST_BRANCH_NAME, true))
                .thenReturn(read("get_file_tree.json", new TypeReference<List<TGitTreeItem>>() {
                }));
    }

    @Test
    public void testGetFile() {
        TGitRepositoryFile file = tGitApi.getRepositoryFileApi()
                .getFile(TEST_PROJECT_NAME, TEST_FILE_PATH, TEST_BRANCH_NAME);

        Assertions.assertEquals("master", file.getRef());
        Assertions.assertEquals("bXIgMQptciAyCm1yIDMKbXIgNAptciA1Cm1yIDY=", file.getContent());
        Assertions.assertEquals("mr 1\n"
                + "mr 2\n"
                + "mr 3\n"
                + "mr 4\n"
                + "mr 5\n"
                + "mr 6", file.getDecodedContentAsString());
        Assertions.assertEquals("mr.txt", file.getFileName());
        Assertions.assertEquals("mr.txt", file.getFilePath());
        Assertions.assertEquals("f2395fe57c34bb308725a686a623e369d2fc36d2", file.getBlobId());
        Assertions.assertEquals("b5f141f9c1b3f87d9b070157097130be7fb7563a", file.getCommitId());
    }

    @Test
    public void testGetTree() {
        List<TGitTreeItem> tree = tGitApi.getRepositoryFileApi()
                .getTree(TEST_PROJECT_NAME, null, TEST_BRANCH_NAME, true);
        Assertions.assertEquals(3, tree.size());

        TGitTreeItem item1 = tree.get(0);
        Assertions.assertEquals("a11994a2ba987a3da6d71c56e6edc3e7c182d50f", item1.getId());
        Assertions.assertEquals("README.md", item1.getName());
        Assertions.assertEquals(TGitBlobType.BLOB, item1.getType());
        Assertions.assertEquals("100644", item1.getMode());
    }
}
