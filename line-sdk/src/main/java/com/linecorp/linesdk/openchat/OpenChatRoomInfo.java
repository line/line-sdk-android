package com.linecorp.linesdk.openchat;

public class OpenChatRoomInfo {
    private String roomId;
    private String landingPageUrl;

    public OpenChatRoomInfo(String roomId, String landingPageUrl) {
        this.roomId = roomId;
        this.landingPageUrl = landingPageUrl;
    }

    public String getRoomId() {
        return roomId;
    }

    public String getLandingPageUrl() {
        return landingPageUrl;
    }

    @Override
    public String toString() {
        return "OpenChatRoomInfo{" +
                       "roomId='" + roomId + '\'' +
                       ", landingPageUrl='" + landingPageUrl + '\'' +
                       '}';
    }
}
