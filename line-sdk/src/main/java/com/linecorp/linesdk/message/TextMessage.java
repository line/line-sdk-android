package com.linecorp.linesdk.message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents a text message to be sent using the
 * {@link com.linecorp.linesdk.message.MessageSendRequest} object.
 */
public class TextMessage extends MessageData {

    @NonNull
    private final String text;
    @Nullable
    private final MessageSender sendBy;


    /**
     * Construct a {@link TextMessage} object with text.
     * @param text Required. The text to be sent.
     */
    public TextMessage(@NonNull String text) {
        this.text = text;
        sendBy = null;
    }

    /**
     * Construct a {@link TextMessage} object with text and a message sender.
     * @param text Required. The text to be sent.
     * @param messageSender Required. The sender of the message.
     */
    public TextMessage(@NonNull String text, @Nullable MessageSender messageSender) {
        this.text = text;
        this.sendBy = messageSender;
    }

    @NonNull
    @Override
    public Type getType() {
        return Type.TEXT;
    }

    @NonNull
    @Override
    public JSONObject toJsonObject() throws JSONException {
        JSONObject jsonObject = super.toJsonObject();
        jsonObject.put("text", text);
        if (sendBy != null) {
            jsonObject.put("sentBy", sendBy.toJsonObject());
        }
        return jsonObject;
    }
}
