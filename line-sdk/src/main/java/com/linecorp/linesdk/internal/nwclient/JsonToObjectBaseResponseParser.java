package com.linecorp.linesdk.internal.nwclient;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import com.linecorp.linesdk.internal.nwclient.core.JsonResponseParser;
import com.linecorp.linesdk.internal.nwclient.core.ResponseDataParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * {@link ResponseDataParser} to parse an any object from a json data.
 */
public abstract class JsonToObjectBaseResponseParser<T> implements ResponseDataParser<T> {
    @NonNull
    private final JsonResponseParser jsonResponseParser;

    public JsonToObjectBaseResponseParser() {
        this(new JsonResponseParser());
    }

    public JsonToObjectBaseResponseParser(@NonNull String charsetName) {
        this(new JsonResponseParser(charsetName));
    }

    @VisibleForTesting
    JsonToObjectBaseResponseParser(@NonNull JsonResponseParser jsonResponseParser) {
        this.jsonResponseParser = jsonResponseParser;
    }

    @NonNull
    @Override
    public T getResponseData(@NonNull InputStream inputStream) throws IOException {
        try {
            return parseJsonToObject(jsonResponseParser.getResponseData(inputStream));
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    @NonNull
    protected abstract T parseJsonToObject(@NonNull JSONObject jsonObject) throws JSONException;
}
