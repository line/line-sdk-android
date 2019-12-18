package com.linecorp.linesdk.message.template;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * UriAction is a class to contain uri information when a component is clicked in
 * a {@link com.linecorp.linesdk.message.TemplateMessage}.
 * It supports HTTP(s) URLs, LINE custom scheme URLs and other URI schemes.
 */
public class UriAction extends ClickActionForTemplateMessage {
    @NonNull
    private String label;
    @NonNull
    private String uri;

    public UriAction(@NonNull String label, @NonNull String uri) {
        this.type = "uri";
        this.uri = uri;
        this.label = label;
    }

    @NonNull
    @Override
    public JSONObject toJsonObject() throws JSONException {
        JSONObject jsonObject = super.toJsonObject();
        jsonObject.put("uri", uri);
        jsonObject.put("label", label);
        return jsonObject;
    }
}
