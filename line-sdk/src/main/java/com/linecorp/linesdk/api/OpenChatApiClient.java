package com.linecorp.linesdk.api;

import androidx.annotation.NonNull;

import com.linecorp.linesdk.LineApiResponse;
import com.linecorp.linesdk.openchat.MembershipStatus;
import com.linecorp.linesdk.openchat.OpenChatParameters;
import com.linecorp.linesdk.openchat.OpenChatRoomInfo;
import com.linecorp.linesdk.openchat.OpenChatRoomStatus;

public interface OpenChatApiClient {

    @NonNull
    LineApiResponse<Boolean> getAgreementStatus();

    @NonNull
    LineApiResponse<Boolean> updateAgreementStatus(@NonNull Boolean agreed);

    @NonNull
    LineApiResponse<OpenChatRoomInfo> createOpenChatRoom(@NonNull OpenChatParameters openChatParameters);

    @NonNull
    LineApiResponse<OpenChatRoomStatus> getOpenChatRoomStatus(@NonNull String roomId);

    @NonNull
    LineApiResponse<MembershipStatus> getMembershipStatus(@NonNull String roomId);
}
