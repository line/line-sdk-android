package com.linecorp.linesdk.internal.nwclient;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;

import com.linecorp.linesdk.BuildConfig;
import com.linecorp.linesdk.FriendSortField;
import com.linecorp.linesdk.GetFriendsResponse;
import com.linecorp.linesdk.GetGroupsResponse;
import com.linecorp.linesdk.LineApiError;
import com.linecorp.linesdk.LineApiResponse;
import com.linecorp.linesdk.LineApiResponseCode;
import com.linecorp.linesdk.LineFriendshipStatus;
import com.linecorp.linesdk.LineGroup;
import com.linecorp.linesdk.LineProfile;
import com.linecorp.linesdk.SendMessageResponse;
import com.linecorp.linesdk.internal.InternalAccessToken;
import com.linecorp.linesdk.internal.nwclient.core.ChannelServiceHttpClient;
import com.linecorp.linesdk.internal.nwclient.core.ResponseDataParser;
import com.linecorp.linesdk.message.MessageData;
import com.linecorp.linesdk.message.MessageSendRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.linecorp.linesdk.utils.UriUtils.buildParams;
import static com.linecorp.linesdk.utils.UriUtils.buildUri;

/**
 * Internal LINE Talk API client to process internal process such as building request data and
 * parsing response data.
 */
public class TalkApiClient {
    private static final String REQUEST_HEADER_ACCESS_TOKEN = "Authorization";
    private static final String TOKEN_TYPE_BEARER = "Bearer";

    private static final String BASE_PATH_COMMON_API = "v2";
    private static final String BASE_PATH_FRIENDSHIP_API = "friendship/v1";
    private static final String BASE_PATH_GRAPH_API = "graph/v2";
    private static final String BASE_PATH_MESSAGE_API = "message/v3";

    private static final ResponseDataParser<LineProfile> PROFILE_PARSER = new ProfileParser();
    private static final ResponseDataParser<LineFriendshipStatus> FRIENDSHIP_STATUS_PARSER =
            new FriendshipStatusParser();
    private static final ResponseDataParser<GetFriendsResponse> FRIENDS_PARSER = new FriendsParser();
    private static final ResponseDataParser<GetGroupsResponse> GROUP_PARSER = new GroupParser();
    private static final ResponseDataParser<String> STRING_PARSER = new StringParser();
    private static final ResponseDataParser<List<SendMessageResponse>> SENDMESSAGE_PARSER =
            new MultiSendResponseParser();

    @NonNull
    private final Uri apiBaseUrl;

    @NonNull
    private final ChannelServiceHttpClient httpClient;

    public TalkApiClient(Context applicationContext, @NonNull Uri apiBaseUrl) {
        this(apiBaseUrl, new ChannelServiceHttpClient(applicationContext, BuildConfig.VERSION_NAME));
    }

    @VisibleForTesting
    TalkApiClient(
            @NonNull Uri apiBaseUrl,
            @NonNull ChannelServiceHttpClient httpClient) {
        this.apiBaseUrl = apiBaseUrl;
        this.httpClient = httpClient;
    }

    @NonNull
    private static Map<String, String> buildRequestHeaders(@NonNull InternalAccessToken accessToken) {
        return buildParams(
                REQUEST_HEADER_ACCESS_TOKEN, TOKEN_TYPE_BEARER + ' ' + accessToken.getAccessToken()
        );
    }

    @NonNull
    public LineApiResponse<LineProfile> getProfile(@NonNull InternalAccessToken accessToken) {
        final Uri uri = buildUri(apiBaseUrl, BASE_PATH_COMMON_API, "profile");
        return httpClient.get(
                uri,
                buildRequestHeaders(accessToken),
                Collections.emptyMap() /* queryParameters */,
                PROFILE_PARSER);
    }

    @NonNull
    public LineApiResponse<LineFriendshipStatus> getFriendshipStatus(@NonNull InternalAccessToken accessToken) {
        final Uri uri = buildUri(apiBaseUrl, BASE_PATH_FRIENDSHIP_API, "status");

        return httpClient.get(
                uri,
                buildRequestHeaders(accessToken),
                Collections.emptyMap() /* queryParameters */,
                FRIENDSHIP_STATUS_PARSER);
    }

