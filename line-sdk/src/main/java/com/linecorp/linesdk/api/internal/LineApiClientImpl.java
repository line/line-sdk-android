package com.linecorp.linesdk.api.internal;

import android.text.TextUtils;

import com.linecorp.linesdk.FriendSortField;
import com.linecorp.linesdk.GetFriendsResponse;
import com.linecorp.linesdk.GetGroupsResponse;
import com.linecorp.linesdk.LineAccessToken;
import com.linecorp.linesdk.LineApiError;
import com.linecorp.linesdk.LineApiResponse;
import com.linecorp.linesdk.LineApiResponseCode;
import com.linecorp.linesdk.LineCredential;
import com.linecorp.linesdk.LineFriendshipStatus;
import com.linecorp.linesdk.LineProfile;
import com.linecorp.linesdk.SendMessageResponse;
import com.linecorp.linesdk.api.LineApiClient;
import com.linecorp.linesdk.internal.AccessTokenCache;
import com.linecorp.linesdk.internal.AccessTokenVerificationResult;
import com.linecorp.linesdk.internal.InternalAccessToken;
import com.linecorp.linesdk.internal.RefreshTokenResult;
import com.linecorp.linesdk.internal.nwclient.LineAuthenticationApiClient;
import com.linecorp.linesdk.internal.nwclient.TalkApiClient;
import com.linecorp.linesdk.message.MessageData;
import com.linecorp.linesdk.openchat.MembershipStatus;
import com.linecorp.linesdk.openchat.OpenChatParameters;
import com.linecorp.linesdk.openchat.OpenChatRoomInfo;
import com.linecorp.linesdk.openchat.OpenChatRoomJoinType;
import com.linecorp.linesdk.openchat.OpenChatRoomStatus;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Implementation of {@link LineApiClient}.
 */
public class LineApiClientImpl implements LineApiClient {
    private static final LineApiResponse ERROR_RESPONSE_NO_TOKEN = LineApiResponse.createAsError(
            LineApiResponseCode.INTERNAL_ERROR,
            new LineApiError("access token is null")
    );

    @NonNull
    private final String channelId;
    @NonNull
    private final LineAuthenticationApiClient oauthApiClient;
    @NonNull
    private final TalkApiClient talkApiClient;
    @NonNull
    private final AccessTokenCache accessTokenCache;

    public LineApiClientImpl(
            @NonNull String channelId,
            @NonNull LineAuthenticationApiClient oauthApiClient,
            @NonNull TalkApiClient talkApiClient,
            @NonNull AccessTokenCache accessTokenCache) {
        this.channelId = channelId;
        this.oauthApiClient = oauthApiClient;
        this.talkApiClient = talkApiClient;
        this.accessTokenCache = accessTokenCache;
    }

    @NonNull
    private <T> LineApiResponse<T> callWithAccessToken(@NonNull final APIWithAccessToken<T> api) {
        InternalAccessToken accessToken;

        try {
            accessToken = accessTokenCache.getAccessToken();
        } catch (Exception exception) {
            return LineApiResponse.createAsError(
                    LineApiResponseCode.INTERNAL_ERROR,
                    new LineApiError("get access token fail:" + exception.getMessage())
            );
        }

        if (accessToken == null) {
            return ERROR_RESPONSE_NO_TOKEN;
        } else {
            return api.call(accessToken);
        }
    }

    @Override
    @NonNull
    public LineApiResponse<?> logout() {
        return callWithAccessToken(this::logout);
    }

    @NonNull
    private LineApiResponse<?> logout(@NonNull final InternalAccessToken accessToken) {
        accessTokenCache.clear();
        LineApiResponse<?> response = oauthApiClient.revokeRefreshToken(channelId, accessToken);

        return response;
    }

