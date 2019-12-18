package com.linecorp.linesdk.api;

import com.linecorp.linesdk.LineApiResponse;
import com.linecorp.linesdk.openchat.MembershipStatus;
import com.linecorp.linesdk.openchat.OpenChatRoomInfo;
import com.linecorp.linesdk.openchat.OpenChatParameters;
import com.linecorp.linesdk.openchat.OpenChatRoomStatus;

public interface OpenChatApiClient {
    LineApiResponse<Boolean> getAgreementStatus();

    LineApiResponse<Boolean> updateAgreementStatus(Boolean agreed);

    LineApiResponse<OpenChatRoomInfo> createOpenChatRoom(OpenChatParameters openChatParameters);

    LineApiResponse<OpenChatRoomStatus> getOpenChatRoomStatus(String roomId);

    LineApiResponse<MembershipStatus> getMembershipStatus(String roomId);
}
