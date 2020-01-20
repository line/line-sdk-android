package com.linecorp.linesdk.message.flex.component;

import androidx.annotation.NonNull;

import com.linecorp.linesdk.LineSdkTestRunner;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BaseFlexComponentTest extends LineSdkTestRunner {

    protected FlexMessageComponent component;

    @Test
    public void initialize() {
        // do nothing
    }

    protected void messageShouldBe(@NonNull String expected) throws JSONException {
        assertEquals(new JSONObject(expected).toString(), component.toJsonObject().toString());
    }
}
