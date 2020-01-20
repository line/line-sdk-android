package com.linecorp.linesdk.message;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents a video message to be sent using the
 * {@link com.linecorp.linesdk.message.MessageSendRequest} object.
 */
public class VideoMessage extends MessageData {

    @NonNull
    private final String originalContentUrl;
    @NonNull
    private final String previewImageUrl;

    /**
     * Construct a {@link VideoMessage} object.
     * @param originalContentUrl Required. The URL of the video file.
     * @param previewImageUrl Required. The URL of the preview video file.
     */
    public VideoMessage(@NonNull String originalContentUrl,
                        @NonNull String previewImageUrl) {
        this.originalContentUrl = originalContentUrl;
        this.previewImageUrl = previewImageUrl;
    }

    @NonNull
    @Override
    public Type getType() {
        return Type.VIDEO;
    }

    @NonNull
    @Override
    public JSONObject toJsonObject() throws JSONException {
        JSONObject jsonObject = super.toJsonObject();
        jsonObject.put("originalContentUrl", originalContentUrl);
        jsonObject.put("previewImageUrl", previewImageUrl);
        return jsonObject;
    }
}
