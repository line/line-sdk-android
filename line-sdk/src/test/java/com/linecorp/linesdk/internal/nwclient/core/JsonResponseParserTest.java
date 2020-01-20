package com.linecorp.linesdk.internal.nwclient.core;

import com.linecorp.linesdk.BuildConfig;
import com.linecorp.linesdk.TestConfig;
import com.linecorp.linesdk.TestStringInputStream;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test for {@link JsonResponseParser}.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = TestConfig.TARGET_SDK_VERSION)
public class JsonResponseParserTest {
    private static final String CHARSET_NAME = "UTF-8";

    private JsonResponseParser target;

    @Before
    public void setUp() {
        target = new JsonResponseParser(CHARSET_NAME);
    }

    @Test
    public void testGetResponseData() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("testStr", "string data");
        jsonObject.put("testNum", 123);
        jsonObject.put("testBoolean", true);

        JSONObject responseData =
                target.getResponseData(new TestStringInputStream(jsonObject.toString(), CHARSET_NAME));
        assertEquals("string data", responseData.optString("testStr"));
        assertEquals(123, responseData.optInt("testNum"));
        assertTrue(responseData.optBoolean("testBoolean"));
    }
}
