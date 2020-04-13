package com.linecorp.linesdk.message;

import androidx.annotation.NonNull;

import com.linecorp.linesdk.message.flex.container.FlexMessageContainer;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents a Flex Message to be sent using the {@link MessageSendRequest} object.
 *
 * To create a {@link FlexMessage} object, firstly create a container, and then pass it with the
 * {@link FlexMessage#altText} parameter to the initializer.
 *
 * For more information, please refer to <a href="https://developers.line.biz/en/docs/messaging-api/message-types/#flex-messages">Flex messages</a>.
 */
public class FlexMessage extends MessageData {

    @NonNull
    private String altText;
    @NonNull
    private FlexMessageContainer contents;

    /**
     * Constructs a {@link FlexMessage} object.
     * @param altText Required. The alternative text to be shown when the user device doesn't
     *                support Flex Messages.
     * @param contents Required. The Flex Message.
     */
    public FlexMessage(@NonNull String altText, @NonNull FlexMessageContainer contents) {
        this.altText = altText;
        this.contents = contents;
    }

    @NonNull
    @Override
    public Type getType() {
        return Type.FLEX;
    }

    @NonNull
    @Override
    public JSONObject toJsonObject() throws JSONException {
        JSONObject jsonObject = super.toJsonObject();
        jsonObject.put("altText", altText);
        jsonObject.put("contents", contents.toJsonObject());
        return jsonObject;
    }
}
