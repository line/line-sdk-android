package com.linecorp.linesdk.internal.nwclient.core;

import com.linecorp.linesdk.TestStringInputStream;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test for {@link StringResponseParser}.
 */
public class StringResponseParserTest {
    private static final String CHARSET_NAME = "UTF-8";
    private static final String RESPONSE_DATA = "response data for test";

    private StringResponseParser target;

    @Before
    public void setUp() {
        target = new StringResponseParser(CHARSET_NAME);
    }

    @Test
    public void testGetResponseData() throws Exception {
        assertEquals(RESPONSE_DATA,
                target.getResponseData(new TestStringInputStream(RESPONSE_DATA, CHARSET_NAME)));
    }
}