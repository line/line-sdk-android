package com.linecorp.linesdk.api.internal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import com.linecorp.linesdk.BuildConfig;
import com.linecorp.linesdk.LineApiError;
import com.linecorp.linesdk.LineApiResponse;
import com.linecorp.linesdk.LineApiResponseCode;
import com.linecorp.linesdk.api.BaseApiClient;
import com.linecorp.linesdk.api.OpenChatApiClient;
import com.linecorp.linesdk.internal.AccessTokenCache;
import com.linecorp.linesdk.internal.InternalAccessToken;
import com.linecorp.linesdk.internal.nwclient.JsonToObjectBaseResponseParser;
import com.linecorp.linesdk.internal.nwclient.core.ChannelServiceHttpClient;
import com.linecorp.linesdk.openchat.MembershipStatus;
import com.linecorp.linesdk.openchat.OpenChatParameters;
import com.linecorp.linesdk.openchat.OpenChatRoomInfo;
import com.linecorp.linesdk.openchat.OpenChatRoomStatus;
import com.linecorp.linesdk.openchat.ui.CreateOpenChatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of {@link OpenChatApiClient}.
 */
public class OpenChatApiClientImpl extends BaseApiClient implements OpenChatApiClient {
    private static final LineApiResponse ERROR_RESPONSE_NO_TOKEN = LineApiResponse.createAsError(
            LineApiResponseCode.INTERNAL_ERROR,
            new LineApiError("access token is null")
    );

    @NonNull
    private final AccessTokenCache accessTokenCache;

    @NonNull
    private final String channelId;

    public OpenChatApiClientImpl(
            Context applicationContext,
            @NonNull Uri apiBaseUrl,
            @NonNull String channelId) {
            super(apiBaseUrl, new ChannelServiceHttpClient(applicationContext, BuildConfig.VERSION_NAME));
        this.channelId = channelId;
        this.accessTokenCache = new AccessTokenCache(applicationContext, channelId);
    }

    @NonNull
    @Override
    public LineApiResponse<Boolean> updateAgreementStatus(@NonNull Boolean agreed) {
        final InternalAccessToken accessToken = accessTokenCache.getAccessToken();
        if (accessToken == null) return ERROR_RESPONSE_NO_TOKEN;

        final Uri uri = Uri.parse(apiBaseUrl + "square/v1/terms/agreement");
        final String postData = String.format("{ \"agreed\": %s }", (agreed) ? "true" : "false");

        return httpClient.putWithJson(
                uri,
                buildRequestHeaders(accessToken),
                postData,
                null);
    }

    @NonNull
    @Override
    public LineApiResponse<OpenChatRoomInfo> createOpenChatRoom(@NonNull OpenChatParameters openChatParameters) {
        final InternalAccessToken accessToken = accessTokenCache.getAccessToken();
        if (accessToken == null) return ERROR_RESPONSE_NO_TOKEN;

        final Uri uri = Uri.parse(apiBaseUrl + "square/v1/square");

        return httpClient.postWithJson(
                uri,
                buildRequestHeaders(accessToken),
                openChatParameters.toJsonString(),
                new OpenChatRoomInfoParser());
    }

    @NonNull
    @Override
    public LineApiResponse<OpenChatRoomStatus> getOpenChatRoomStatus(@NonNull String roomId) {
        final InternalAccessToken accessToken = accessTokenCache.getAccessToken();
        if (accessToken == null) return ERROR_RESPONSE_NO_TOKEN;

        final Uri uri = Uri.parse(apiBaseUrl + "square/v1/square/" + roomId + "/status");
        final Map<String, String> queryParameters = new HashMap<>();
        queryParameters.put("squareMid", roomId);

        return httpClient.get(
                uri,
                buildRequestHeaders(accessToken),
                queryParameters,
                new OpenChatRoomStatusParser());
    }

    @NonNull
    @Override
    public LineApiResponse<MembershipStatus> getMembershipStatus(@NonNull String roomId) {
        final InternalAccessToken accessToken = accessTokenCache.getAccessToken();
        if (accessToken == null) return ERROR_RESPONSE_NO_TOKEN;

        final Uri uri = Uri.parse(apiBaseUrl + "square/v1/square/" + roomId + "/membership");

        return httpClient.get(
                uri,
                buildRequestHeaders(accessToken),
                Collections.emptyMap(),
                new MembershipStatusParser());
    }

    @Override
    public Intent getCreateOpenChatRoomIntent(@NonNull Activity activity) {
        return CreateOpenChatActivity.createIntent(activity, apiBaseUrl.toString(), channelId);
    }

    @Override
    public OpenChatRoomInfo getOpenChatRoomInfoFromIntent(Intent intent) {
        return intent.getParcelableExtra(CreateOpenChatActivity.ARG_OPEN_CHATROOM_INFO);
    }

    @VisibleForTesting
    private static class OpenChatRoomInfoParser extends JsonToObjectBaseResponseParser<OpenChatRoomInfo> {
        @NonNull
        @Override
        protected OpenChatRoomInfo parseJsonToObject(@NonNull JSONObject jsonObject) throws JSONException {
            return new OpenChatRoomInfo(jsonObject.getString("squareMid"),
                    jsonObject.getString("url"));
        }
    }

    @VisibleForTesting
    private static class OpenChatRoomStatusParser extends JsonToObjectBaseResponseParser<OpenChatRoomStatus> {
        @NonNull
        @Override
        protected OpenChatRoomStatus parseJsonToObject(@NonNull JSONObject jsonObject) throws JSONException {
            String status = jsonObject.getString("status").toUpperCase();
            return OpenChatRoomStatus.valueOf(status);
        }
    }

    @VisibleForTesting
    private static class MembershipStatusParser extends JsonToObjectBaseResponseParser<MembershipStatus> {
        @NonNull
        @Override
        protected MembershipStatus parseJsonToObject(@NonNull JSONObject jsonObject) throws JSONException {
            String state = jsonObject.getString("state").toUpperCase();
            return MembershipStatus.valueOf(state);
        }
    }
}
