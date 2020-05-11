package com.linecorp.linesdk.internal.nwclient;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.linecorp.linesdk.BuildConfig;
import com.linecorp.linesdk.FriendSortField;
import com.linecorp.linesdk.GetFriendsResponse;
import com.linecorp.linesdk.GetGroupsResponse;
import com.linecorp.linesdk.LineApiError;
import com.linecorp.linesdk.LineApiResponse;
import com.linecorp.linesdk.LineApiResponseCode;
import com.linecorp.linesdk.LineFriendProfile;
import com.linecorp.linesdk.LineFriendshipStatus;
import com.linecorp.linesdk.LineGroup;
import com.linecorp.linesdk.LineProfile;
import com.linecorp.linesdk.SendMessageResponse;
import com.linecorp.linesdk.internal.InternalAccessToken;
import com.linecorp.linesdk.internal.nwclient.core.ChannelServiceHttpClient;
import com.linecorp.linesdk.internal.nwclient.core.ResponseDataParser;
import com.linecorp.linesdk.message.MessageData;
import com.linecorp.linesdk.message.MessageSendRequest;
import com.linecorp.linesdk.message.OttRequest;
import com.linecorp.linesdk.openchat.MembershipStatus;
import com.linecorp.linesdk.openchat.OpenChatParameters;
import com.linecorp.linesdk.openchat.OpenChatRoomInfo;
import com.linecorp.linesdk.openchat.OpenChatRoomJoinType;
import com.linecorp.linesdk.openchat.OpenChatRoomStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

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
    private static final String BASE_PATH_OPENCHAT_API = "openchat/v1";
    /* package */ static final String BASE_PATH_GRAPH_API = "graph/v2";
    /* package */ static final String BASE_PATH_MESSAGE_API = "message/v3";
    /* package */ static final String PATH_FRIENDS = "friends";
    /* package */ static final String PATH_OTS_FRIENDS = "ots/friends";
    /* package */ static final String PATH_GROUPS = "groups";
    /* package */ static final String PATH_OTS_GROUPS = "ots/groups";
    /* package */ static final String PATH_OTT_SHARE = "ott/share";
    /* package */ static final String PATH_OTT_ISSUE = "ott/issue";

    @NonNull
    private final Uri apiBaseUrl;

    @NonNull
    private final ChannelServiceHttpClient httpClient;

    private static final ResponseDataParser<LineProfile> PROFILE_PARSER = new ProfileParser();
    private static final ResponseDataParser<LineFriendshipStatus> FRIENDSHIP_STATUS_PARSER = new FriendshipStatusParser();
    private static final ResponseDataParser<GetFriendsResponse> FRIENDS_PARSER = new FriendsParser();
    private static final ResponseDataParser<GetGroupsResponse> GROUP_PARSER = new GroupParser();
    private static final ResponseDataParser<List<SendMessageResponse>> MULTI_SEND_RESPONSE_PARSER = new MultiSendResponseParser();
    private static final ResponseDataParser<Boolean> OPEN_CHAT_AGREEMENT_STATUS_PARSER = new OpenChatAgreementStatusParser();
    private static final ResponseDataParser<OpenChatRoomInfo> OPEN_CHAT_ROOM_INFO_PARSER = new OpenChatRoomInfoParser();
    private static final ResponseDataParser<OpenChatRoomStatus> OPEN_CHAT_ROOM_STATUS_PARSER = new OpenChatRoomStatusParser();
    private static final ResponseDataParser<MembershipStatus> OPEN_CHAT_MEMBERSHIP_PARSER = new MembershipStatusParser();
    private static final ResponseDataParser<OpenChatRoomJoinType> OPEN_CHAT_ROOM_JOIN_TYPE_PARSER= new OpenChatRoomJoinTypeParser();

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
            @Nullable String pageToken,
            boolean isForOttShareMessage) {
        final String pathSegment = (isForOttShareMessage) ? PATH_OTS_FRIENDS : PATH_FRIENDS;
        final Uri uri = buildUri(apiBaseUrl, BASE_PATH_GRAPH_API, pathSegment);
        final Map<String, String> queryParams = buildParams("sort", sortField.getServerKey());
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
            @Nullable String pageToken,
            boolean isForOttShareMessage) {
        final String pathSegment = (isForOttShareMessage) ? PATH_OTS_GROUPS : PATH_GROUPS;
        final Uri uri = buildUri(apiBaseUrl, BASE_PATH_GRAPH_API, pathSegment);
        final Map<String, String> queryParams;
        if (!TextUtils.isEmpty(pageToken)) {
            queryParams = buildParams("pageToken", pageToken);
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
            @Nullable String nextPageRequestToken) {
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
            @Nullable String nextPageRequestToken) {
        final Uri uri = buildUri(apiBaseUrl, BASE_PATH_GRAPH_API, "groups", groupId, "approvers");

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
            @NonNull String targetUserId,
            @NonNull List<MessageData> messages) {
        String postData;
        try {
            postData = MessageSendRequest.createSingleUserType(targetUserId, messages).toJsonString();
        } catch (JSONException e) {
            return createInternalErrorResponse(e);
        }

        return httpClient.postWithJson(
                buildUri(apiBaseUrl, BASE_PATH_MESSAGE_API, "send"),
                buildRequestHeaders(accessToken),
                postData,
                new StringParser("status"));
    }

    @NonNull
    public LineApiResponse<List<SendMessageResponse>> sendMessageToMultipleUsers(
            @NonNull InternalAccessToken accessToken,
            @NonNull List<String> targetUserIds,
            @NonNull List<MessageData> messages) {
        return sendMessageToMultipleUsers(accessToken, targetUserIds, messages, false);
    }

    @NonNull
    public LineApiResponse<List<SendMessageResponse>> sendMessageToMultipleUsers(
            @NonNull InternalAccessToken accessToken,
            @NonNull List<String> targetUserIds,
            @NonNull List<MessageData> messages,
            boolean isOttUsed) {
        if (isOttUsed) {
            LineApiResponse<String> ottResponse = getOtt(accessToken, targetUserIds);
            if (ottResponse.isSuccess()) {
                return sendMessageToMultipleUsersUsingOtt(accessToken, ottResponse.getResponseData(), messages);
            } else {
                return LineApiResponse.createAsError(
                        ottResponse.getResponseCode(),
                        ottResponse.getErrorData());
            }
        } else {
            return sendMessageToMultipleUsersUsingUserIds(accessToken, targetUserIds, messages);
        }
    }

    @NonNull
    private LineApiResponse<List<SendMessageResponse>> sendMessageToMultipleUsersUsingUserIds(
            @NonNull InternalAccessToken accessToken,
            @NonNull List<String> targetUserIds,
            @NonNull List<MessageData> messages) {
        String postData;
        try {
            postData = MessageSendRequest.createMultiUsersType(targetUserIds, messages).toJsonString();
        } catch (JSONException e) {
            return createInternalErrorResponse(e);
        }

        return httpClient.postWithJson(
                buildUri(apiBaseUrl, BASE_PATH_MESSAGE_API, "multisend"),
                buildRequestHeaders(accessToken),
                postData,
                MULTI_SEND_RESPONSE_PARSER);
    }

    @VisibleForTesting
    @NonNull
    protected LineApiResponse<List<SendMessageResponse>> sendMessageToMultipleUsersUsingOtt(
            @NonNull InternalAccessToken accessToken,
            @NonNull String ott,
            @NonNull List<MessageData> messages) {
        String postData;
        try {
            postData = MessageSendRequest.createOttType(ott, messages).toJsonString();
        } catch (JSONException e) {
            return createInternalErrorResponse(e);
        }

        return httpClient.postWithJson(
                buildUri(apiBaseUrl, BASE_PATH_MESSAGE_API, PATH_OTT_SHARE),
                buildRequestHeaders(accessToken),
                postData,
                MULTI_SEND_RESPONSE_PARSER);
    }

    @NonNull
    public LineApiResponse<Boolean> getOpenChatAgreementStatus(
            @NonNull InternalAccessToken accessToken) {
        final Uri uri = buildUri(apiBaseUrl, BASE_PATH_OPENCHAT_API, "terms/agreement");

        return httpClient.get(
                uri,
                buildRequestHeaders(accessToken),
                Collections.emptyMap(),
                OPEN_CHAT_AGREEMENT_STATUS_PARSER);
    }

    @NonNull
    public LineApiResponse<OpenChatRoomInfo> createOpenChatRoom(
            @NonNull InternalAccessToken accessToken,
            @NonNull OpenChatParameters openChatParameters) {
        final Uri uri = buildUri(apiBaseUrl, BASE_PATH_OPENCHAT_API, "openchats");

        return httpClient.postWithJson(
                uri,
                buildRequestHeaders(accessToken),
                openChatParameters.toJsonString(),
                OPEN_CHAT_ROOM_INFO_PARSER);
    }

    @NonNull
    public LineApiResponse<Boolean> joinOpenChatRoom(
            @NonNull InternalAccessToken accessToken,
            @NonNull String roomId,
            @NonNull String displayName) {
        final Uri uri = buildUri(apiBaseUrl, BASE_PATH_OPENCHAT_API, "openchats", roomId, "join");

        return httpClient.postWithJson(
                uri,
                buildRequestHeaders(accessToken),
                "{\"displayName\": \"" + displayName + "\" }",
                null);
    }

    @NonNull
    public LineApiResponse<OpenChatRoomStatus> getOpenChatRoomStatus(
            @NonNull InternalAccessToken accessToken,
            @NonNull String roomId) {
        final Uri uri = buildUri(apiBaseUrl, BASE_PATH_OPENCHAT_API, "openchats", roomId, "status");
        final Map<String, String> queryParameters = new HashMap<>();
        queryParameters.put("openChatId", roomId);

        return httpClient.get(
                uri,
                buildRequestHeaders(accessToken),
                queryParameters,
                OPEN_CHAT_ROOM_STATUS_PARSER);
    }

    @NonNull
    public LineApiResponse<MembershipStatus> getOpenChatMembershipStatus(
            @NonNull InternalAccessToken accessToken,
            @NonNull String roomId) {
        final Uri uri = buildUri(apiBaseUrl, BASE_PATH_OPENCHAT_API, "openchats", roomId, "members/me/membership");

        return httpClient.get(
                uri,
                buildRequestHeaders(accessToken),
                Collections.emptyMap(),
                OPEN_CHAT_MEMBERSHIP_PARSER);
    }

    @NonNull
    public LineApiResponse<OpenChatRoomJoinType> getOpenChatRoomJoinType(
            @NonNull InternalAccessToken accessToken,
            @NonNull String roomId) {
        final Uri uri = buildUri(apiBaseUrl, BASE_PATH_OPENCHAT_API, "openchats", roomId, "type");

        return httpClient.get(
                uri,
                buildRequestHeaders(accessToken),
                Collections.emptyMap(),
                OPEN_CHAT_ROOM_JOIN_TYPE_PARSER);
    }

    private <T> LineApiResponse<T> createInternalErrorResponse(Exception exception) {
        return LineApiResponse.createAsError(LineApiResponseCode.INTERNAL_ERROR, new LineApiError(exception));
    }

    @VisibleForTesting
    private static class OpenChatAgreementStatusParser extends JsonToObjectBaseResponseParser<Boolean> {
        @NonNull
        @Override
        protected Boolean parseJsonToObject(@NonNull JSONObject jsonObject) throws JSONException {
            return jsonObject.getBoolean("agreed");
        }
    }


    @VisibleForTesting
    private static class OpenChatRoomInfoParser extends JsonToObjectBaseResponseParser<OpenChatRoomInfo> {
        @NonNull
        @Override
        protected OpenChatRoomInfo parseJsonToObject(@NonNull JSONObject jsonObject) throws JSONException {
            return new OpenChatRoomInfo(jsonObject.getString("openchatId"),
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

    @VisibleForTesting
    private static class OpenChatRoomJoinTypeParser extends JsonToObjectBaseResponseParser<OpenChatRoomJoinType> {
        @NonNull
        @Override
        protected OpenChatRoomJoinType parseJsonToObject(@NonNull JSONObject jsonObject) throws JSONException {
            String state = jsonObject.getString("type").toUpperCase();
            return OpenChatRoomJoinType.valueOf(state);
        }
    }

    @NonNull
    private LineApiResponse<String> getOtt(
            @NonNull InternalAccessToken accessToken,
            @NonNull List<String> targetUserIds) {
        String postData;
        try {
            postData = new OttRequest(targetUserIds).toJsonString();
        } catch (JSONException e) {
            return LineApiResponse.createAsError(
                    LineApiResponseCode.INTERNAL_ERROR,
                    new LineApiError(e));
        }

        return httpClient.postWithJson(
                buildUri(apiBaseUrl, BASE_PATH_MESSAGE_API, PATH_OTT_ISSUE),
                buildRequestHeaders(accessToken),
                postData,
                new StringParser("token"));
    }

    @NonNull
    private static Map<String, String> buildRequestHeaders(@NonNull InternalAccessToken accessToken) {
        return buildParams(
                REQUEST_HEADER_ACCESS_TOKEN,
                TOKEN_TYPE_BEARER + ' ' + accessToken.getAccessToken());
    }

    @VisibleForTesting
    static class FriendsParser extends JsonToObjectBaseResponseParser<GetFriendsResponse> {
        @NonNull
        @Override
        protected GetFriendsResponse parseJsonToObject(@NonNull JSONObject jsonObject) throws JSONException {
            List<LineFriendProfile> friendList = new ArrayList<>();
            JSONArray friendsArray = jsonObject.getJSONArray("friends");
            for (int i = 0; i < friendsArray.length(); i++) {
                friendList.add(FriendProfileParser.parseLineFriendProfile(friendsArray.getJSONObject(i)));
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
        protected LineProfile parseJsonToObject(@NonNull JSONObject jsonObject) throws JSONException {
            return parseLineProfile(jsonObject);
        }
    }

    @VisibleForTesting
    static class FriendProfileParser extends JsonToObjectBaseResponseParser<LineFriendProfile> {
        private static LineFriendProfile parseLineFriendProfile(@NonNull JSONObject jsonObject) throws JSONException {
            LineProfile lineProfile = ProfileParser.parseLineProfile(jsonObject);
            String overriddenDisplayName = jsonObject.optString("displayNameOverridden", null);
            return new LineFriendProfile(lineProfile.getUserId(),
                    lineProfile.getDisplayName(),
                    lineProfile.getPictureUrl(),
                    lineProfile.getStatusMessage(),
                    overriddenDisplayName);
        }

        @NonNull
        @Override
        protected LineFriendProfile parseJsonToObject(@NonNull JSONObject jsonObject) throws JSONException {
            return parseLineFriendProfile(jsonObject);
        }
    }

    @VisibleForTesting
    static class FriendshipStatusParser extends JsonToObjectBaseResponseParser<LineFriendshipStatus> {
        @NonNull
        @Override
        protected LineFriendshipStatus parseJsonToObject(@NonNull JSONObject jsonObject) throws JSONException {
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
        protected GetGroupsResponse parseJsonToObject(@NonNull JSONObject jsonObject) throws JSONException {
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

        private String jsonKey;

        StringParser(String jsonKey) {
            this.jsonKey = jsonKey;
        }

        @NonNull
        @Override
        protected String parseJsonToObject(@NonNull JSONObject jsonObject) throws JSONException {
            return jsonObject.getString(jsonKey);
        }
    }

    @VisibleForTesting
    static class MultiSendResponseParser extends JsonToObjectBaseResponseParser<List<SendMessageResponse>> {
        @NonNull
        @Override
        protected List<SendMessageResponse> parseJsonToObject(@NonNull JSONObject jsonObject) throws JSONException {
            List<SendMessageResponse> sendMessageResponses = new ArrayList<>();
            String jsonKeyResults = "results";
            if (jsonObject.has(jsonKeyResults)) {
                JSONArray resultArray = jsonObject.getJSONArray(jsonKeyResults);
                for (int i = 0; i < resultArray.length(); i++) {
                    sendMessageResponses.add(SendMessageResponse.fromJsonObject(resultArray.getJSONObject(i)));
                }
            }
            return sendMessageResponses;
        }
    }
}
