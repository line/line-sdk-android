package com.linecorp.linesdk.message;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents an audio message to be sent using the
 * {@link com.linecorp.linesdk.message.MessageSendRequest} object.
 */
public class AudioMessage extends MessageData {

    @NonNull
    private final String originalContentUrl;
    @NonNull
    private final Long durationMillis;

    /**
     * Constructs an {@link AudioMessage} object.
     * @param originalContentUrl Required. The URL of the audio file.
     * @param durationMillis Required. The length of the audio file in milliseconds.
     */
    public AudioMessage(@NonNull String originalContentUrl,
                        @NonNull Long durationMillis) {
        this.originalContentUrl = originalContentUrl;
        this.durationMillis = durationMillis;
    }

    @NonNull
    @Override
    public Type getType() {
        return Type.AUDIO;
    }

    @NonNull
    @Override
    public JSONObject toJsonObject() throws JSONException {
        JSONObject jsonObject = super.toJsonObject();
        jsonObject.put("originalContentUrl", originalContentUrl);
        jsonObject.put("duration", durationMillis);
        return jsonObject;
    }
}
