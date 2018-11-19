package com.linecorp.linesdk.message;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Represents message data to be sent via the User Message API.
 */
public class MessageSendRequest {
    @Nullable
    private String targetUserId;
    @Nullable
    private List<String> targetUserIds;
    @NonNull
    private final List<MessageData> messages;

    /**
     * Construct a {@link MessageSendRequest} object with multiple recipients and multiple messages.
     * @param targetUserIds Required. A list of the recipients' user IDs.
     * @param messages Required. A list of {@link MessageData} objects.
     */
    public MessageSendRequest(@NonNull List<String> targetUserIds, @NonNull List<MessageData> messages) {
        this.targetUserIds = targetUserIds;
        this.messages = messages;
    }

    /**
     * Construct a {@link MessageSendRequest} object with a single recipient and multiple messages.
     * @param targetUserId Required. The recipient's user ID.
     * @param messages Required. A list of {@link MessageData} objects.
     */
    public MessageSendRequest(@NonNull String targetUserId, @NonNull List<MessageData> messages) {
        this.targetUserId = targetUserId;
        this.messages = messages;
    }

    @NonNull
    public JSONObject toJsonObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();

        if (targetUserId != null) {
            jsonObject.put("to", targetUserId);
        }

        if (targetUserIds != null){
            JSONArray toArray = new JSONArray();
            for (String id : targetUserIds) {
                toArray.put(id);
            }
            jsonObject.put("to", toArray);
        }

        JSONArray messageArray = new JSONArray();
        for (MessageData message : messages) {
            messageArray.put(message.toJsonObject());
        }

        jsonObject.put("messages", messageArray);
        return jsonObject;
    }
}
