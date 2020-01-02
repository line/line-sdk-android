package com.linecorp.linesdk.message;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents message content that the channel sends to users.
 */
public abstract class MessageData implements Jsonable {
    @NonNull
    public abstract Type getType();

    @NonNull
    @Override
    public JSONObject toJsonObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", getType().name().toLowerCase());
        return jsonObject;
    }
}
