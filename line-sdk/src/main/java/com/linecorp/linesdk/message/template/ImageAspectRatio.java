package com.linecorp.linesdk.message.template;

import android.support.annotation.NonNull;

public enum ImageAspectRatio {
    // 1.51 : 1 (width : height)
    RECTANGLE("rectangle"),
    // 1 : 1
    SQUARE("square");

    @NonNull
    private String serverKey;

    ImageAspectRatio(@NonNull String serverKey) {
        this.serverKey = serverKey;
    }

    @NonNull
    public String getServerKey() {
        return serverKey;
    }
}
