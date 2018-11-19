package com.linecorp.linesdk.message;

import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Jsonable is an interface to declare that the object provides
 * a function to convert itself to json object
 */
public interface Jsonable {
    @NonNull
    JSONObject toJsonObject() throws JSONException;
}
