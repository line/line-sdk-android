package com.linecorp.linesdk.api;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.linecorp.linesdk.LineApiResponse;
import com.linecorp.linesdk.openchat.MembershipStatus;
import com.linecorp.linesdk.openchat.OpenChatParameters;
import com.linecorp.linesdk.openchat.OpenChatRoomInfo;
import com.linecorp.linesdk.openchat.OpenChatRoomStatus;

public interface OpenChatApiClient {
    @NonNull
    LineApiResponse<Boolean> updateAgreementStatus(@NonNull Boolean agreed);

    @NonNull
    LineApiResponse<OpenChatRoomInfo> createOpenChatRoom(@NonNull OpenChatParameters openChatParameters);

    @NonNull
    LineApiResponse<OpenChatRoomStatus> getOpenChatRoomStatus(@NonNull String roomId);

    @NonNull
    LineApiResponse<MembershipStatus> getMembershipStatus(@NonNull String roomId);

    @NonNull
    Intent getCreateOpenChatRoomIntent(@NonNull Activity activity);

    @Nullable
    OpenChatRoomInfo getOpenChatRoomInfoFromIntent(Intent intent);
}
