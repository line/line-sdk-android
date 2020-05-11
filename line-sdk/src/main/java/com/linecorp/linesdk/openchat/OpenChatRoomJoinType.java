package com.linecorp.linesdk.openchat;

/**
 * The joining type of an Open Chat room. The value indicates what is required if a user want to join the room.
 */
public enum OpenChatRoomJoinType {
    // The room is public and open for anyone to join.
    NONE,
    // A user needs to request to join the room, only approved user can join.
    // The admins or authority users of the room can approve the request.
    APPROVAL,
    // A user needs to input the join code to join the room.
    CODE,
    // The received state is not defined yet in current version.
    // Try to upgrade to the latest SDK version if you encountered this.
    UNDEFINED
}