    @Override
    @NonNull
    public LineApiResponse<LineAccessToken> refreshAccessToken() {
        InternalAccessToken accessToken;
        try {
            accessToken = accessTokenCache.getAccessToken();
        } catch (Exception exception) {
            return LineApiResponse.createAsError(
                    LineApiResponseCode.INTERNAL_ERROR,
                    new LineApiError("get access token fail:" + exception.getMessage())
            );
        }

        if (accessToken == null || TextUtils.isEmpty(accessToken.getRefreshToken())) {
            return LineApiResponse.createAsError(
                    LineApiResponseCode.INTERNAL_ERROR,
                    new LineApiError("access token or refresh token is not found."));
        }
        LineApiResponse<RefreshTokenResult> response =
                oauthApiClient.refreshToken(channelId, accessToken);
        if (!response.isSuccess()) {
            return LineApiResponse.createAsError(
                    response.getResponseCode(), response.getErrorData());
        }
        // Server returns new refreshToken if the current refreshToken must be replaced.
        // Otherwise, returns null.
        RefreshTokenResult refreshTokenResult = response.getResponseData();
        String refreshToken = TextUtils.isEmpty(refreshTokenResult.getRefreshToken())
                ? accessToken.getRefreshToken() : refreshTokenResult.getRefreshToken();
        InternalAccessToken newToken = new InternalAccessToken(
                refreshTokenResult.getAccessToken(),
                refreshTokenResult.getExpiresInMillis(),
                System.currentTimeMillis() /* issuedClientTimeMillis */,
                refreshToken);

        try {
            accessTokenCache.saveAccessToken(newToken);
        } catch(Exception exception) {
            return LineApiResponse.createAsError(
                    LineApiResponseCode.INTERNAL_ERROR,
                    new LineApiError("save access token fail:" + exception.getMessage())
            );
        }

        return LineApiResponse.createAsSuccess(new LineAccessToken(
                newToken.getAccessToken(),
                newToken.getExpiresInMillis(),
                newToken.getIssuedClientTimeMillis()));
    }

    @Override
    @NonNull
    public LineApiResponse<LineCredential> verifyToken() {
        return callWithAccessToken(this::verifyToken);
    }

    @NonNull
    private LineApiResponse<LineCredential> verifyToken(@NonNull InternalAccessToken accessToken) {
        LineApiResponse<AccessTokenVerificationResult> response =
                oauthApiClient.verifyAccessToken(accessToken);
        if (!response.isSuccess()) {
            return LineApiResponse.createAsError(
                    response.getResponseCode(), response.getErrorData());
        }
        AccessTokenVerificationResult verificationResult = response.getResponseData();
        long verifiedClientTimeMillis = System.currentTimeMillis();

        try {
            accessTokenCache.saveAccessToken(
                    new InternalAccessToken(
                            accessToken.getAccessToken(),
                            verificationResult.getExpiresInMillis(),
                            verifiedClientTimeMillis,
                            accessToken.getRefreshToken()));
        } catch(Exception exception) {
            return LineApiResponse.createAsError(
                    LineApiResponseCode.INTERNAL_ERROR,
                    new LineApiError("save access token fail:" + exception.getMessage())
            );
        }

        return LineApiResponse.createAsSuccess(
                new LineCredential(
                        new LineAccessToken(
                                accessToken.getAccessToken(),
                                verificationResult.getExpiresInMillis(),
                                verifiedClientTimeMillis),
                        verificationResult.getScopes()));
    }

    @Override
    @NonNull
    public LineApiResponse<LineAccessToken> getCurrentAccessToken() {
        InternalAccessToken internalAccessToken;
        try {
            internalAccessToken = accessTokenCache.getAccessToken();
        } catch (Exception exception) {
            return LineApiResponse.createAsError(
                    LineApiResponseCode.INTERNAL_ERROR,
                    new LineApiError("get access token fail:" + exception.getMessage())
            );
        }

        if (internalAccessToken == null) {
            return LineApiResponse.createAsError(
                    LineApiResponseCode.INTERNAL_ERROR,
                    new LineApiError("The cached access token does not exist."));
        }

        return LineApiResponse.createAsSuccess(new LineAccessToken(
                internalAccessToken.getAccessToken(),
                internalAccessToken.getExpiresInMillis(),
                internalAccessToken.getIssuedClientTimeMillis()));
    }

    @Override
    @TokenAutoRefresh
    @NonNull
    public LineApiResponse<LineProfile> getProfile() {
        return callWithAccessToken(talkApiClient::getProfile);
    }

    @Override
    @TokenAutoRefresh
    @NonNull
    public LineApiResponse<LineFriendshipStatus> getFriendshipStatus() {
        return callWithAccessToken(talkApiClient::getFriendshipStatus);
    }

    @Override
    @TokenAutoRefresh
    @NonNull
    public LineApiResponse<GetFriendsResponse> getFriends(
            @NonNull FriendSortField sort,
            @Nullable String nextPageRequestToken) {
        return getFriends(sort, nextPageRequestToken, false);
    }