    @NonNull
    public LineApiResponse<GetFriendsResponse> getFriends(
            @NonNull InternalAccessToken accessToken,
            @NonNull FriendSortField sortField,
            @Nullable String pageToken
    ) {
        final Uri uri = buildUri(apiBaseUrl, BASE_PATH_GRAPH_API, "friends");
        final Map<String, String> queryParams = buildParams(
                "sort", sortField.getServerKey()
        );
        if (!TextUtils.isEmpty(pageToken)) {
            queryParams.put("pageToken", pageToken);
        }
        return httpClient.get(
                uri,
                buildRequestHeaders(accessToken),
                queryParams,
                FRIENDS_PARSER);
    }

    @NonNull
    public LineApiResponse<GetGroupsResponse> getGroups(
            @NonNull InternalAccessToken accessToken,
            @Nullable String pageToken
    ) {
        final Uri uri = buildUri(apiBaseUrl, BASE_PATH_GRAPH_API, "groups");
        final Map<String, String> queryParams;
        if (!TextUtils.isEmpty(pageToken)) {
            queryParams = buildParams(
                    "pageToken", pageToken
            );
        } else {
            queryParams = Collections.emptyMap();
        }
        return httpClient.get(
                uri,
                buildRequestHeaders(accessToken),
                queryParams,
                GROUP_PARSER);
    }

    @NonNull
    public LineApiResponse<GetFriendsResponse> getFriendsApprovers(
            @NonNull InternalAccessToken accessToken,
            @NonNull FriendSortField sortField,
            @Nullable String nextPageRequestToken
    ) {
        final Uri uri = buildUri(apiBaseUrl, BASE_PATH_GRAPH_API,
                                 "friends", "approvers");
        final Map<String, String> queryParams = buildParams(
                "sort", sortField.getServerKey()
        );
        if (!TextUtils.isEmpty(nextPageRequestToken)) {
            queryParams.put("pageToken", nextPageRequestToken);
        }

        return httpClient.get(
                uri,
                buildRequestHeaders(accessToken),
                queryParams,
                FRIENDS_PARSER);
    }

    @NonNull
    public LineApiResponse<GetFriendsResponse> getGroupApprovers(
            @NonNull InternalAccessToken accessToken,
            @NonNull String groupId,
            @Nullable String nextPageRequestToken
    ) {
        final Uri uri = buildUri(apiBaseUrl, BASE_PATH_GRAPH_API,
                                 "groups", groupId, "approvers");

        final Map<String, String> queryParams;
        if (!TextUtils.isEmpty(nextPageRequestToken)) {
            queryParams = buildParams(
                    "pageToken", nextPageRequestToken
            );
        } else {
            queryParams = Collections.emptyMap();
        }

        return httpClient.get(
                uri,
                buildRequestHeaders(accessToken),
                queryParams,
                FRIENDS_PARSER);
    }

    @NonNull
    public LineApiResponse<String> sendMessage(
            @NonNull InternalAccessToken accessToken,
            @NonNull String receiverId,
            @NonNull List<MessageData> messages
    ) {
        final Uri uri = buildUri(apiBaseUrl, BASE_PATH_MESSAGE_API, "send");

        MessageSendRequest messageSendRequest = new MessageSendRequest(receiverId, messages);
        String postData;
        try {
            postData = messageSendRequest.toJsonObject().toString();
        } catch (JSONException e) {
            return LineApiResponse.createAsError(
                    LineApiResponseCode.INTERNAL_ERROR,
                    new LineApiError(e));
        }

        return httpClient.postWithJson(
                uri,
                buildRequestHeaders(accessToken),
                postData,
                STRING_PARSER);
    }

    @NonNull
    public LineApiResponse<List<SendMessageResponse>> sendMessageToMultipleUsers(
            @NonNull InternalAccessToken accessToken,
            @NonNull List<String> receiverIds,
            @NonNull List<MessageData> messages
    ) {
        final Uri uri = buildUri(apiBaseUrl, BASE_PATH_MESSAGE_API, "multisend");

        MessageSendRequest messageSendRequest = new MessageSendRequest(receiverIds, messages);
        String postData;
        try {
            postData = messageSendRequest.toJsonObject().toString();
        } catch (JSONException e) {
            return LineApiResponse.createAsError(
                    LineApiResponseCode.INTERNAL_ERROR,
                    new LineApiError(e));
        }

        return httpClient.postWithJson(
                uri,
                buildRequestHeaders(accessToken),
                postData,
                SENDMESSAGE_PARSER);
    }

