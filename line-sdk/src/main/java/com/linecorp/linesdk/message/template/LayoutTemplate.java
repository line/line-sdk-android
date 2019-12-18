package com.linecorp.linesdk.message.template;

import androidx.annotation.NonNull;

import com.linecorp.linesdk.message.Jsonable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * LayoutTemplate is a base class for all different templates inside
 * {@link com.linecorp.linesdk.message.TemplateMessage}.
 */
abstract public class LayoutTemplate implements Jsonable {
    @NonNull
    private final Type type;

    public LayoutTemplate(@NonNull Type type) {
        this.type = type;
    }

    @NonNull
    @Override
    public JSONObject toJsonObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", type.getServerKey());
        return jsonObject;
    }
}
