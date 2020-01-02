package com.linecorp.linesdk.internal.nwclient.core;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * {@link ResponseDataParser} to parse a response data to JSONObject.
 */
public class JsonResponseParser implements ResponseDataParser<JSONObject> {
    @NonNull
    private final StringResponseParser stringResponseParser;

    public JsonResponseParser() {
        stringResponseParser = new StringResponseParser();
    }

    public JsonResponseParser(@NonNull String charsetName) {
        stringResponseParser = new StringResponseParser(charsetName);
    }

    @Override
    @NonNull
    public JSONObject getResponseData(@NonNull InputStream inputStream) throws IOException {
        try {
            return new JSONObject(stringResponseParser.getResponseData(inputStream));
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }
}
