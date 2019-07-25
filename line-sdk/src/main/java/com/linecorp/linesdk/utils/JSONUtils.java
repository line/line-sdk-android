package com.linecorp.linesdk.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.linecorp.linesdk.message.Jsonable;
import com.linecorp.linesdk.message.Stringable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to support JSONObject operations.
 */
public final class JSONUtils {
    public static <T> void put(
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @Nullable final T value
    ) throws JSONException {
        if (value == null) { return; }

        if (value instanceof Jsonable) {
            jsonObject.put(key, ((Jsonable) value).toJsonObject());
        } else if (value instanceof Stringable) {
            jsonObject.put(key, ((Stringable) value).name().toLowerCase());
        } else {
            jsonObject.put(key, value);
        }
    }

    public static <T> void putArray(
            @NonNull final JSONObject jsonObject,
            @NonNull final String key,
            @Nullable final List<T> objectList
    ) throws JSONException {
        if (objectList == null) { return; }

        JSONArray toArray = new JSONArray();
        for (T object : objectList) {
            if (object instanceof Jsonable) {
                toArray.put(((Jsonable) object).toJsonObject());
            } else {
                toArray.put(object);
            }
        }
        jsonObject.put(key, toArray);
    }

    public static List<String> toStringList(@NonNull final JSONArray array) throws JSONException {
        if (array == null) {
            return null;
        }

        final List<String> list = new ArrayList<>();
        for (int idx = 0; idx < array.length(); idx++) {
            list.add(array.getString(idx));
        }

        return list;
    }
}
