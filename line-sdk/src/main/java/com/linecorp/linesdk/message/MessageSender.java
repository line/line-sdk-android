package com.linecorp.linesdk.message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents a sender of the message.
 */
public class MessageSender implements  Jsonable {
    @NonNull
    private final String label;
    @NonNull
    private final String footerIconUrl;
    @Nullable
    private final String footerLinkUrl;

    /**
     * Construct a {@link MessageSender} object.
     * @param label Required. The description of the sender.
     * @param footerIconUrl Required. The URL of the footer icon.
     * @param footerLinkUrl Optional. The URL of the footer link.
     */
    public MessageSender(@NonNull String label,
                         @NonNull String footerIconUrl,
                         @Nullable String footerLinkUrl
    ) {
        this.label = label;
        this.footerIconUrl = footerIconUrl;
        this.footerLinkUrl = footerLinkUrl;
    }

    @NonNull
    @Override
    public JSONObject toJsonObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("label", label);
        jsonObject.put("iconUrl", footerIconUrl);
        if (footerLinkUrl != null) {
            jsonObject.put("linkUrl", footerLinkUrl);
        }
        return jsonObject;
    }
}
