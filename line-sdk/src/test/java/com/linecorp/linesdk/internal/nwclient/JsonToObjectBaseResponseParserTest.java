package com.linecorp.linesdk.internal.nwclient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.linecorp.linesdk.internal.nwclient.core.JsonResponseParser;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;

/**
 * Test for {@link JsonToObjectBaseResponseParser}.
 */
public class JsonToObjectBaseResponseParserTest {
    private static final JSONObject JSON_OBJECT = new JSONObject();
    private static final Object EXPECTED_RESULT = new Object();

    @Mock
    private InputStream inputStream;
    @Mock
    private JsonResponseParser jsonResponseParser;

    private TargetClass target;

    private static class TargetClass extends JsonToObjectBaseResponseParser<Object> {
        private boolean isJsonExceptionThrown;
        @Nullable
        private JSONObject latestJsonObject;

        private TargetClass(@NonNull JsonResponseParser jsonResponseParser) {
            super(jsonResponseParser);
        }

        @NonNull
        @Override
        protected Object parseJsonToObject(@NonNull JSONObject jsonObject) throws JSONException {
            latestJsonObject = jsonObject;
            if (isJsonExceptionThrown) {
                throw new JSONException("test");
            }
            return EXPECTED_RESULT;
        }
    }

    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        target = new TargetClass(jsonResponseParser);
        doReturn(JSON_OBJECT).when(jsonResponseParser).getResponseData(any(InputStream.class));
    }

    @Test
    public void testSuccessfullyResponse() throws IOException {
        Object actualResult = target.getResponseData(inputStream);
        assertSame(EXPECTED_RESULT, actualResult);
        assertSame(JSON_OBJECT, target.latestJsonObject);
    }

    @Test(expected = IOException.class)
    public void testIllegalJsonData() throws IOException {
        target.isJsonExceptionThrown = true;
        target.getResponseData(inputStream);
    }
}
