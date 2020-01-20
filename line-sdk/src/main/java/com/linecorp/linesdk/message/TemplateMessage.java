package com.linecorp.linesdk.message;

import androidx.annotation.NonNull;

import com.linecorp.linesdk.message.template.LayoutTemplate;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents a template message to be sent using the
 * {@link com.linecorp.linesdk.message.MessageSendRequest} object.
 */
public class TemplateMessage extends MessageData {

    @NonNull
    private String altText;
    @NonNull
    private LayoutTemplate template;

    @NonNull
    @Override
    public Type getType() {
        return Type.TEMPLATE;
    }

    /**
     * Constructs a {@link TemplateMessage} object.
     * @param altText Required. The alternative text to be shown when the user device doesn't
     *                support template messages.
     * @param template Required. The template message.
     */
    public TemplateMessage(@NonNull String altText, @NonNull LayoutTemplate template) {
        this.altText = altText;
        this.template = template;
    }

    @NonNull
    @Override
    public JSONObject toJsonObject() throws JSONException {
        JSONObject jsonObject = super.toJsonObject();
        jsonObject.put("altText", altText);
        jsonObject.put("template", template.toJsonObject());
        return jsonObject;
    }
}
