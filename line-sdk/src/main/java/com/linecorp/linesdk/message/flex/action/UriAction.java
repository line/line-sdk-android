package com.linecorp.linesdk.message.flex.action;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * When a control associated with this action is tapped, the URI specified in the uri property is opened.
 */
public class UriAction extends Action {

    @NonNull
    private String uri;

    public UriAction(@NonNull String uri, @Nullable String label) {
        super(Type.URI, label);
        this.uri = uri;
    }

    public UriAction(@NonNull String uri) {
        this(uri, null);
    }

    @NonNull
    @Override
    public JSONObject toJsonObject() throws JSONException {
        JSONObject jsonObject = super.toJsonObject();
        jsonObject.put("uri", uri);
        return jsonObject;
    }
}
