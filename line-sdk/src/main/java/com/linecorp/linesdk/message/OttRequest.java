package com.linecorp.linesdk.message;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Represents user IDs to be sent via the OTT(One time token) API.
 *
 * <p>Example:
 * <pre>
 * {
 *      "userIds": [
 *          "Uee2db09bdf8c11d9e63a7bdd48898c7e"
 *      ]
 * }</pre>
 */
public class OttRequest {
    @NonNull
    private List<String> targetUserIds;

    /**
     * Construct a {@link OttRequest} object with multiple recipients.
     *
     * @param targetUserIds Required. A list of the recipients' user IDs.
     */
    public OttRequest(@NonNull List<String> targetUserIds) {
        this.targetUserIds = targetUserIds;
    }

    @NonNull
    public String toJsonString() throws JSONException {
        return toJsonObject().toString();
    }

    @NonNull
    private JSONObject toJsonObject() throws JSONException {
        JSONArray jsonArrayUserIds = new JSONArray();
        for (String id : targetUserIds) {
            jsonArrayUserIds.put(id);
        }
        return new JSONObject().put("userIds", jsonArrayUserIds);
    }
}
