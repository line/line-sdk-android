package com.linecorp.linesdk.openchat;

/**
 * The status of an Open Chat room.
 */
public enum OpenChatRoomStatus {
    // The room is alive. Other users can join it.
    ALIVE,
    // The room is already deleted.
    DELETED,
    // The room is suspended for some reason.
    SUSPENDED
}
