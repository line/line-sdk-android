package com.linecorp.linesdk.openchat;

/**
 * The membership state of current user to the room.
 */
public enum MembershipStatus {
    // The user has already joined the room.
    JOINED,
    // The user is not a member of the room yet.
    NOT_JOINED
}
