package com.linecorp.linesdk;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @hide
 */
public class SendMessageResponse {
    public enum Status {
        OK,
        DISCARDED
    }

    @NonNull
    private String receiverId;

    @NonNull
    private Status status;

    public SendMessageResponse(@NonNull String targetUserId, @NonNull Status status) {
        this.receiverId = targetUserId;
        this.status = status;
    }

    @NonNull
    public String getTargetUserId() {
        return receiverId;
    }

    @NonNull
    public Status getStatus() {
        return status;
    }

    @NonNull
    public static SendMessageResponse fromJsonObject(@NonNull JSONObject jsonObject) throws JSONException {
        SendMessageResponse.Status status;
        if (jsonObject.get("status").equals(SendMessageResponse.Status.OK.name().toLowerCase())) {
            status = SendMessageResponse.Status.OK;
        } else {
            status = SendMessageResponse.Status.DISCARDED;
        }
        return new SendMessageResponse( jsonObject.getString("to"), status);
    }

    @Override
    public String toString() {
        return "SendMessageResponse{" +
                       "receiverId='" + receiverId + '\'' +
                       ", status='" + status + '\'' +
                       '}';
    }
}
