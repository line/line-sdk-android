package com.linecorp.linesdk.message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.linecorp.linesdk.utils.JSONUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Represents message data to be sent via the User Message API.
 *
 * <p><p>There are three types of message data.
 * <p>For example,
 * <p>message data using a single user id:
 * <pre>
 * {
 *      "to": "ue97cd0e6646fd73eee758761ca376a15",
 *      "messages": [
 *          {
 *              "type": "buttons",
 *              "text": "ButtonsLayoutTemplate LayoutTemplate",
 *              ...
 *          }
 *      ]
 * }</pre>
 *
 * <p>Message data using multiple user IDs:
 * <pre>
 * {
 *      "to": [
 * 	        "cb7e0f5b861d704ae00cea5105620e730",
 * 	        "ue97cd0e6646fd73eee758761ca376a15"
 *      ],
 *      "messages": [
 *          {
 *              "type": "buttons",
 *              "text": "ButtonsLayoutTemplate LayoutTemplate",
 *              ...
 *          }
 *      ]
 * }</pre>
 *
 * <p>Message data using OTT:
 * <pre>
 * {
 *      "token": "f64e13f2-1658-4dde-bac4-a3bb5fb94bbd",
 *      "messages": [
 *          {
 *              "type": "buttons",
 *              "text": "ButtonsLayoutTemplate LayoutTemplate",
 *              ...
 *          }
 *      ]
 * }</pre>
 * <p>
 */
public class MessageSendRequest {
    @Nullable
    private String targetUserId;
    @Nullable
    private List<String> targetUserIds;
    @Nullable
    private String ott;
    @Nullable
    private List<MessageData> messages;

    /**
     * Create a {@link MessageSendRequest} object with a single recipient and multiple messages.
     *
     * @param targetUserId Required. The recipient's user ID.
     * @param messages     Required. A list of {@link MessageData} objects.
     */
    public static MessageSendRequest createSingleUserType(@NonNull String targetUserId, @NonNull List<MessageData> messages) {
        return new MessageSendRequest().setTargetUserId(targetUserId).setMessages(messages);
    }

    /**
     * Create a {@link MessageSendRequest} object with multiple recipients and multiple messages.
     *
     * @param targetUserIds Required. A list of the recipients' user IDs.
     * @param messages      Required. A list of {@link MessageData} objects.
     */
    public static MessageSendRequest createMultiUsersType(@NonNull List<String> targetUserIds, @NonNull List<MessageData> messages) {
        return new MessageSendRequest().setTargetUserIds(targetUserIds).setMessages(messages);
    }

    /**
     * Construct a {@link MessageSendRequest} object with OTT and multiple messages.
     *
     * @param ott      Required. One time token.
     * @param messages Required. A list of {@link MessageData} objects.
     */
    public static MessageSendRequest createOttType(@NonNull String ott, @NonNull List<MessageData> messages) {
        return new MessageSendRequest().setOtt(ott).setMessages(messages);
    }

    private MessageSendRequest() {
    }

    @NonNull
    public String toJsonString() throws JSONException {
        return toJsonObject().toString();
    }

    private MessageSendRequest setOtt(@NonNull String ott) {
        this.ott = ott;
        return this;
    }

    private MessageSendRequest setTargetUserId(@NonNull String targetUserId) {
        this.targetUserId = targetUserId;
        return this;
    }

    private MessageSendRequest setTargetUserIds(@NonNull List<String> targetUserIds) {
        this.targetUserIds = targetUserIds;
        return this;
    }

    private MessageSendRequest setMessages(@NonNull List<MessageData> messages) {
        this.messages = messages;
        return this;
    }

    @NonNull
    private JSONObject toJsonObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        JSONUtils.put(jsonObject, "to", targetUserId);
        JSONUtils.putArray(jsonObject, "to", targetUserIds);
        JSONUtils.put(jsonObject, "token", ott);
        JSONUtils.putArray(jsonObject, "messages", messages);
        return jsonObject;
    }
}
