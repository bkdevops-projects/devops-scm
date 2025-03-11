package com.tencent.devops.scm.provider.git.tgit;

import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tencent.devops.scm.api.enums.ContentKind;
import com.tencent.devops.scm.api.pojo.Content;
import com.tencent.devops.scm.api.pojo.Tree;
import com.tencent.devops.scm.sdk.tgit.TGitRepositoryFileApi;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitRepositoryFile;
import com.tencent.devops.scm.sdk.tgit.pojo.TGitTreeItem;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class TGitFileServiceTest extends AbstractTGitServiceTest {

    private static final String TEST_BRANCH_NAME = "master";
    private static final String TEST_FILE_PATH = "mr.txt";

    private final TGitFileService fileService;

    public TGitFileServiceTest() {
        super();

        when(tGitApi.getRepositoryFileApi()).thenReturn(Mockito.mock(TGitRepositoryFileApi.class));
        TGitRepositoryFileApi repositoryFileApi = tGitApi.getRepositoryFileApi();

        when(repositoryFileApi.getFile(TEST_PROJECT_NAME, TEST_FILE_PATH, TEST_BRANCH_NAME))
                .thenReturn(read("get_file.json", TGitRepositoryFile.class));
        when(tGitApi.getRepositoryFileApi().getTree(TEST_PROJECT_NAME, null, TEST_BRANCH_NAME, true))
                .thenReturn(read("get_file_tree.json", new TypeReference<List<TGitTreeItem>>() {
                }));

        fileService = new TGitFileService(apiFactory);
    }

    @Test
    public void testFind() {
        Content content = fileService.find(providerRepository, TEST_FILE_PATH, TEST_BRANCH_NAME);
        Assertions.assertEquals("mr.txt", content.getPath());
        Assertions.assertEquals("mr 1\n"
                + "mr 2\n"
                + "mr 3\n"
                + "mr 4\n"
                + "mr 5\n"
                + "mr 6", content.getContent());
        Assertions.assertEquals("b5f141f9c1b3f87d9b070157097130be7fb7563a", content.getSha());
        Assertions.assertEquals("f2395fe57c34bb308725a686a623e369d2fc36d2", content.getBlobId());
    }

    @Test
    public void testListTree() {
        List<Tree> trees = fileService.listTree(providerRepository, null, TEST_BRANCH_NAME, true);
        Assertions.assertEquals(3, trees.size());

        Tree tree = trees.get(0);
        Assertions.assertEquals("a11994a2ba987a3da6d71c56e6edc3e7c182d50f", tree.getBlobId());
        Assertions.assertEquals("README.md", tree.getPath());
        Assertions.assertEquals(ContentKind.FILE, tree.getKind());
    }
}
