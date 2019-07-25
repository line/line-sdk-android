package com.linecorp.linesdk.message.template;

import androidx.annotation.NonNull;

import com.linecorp.linesdk.message.Jsonable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * ClickActionForTemplateMessage is a class to describe what action should be taken when a component is clicked in
 * a {@link com.linecorp.linesdk.message.TemplateMessage}.
 */
public class ClickActionForTemplateMessage implements Jsonable {
    protected String type;

    @NonNull
    @Override
    public JSONObject toJsonObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", type);
        return jsonObject;
    }
}