    @VisibleForTesting
    static class FriendsParser extends JsonToObjectBaseResponseParser<GetFriendsResponse> {
        @NonNull
        @Override
        GetFriendsResponse parseJsonToObject(@NonNull JSONObject jsonObject) throws JSONException {
            List<LineProfile> friendList = new ArrayList<>();
            JSONArray friendsArray = jsonObject.getJSONArray("friends");
            for (int i = 0; i < friendsArray.length(); i++) {
                friendList.add(ProfileParser.parseLineProfile(friendsArray.getJSONObject(i)));
            }
            String pageToken = jsonObject.optString("pageToken", null);
            return new GetFriendsResponse(friendList, pageToken);
        }
    }

    @VisibleForTesting
    static class ProfileParser extends JsonToObjectBaseResponseParser<LineProfile> {
        private static LineProfile parseLineProfile(@NonNull JSONObject jsonObject) throws JSONException {
            String pictureUrlStr = jsonObject.optString("pictureUrl", null /* fallback */);
            return new LineProfile(
                    jsonObject.getString("userId"),
                    jsonObject.getString("displayName"),
                    pictureUrlStr == null ? null : Uri.parse(pictureUrlStr),
                    jsonObject.optString("statusMessage", null /* fallback */));
        }

        @NonNull
        @Override
        LineProfile parseJsonToObject(@NonNull JSONObject jsonObject) throws JSONException {
            return parseLineProfile(jsonObject);
        }
    }

    @VisibleForTesting
    static class FriendshipStatusParser extends JsonToObjectBaseResponseParser<LineFriendshipStatus> {
        @NonNull
        @Override
        LineFriendshipStatus parseJsonToObject(@NonNull JSONObject jsonObject) throws JSONException {
            return new LineFriendshipStatus(jsonObject.getBoolean("friendFlag"));
        }
    }

    @VisibleForTesting
    static class GroupParser extends JsonToObjectBaseResponseParser<GetGroupsResponse> {
        @NonNull
        private static LineGroup parseLineGroup(@NonNull JSONObject jsonObject) throws JSONException {
            String pictureUrlStr = jsonObject.optString("pictureUrl", null /* fallback */);
            return new LineGroup(
                    jsonObject.getString("groupId"),
                    jsonObject.getString("groupName"),
                    pictureUrlStr == null ? null : Uri.parse(pictureUrlStr));
        }

        @NonNull
        @Override
        GetGroupsResponse parseJsonToObject(@NonNull JSONObject jsonObject) throws JSONException {
            List<LineGroup> groupList = new ArrayList<>();
            JSONArray groupsArray = jsonObject.getJSONArray("groups");
            for (int i = 0; i < groupsArray.length(); i++) {
                groupList.add(parseLineGroup(groupsArray.getJSONObject(i)));
            }
            String pageToken = jsonObject.optString("pageToken", null);
            return new GetGroupsResponse(groupList, pageToken);
        }
    }

    @VisibleForTesting
    static class StringParser extends JsonToObjectBaseResponseParser<String> {
        @NonNull
        @Override
        String parseJsonToObject(@NonNull JSONObject jsonObject) throws JSONException {
            String status = jsonObject.getString("status");
            return status;
        }
    }

    @VisibleForTesting
    static class MultiSendResponseParser extends JsonToObjectBaseResponseParser<List<SendMessageResponse>> {
        @NonNull
        @Override
        List<SendMessageResponse> parseJsonToObject(@NonNull JSONObject jsonObject) throws JSONException {
            List<SendMessageResponse> sendMessageResponses = new ArrayList<>();
            JSONArray resultArray = jsonObject.getJSONArray("results");
            for (int i = 0; i < resultArray.length(); i++) {
                sendMessageResponses.add(SendMessageResponse.fromJsonObject(resultArray.getJSONObject(i)));
            }
            return sendMessageResponses;
        }
    }
}
