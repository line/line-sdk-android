package com.linecorp.linesdk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class to create json data for test.
 */
public class TestJsonDataBuilder {
    @NonNull
    private final JSONObject jsonObject;

    public TestJsonDataBuilder() {
        jsonObject = new JSONObject();
    }

    @NonNull
    public TestJsonDataBuilder put(@NonNull String key, @Nullable String value) {
        try {
            jsonObject.put(key, value);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    @NonNull
    public JSONObject build() {
        return jsonObject;
    }

    @NonNull
    public String buildAsString() {
        return jsonObject.toString();
    }
}
