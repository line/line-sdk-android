package com.linecorp.linesdk;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

/**
 * Test for {@link ScopeTest}.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = TestConfig.TARGET_SDK_VERSION)
public class ScopeTest {
    private static final List<Scope> TEST_SCOPE_LIST = Arrays.asList(
            Scope.PROFILE,
            Scope.FRIEND,
            Scope.GROUP,
            Scope.OPENID_CONNECT,
            Scope.OC_EMAIL,
            Scope.OC_ADDRESS
    );

    private static final List<String> TEST_SCOPE_CODE_LIST = Arrays.asList(
            Scope.PROFILE.getCode(),
            Scope.FRIEND.getCode(),
            Scope.GROUP.getCode(),
            Scope.OPENID_CONNECT.getCode(),
            Scope.OC_EMAIL.getCode(),
            Scope.OC_ADDRESS.getCode()
    );

    private static final String TEST_SCOPE_CODE_STR =
            Scope.PROFILE.getCode() + " "
            + Scope.FRIEND.getCode() + " "
            + Scope.GROUP.getCode() + " "
            + Scope.OPENID_CONNECT.getCode() + " "
            + Scope.OC_EMAIL.getCode() + " "
            + Scope.OC_ADDRESS.getCode();

    @Test
    public void testCreateNewValue() {
        new Scope("my_new_scope_01");
        new Scope("my_new_scope_02");
        new Scope("my_new_scope_03");
    }

    @Test
    public void testFindScope() {
        for (final Scope expectedScope : TEST_SCOPE_LIST) {
            final Scope actualScope = Scope.findScope(expectedScope.getCode());

            assertSame(expectedScope, actualScope);
        }
    }

    @Test
    public void testJoin() {
        final String codeStr = Scope.join(TEST_SCOPE_LIST);
        assertEquals(TEST_SCOPE_CODE_STR, codeStr);
    }

    @Test
    public void testParseToList() {
        final List<Scope> scopeList = Scope.parseToList(TEST_SCOPE_CODE_STR);
        assertEquals(TEST_SCOPE_LIST, scopeList);
    }

    @Test
    public void testConvertToScopeList() {
        final List<Scope> scopeList = Scope.convertToScopeList(TEST_SCOPE_CODE_LIST);
        assertEquals(TEST_SCOPE_LIST, scopeList);
    }

    @Test
    public void testConvertToCodeList() {
        final List<String> codeList = Scope.convertToCodeList(TEST_SCOPE_LIST);
        assertEquals(TEST_SCOPE_CODE_LIST, codeList);
    }
}
