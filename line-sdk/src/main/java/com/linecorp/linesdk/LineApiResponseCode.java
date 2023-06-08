package com.linecorp.linesdk;

/**
 * Represents a response code returned from the LINE Platform.
 */
public enum LineApiResponseCode {
    /**
     * The request was successful.
     */
    SUCCESS,
    /**
     * The request was canceled.
     */
    CANCEL,
    /**
     * A network error occurred.
     */
    NETWORK_ERROR,
    /**
     * A server error occurred.
     */
    SERVER_ERROR,
    /**
     * An authentication agent error occurred.
     */
    AUTHENTICATION_AGENT_ERROR,
    /**
     * An internal error occurred.
     */
    INTERNAL_ERROR
}
