package com.linecorp.linesdk.message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.linecorp.linesdk.utils.JSONUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents an image message to be sent using the
 * {@link com.linecorp.linesdk.message.MessageSendRequest} object.
 */
public class ImageMessage extends MessageData {

    @NonNull
    private final String originalContentUrl;
    @NonNull
    private final String previewImageUrl;

    private Boolean animated = false;
    @Nullable
    private String extension;
    @Nullable
    private Long fileSize;
    @Nullable
    private MessageSender sentBy;

    /**
     * Constructs an {@link ImageMessage} object.
     * @param originalContentUrl Required. The URL of the image file.
     * @param previewImageUrl Required. The URL of the preview image file.
     */
    public ImageMessage(@NonNull String originalContentUrl,
                        @NonNull String previewImageUrl) {
        this.originalContentUrl = originalContentUrl;
        this.previewImageUrl = previewImageUrl;
    }

    public void setAnimated(Boolean animated) {
        this.animated = animated;
    }

    public void setExtension(@Nullable String extension) {
        this.extension = extension;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public void setSentBy(@Nullable MessageSender sentBy) {
        this.sentBy = sentBy;
    }


    @NonNull
    @Override
    public Type getType() {
        return Type.IMAGE;
    }

    @NonNull
    @Override
    public JSONObject toJsonObject() throws JSONException {
        JSONObject jsonObject = super.toJsonObject();
        jsonObject.put("originalContentUrl", originalContentUrl);
        jsonObject.put("previewImageUrl", previewImageUrl);
        jsonObject.put("animated", animated);
        jsonObject.put("extension", extension);
        jsonObject.put("fileSize", fileSize);
        JSONUtils.put(jsonObject, "sentBy", sentBy);

        return jsonObject;
    }
}