    @Override
    @TokenAutoRefresh
    @NonNull
    public LineApiResponse<GetFriendsResponse> getFriends(
            @NonNull FriendSortField sort,
            @Nullable String nextPageRequestToken,
            boolean isForOttShareMessage) {
        return callWithAccessToken(
                accessToken -> talkApiClient.getFriends(accessToken, sort, nextPageRequestToken, isForOttShareMessage));
    }

    @Override
    @TokenAutoRefresh
    @NonNull
    public LineApiResponse<GetFriendsResponse> getFriendsApprovers(
            FriendSortField sort,
            @Nullable String nextPageRequestToken) {
        return callWithAccessToken(accessToken -> talkApiClient.getFriendsApprovers(accessToken, sort, nextPageRequestToken));
    }

    @Override
    @TokenAutoRefresh
    @NonNull
    public LineApiResponse<GetGroupsResponse> getGroups(@Nullable String nextPageRequestToken) {
        return getGroups(nextPageRequestToken, false);
    }

    @Override
    @TokenAutoRefresh
    @NonNull
    public LineApiResponse<GetGroupsResponse> getGroups(
            @Nullable String nextPageRequestToken,
            boolean isForOttShareMessage) {
        return callWithAccessToken(
                accessToken -> talkApiClient.getGroups(accessToken, nextPageRequestToken, isForOttShareMessage));
    }

    @Override
    @TokenAutoRefresh
    @NonNull
    public LineApiResponse<GetFriendsResponse> getGroupApprovers(
            @NonNull String groupId,
            @Nullable String nextPageRequestToken) {
        return callWithAccessToken(accessToken -> talkApiClient.getGroupApprovers(accessToken, groupId, nextPageRequestToken));
    }

    @Override
    @TokenAutoRefresh
    @NonNull
    public LineApiResponse<String> sendMessage(
            @NonNull String targetUserId,
            @NonNull List<MessageData> messages) {
        return callWithAccessToken(accessToken -> talkApiClient.sendMessage(accessToken, targetUserId, messages));
    }

    @Override
    @TokenAutoRefresh
    @NonNull
    public LineApiResponse<List<SendMessageResponse>> sendMessageToMultipleUsers(
            @NonNull List<String> targetUserIds,
            @NonNull List<MessageData> messages) {
        return sendMessageToMultipleUsers(targetUserIds, messages, false);
    }

    @Override
    @TokenAutoRefresh
    @NonNull
    public LineApiResponse<List<SendMessageResponse>> sendMessageToMultipleUsers(
            @NonNull List<String> targetUserIds,
            @NonNull List<MessageData> messages,
            boolean isOttUsed) {
        return callWithAccessToken(
                accessToken -> talkApiClient.sendMessageToMultipleUsers(accessToken, targetUserIds, messages, isOttUsed)
        );
    }

    @NonNull
    public LineApiResponse<Boolean> getOpenChatAgreementStatus() {
        return callWithAccessToken(accessToken -> talkApiClient.getOpenChatAgreementStatus(accessToken));
    }

    @NonNull
    public LineApiResponse<OpenChatRoomInfo> createOpenChatRoom(@NonNull OpenChatParameters openChatParameters) {
        return callWithAccessToken(accessToken -> talkApiClient.createOpenChatRoom(accessToken, openChatParameters));

    }

    @NonNull
    public LineApiResponse<Boolean> joinOpenChatRoom(@NonNull String roomId, @NonNull String displayName) {
        return callWithAccessToken(accessToken -> talkApiClient.joinOpenChatRoom(accessToken, roomId, displayName));
    };

    @NonNull
    public LineApiResponse<OpenChatRoomStatus> getOpenChatRoomStatus(@NonNull String roomId) {
        return callWithAccessToken(accessToken -> talkApiClient.getOpenChatRoomStatus(accessToken, roomId));

    }

    @NonNull
    public LineApiResponse<MembershipStatus> getOpenChatMembershipStatus(@NonNull String roomId) {
        return callWithAccessToken(accessToken -> talkApiClient.getOpenChatMembershipStatus(accessToken, roomId));
    }

    @NonNull
    public LineApiResponse<OpenChatRoomJoinType> getOpenChatRoomJoinType(@NonNull String roomId) {
        return callWithAccessToken(accessToken -> talkApiClient.getOpenChatRoomJoinType(accessToken, roomId));
    }

    @FunctionalInterface
    private interface APIWithAccessToken<T> {
        LineApiResponse<T> call(InternalAccessToken accessToken);
    }
}
