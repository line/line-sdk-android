package com.linecorp.linesdk.message.template;

import androidx.annotation.NonNull;

/**
 * Image Scale types for template messages
 */
enum ImageScaleType {
    /**
     * Scale the image to completely fill the image area.
     */
    COVER("cover"),
    /**
     * Scale the image to fit inside the image area.
     */
    CONTAIN("contain");

    @NonNull
    private String serverKey;

    ImageScaleType(@NonNull String serverKey) {
        this.serverKey = serverKey;
    }

    @NonNull
    public String getServerKey() {
        return serverKey;
    }
}
