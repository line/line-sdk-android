package com.linecorp.linesdk.message.template;

/**
 * Type defines supported template types used in TemplateMessage
 */
public enum Type {
    BUTTONS("buttons"),
    CONFIRM("confirm"),
    CAROUSEL("carousel"),
    IMAGE_CAROUSEL("image_carousel");

    private final String serverKey;

    Type(String serverKey) {
        this.serverKey = serverKey;
    }

    public String getServerKey() {
        return serverKey;
    }

}
