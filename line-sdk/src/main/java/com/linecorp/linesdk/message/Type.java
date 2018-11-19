package com.linecorp.linesdk.message;

/**
 * Message types that the {@link MessageSendRequest} class supports.
 */
public enum Type {

    /**
     * The text message.
     */
    TEXT,

    /**
     * The image message.
     */
    IMAGE,

    /**
     * The video message.
     */
    VIDEO,

    /**
     * The audio message.
     */
    AUDIO,

    /**
     * The location data message.
     */
    LOCATION,

    /**
     * The template message that has a predefined layout.
     */
    TEMPLATE,

    /**
     * The Flex Message that has a customized layout.
     */
    FLEX
}
