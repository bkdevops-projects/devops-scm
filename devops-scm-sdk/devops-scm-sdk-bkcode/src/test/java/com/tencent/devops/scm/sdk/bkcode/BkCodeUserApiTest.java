package com.tencent.devops.scm.sdk.bkcode;

import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeResult;
import com.tencent.devops.scm.sdk.bkcode.pojo.BkCodeUser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class BkCodeUserApiTest extends AbstractBkCodeTest {
    public BkCodeUserApiTest() {
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
        when(bkCodeApi.getUserApi()).thenReturn(Mockito.mock(BkCodeUserApi.class));

        when(
                bkCodeApi.getUserApi().getCurrentUser()
        ).thenReturn(
                read(
                        "get_current_user.json",
                        new TypeReference<BkCodeResult<BkCodeUser>>() {}
                ).getData()
        );
    }

    @Test
    public void testGetCurrentUser() {
        BkCodeUser currentUser = bkCodeApi.getUserApi().getCurrentUser();

        Assertions.assertEquals("1d515e15d83972adb785c3551a010bca", currentUser.getId());
        Assertions.assertEquals(
                "zhangsan@tencent.com",
                currentUser.getEmail()
        );
        Assertions.assertEquals("zhangsan", currentUser.getUsername());
        Assertions.assertFalse(currentUser.getAdmin());
    }
}
