package com.linecorp.linesdk.openchat;

/**
 * The joining type of an Open Chat room. The value indicates what is required if a user wants to join the room.
 */
public enum OpenChatRoomJoinType {
    // The room is public and open for anyone to join.
    NONE,
    // A user needs to request to join the room. Only approved users can join.
    // The admins or authority users of the room can approve the request.
    APPROVAL,
    // A user needs to input the join code to join the room.
    CODE,
    // The received state isn't defined yet in the current version.
    // Try to upgrade to the latest SDK version if you encountered this.
    UNDEFINED
}
