package com.tencent.devops.scm.sdk.bkcode;

import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeFileContent;
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class BkCodeFileApiTest extends AbstractBkCodeTest {

    private static final String TEST_FILE_PATH = "a/a_1/a_1.txt";

    public BkCodeFileApiTest() {
        super();
    }

    @BeforeAll
    public static void setup() {
        if (MOCK_DATA) {
            mock();
        } else {
            bkCodeApi = createBkCodeApi();
        }
    }

    public static void mock() {
        bkCodeApi = mockBkCodeApi();
        when(bkCodeApi.getFileApi()).thenReturn(Mockito.mock(BkCodeRepositoryFileApi.class));
        when(bkCodeApi.getFileApi().getFileContent(TEST_PROJECT_NAME, TEST_FILE_PATH, TEST_DEFAULT_BRANCH)).thenReturn(
                read(
                        "get_file_content.json",
                        new TypeReference<BkCodeResult<BkCodeFileContent>>() {}
                ).getData()
        );
    }

    @Test
    public void testGetTags() {
        BkCodeFileContent fileContent = bkCodeApi.getFileApi()
                .getFileContent(TEST_PROJECT_NAME, TEST_FILE_PATH, TEST_DEFAULT_BRANCH);
        Assertions.assertEquals(
                fileContent.getContent(),
                "123 \n123\n123\n123\n123\n123\n123\n123\n123\n123\n123\n"
        );
        Assertions.assertEquals(
                fileContent.getName(),
                "a_1.txt"
        );
        Assertions.assertEquals(
                fileContent.getPath(),
                "a/a_1/a_1.txt"
        );
        Assertions.assertFalse(fileContent.getLfs());
        Assertions.assertTrue(fileContent.getText());
    }
}
