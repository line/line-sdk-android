package com.linecorp.linesdk.api;

import com.linecorp.linesdk.FriendSortField;
import com.linecorp.linesdk.GetFriendsResponse;
import com.linecorp.linesdk.GetGroupsResponse;
import com.linecorp.linesdk.LineAccessToken;
import com.linecorp.linesdk.LineApiResponse;
import com.linecorp.linesdk.LineCredential;
import com.linecorp.linesdk.LineFriendshipStatus;
import com.linecorp.linesdk.LineProfile;
import com.linecorp.linesdk.SendMessageResponse;
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
 * An API client that provides you with access to the Social API to perform operations such as
 * getting the current access token, getting the user profile, logging out the user, refreshing
 * the access token, and verifying the access token.
 */
// This interface also allows LINE internal users to perform operations such as getting the list of
// the user's friends, getting the list of the groups that the user is a member of, and sending
// messages to users.
public interface LineApiClient {
    /**
     * Revokes the access token.
     *
     * @return A {@link LineApiResponse} object containing information about the response.
     */
    @NonNull
    LineApiResponse<?> logout();

    /**
     * Refreshes the access token that the SDK is using for the user.
     *
     * @return A {@link LineApiResponse} object. If the API call is successful, the
     * {@link LineApiResponse} object contains a {@link LineAccessToken} object that contains
     * a new access token. If the API call fails, the payload of the {@link LineApiResponse}
     * object is <code>null</code>.
     */
    @NonNull
    LineApiResponse<LineAccessToken> refreshAccessToken();

    /**
     * Checks whether the access token that the SDK is using for the user is valid.
     *
     * @return A {@link LineApiResponse} object. If the access token is valid, the
     * {@link LineApiResponse} object contains a success response and the {@link LineCredential}
     * object that contains the access token. If the access token is invalid, the
     * {@link LineApiResponse} object contains a failure response.
     */
    @NonNull
    LineApiResponse<LineCredential> verifyToken();

    /**
     * Gets the access token that the SDK is using for the user.
     *
     * @return A {@link LineApiResponse} object. If the API call is successful,
     * a {@link LineApiResponse} object contains the {@link LineAccessToken} object that contains
     * the access token. If the API call fails, the payload of the {@link LineApiResponse} object is
     * <code>null</code>.
     */
    @NonNull
    LineApiResponse<LineAccessToken> getCurrentAccessToken();

    /**
     * Gets the user profile information.
     *
     * @return A {@link LineApiResponse} object. If the API call is successful, the
     * {@link LineApiResponse} object contains a {@link LineProfile} object that contains the
     * user's profile. If the API call fails, the payload of the {@link LineApiResponse} object is
     * <code>null</code>.
     */
    @NonNull
    LineApiResponse<LineProfile> getProfile();

    /**
     * Gets the friendship status between the bot (which is linked to the current channel) and the user.
     *
     * @return A {@link LineApiResponse} object. If the API call is successful, the
     * {@link LineApiResponse} object contains a {@link LineFriendshipStatus} object
     * that contains the friendship status information. If the API call fails, the payload of
     * the {@link LineApiResponse} object is <code>null</code>.
     */
    @NonNull
    LineApiResponse<LineFriendshipStatus> getFriendshipStatus();

    // Graph API methods
    /**
     * @hide
     * LINE internal use only. Gets the user's friends who have authorized the channel to use their
     * profile information or who have authorized LINE to use their profile information with the
     * privacy filter setting.
     * <p>
     * To call this method, you need a channel with the <code>SOCIAL_GRAPH</code> permission and an
     * access token with the <code>friends</code> scope.
     *
     * @param sortField            Optional. The way to sort the friend list. See the
     *                             {@link FriendSortField} section for more information.
     * @param nextPageRequestToken Optional. The continuation token to get the next friend list.
     * @return A {@link LineApiResponse} object. If the API call is successful, the
     * {@link LineApiResponse} object contains a {@link GetFriendsResponse} object that contains
     * up to 200 friends of the user. The {@link GetFriendsResponse} object also contains a
     * continuation token if there are remaining friends that the original API call didn't return.
     * If the API call fails, the payload of the {@link LineApiResponse} object is
     */
    @NonNull
    LineApiResponse<GetFriendsResponse> getFriends(
            @NonNull FriendSortField sortField,
            @Nullable String nextPageRequestToken
    );

    /**
     * @hide
     * LINE internal use only. Gets the user's friends who have authorized the channel to use their
     * profile information or who have authorized LINE to use their profile information with the
     * privacy filter setting.
     * <p>
     * To call this method, you need a channel with the <code>SOCIAL_GRAPH</code> permission and an
     * access token with the <code>friends</code> scope.
     *
     * @param sortField            Optional. The way to sort the friend list. See the
     *                             {@link FriendSortField} section for more information.
     * @param nextPageRequestToken Optional. The continuation token to get the next friend list.
     * @param isForOttShareMessage True if this API is called for sharing messages with OTT,
     *                             false otherwise.
     * @return A {@link LineApiResponse} object. If the API call is successful, the
     * {@link LineApiResponse} object contains a {@link GetFriendsResponse} object that contains
     * up to 200 friends of the user. The {@link GetFriendsResponse} object also contains a
     * continuation token if there are remaining friends that the original API call didn't return.
     * If the API call fails, the payload of the {@link LineApiResponse} object is
     * <code>null</code>.
     * @see FriendSortField
     */
    @NonNull
    LineApiResponse<GetFriendsResponse> getFriends(
            @NonNull FriendSortField sortField,
            @Nullable String nextPageRequestToken,
            boolean isForOttShareMessage
    );

    /**
     * @hide
     * LINE internal use only. Gets the user's friends who have authorized the channel to use their
     * profile information.
     * <p>
     * To call this method, you need a channel with the <code>SOCIAL_GRAPH</code> permission and an
     * access token with the <code>friends</code> scope.
     *
     * @param sortField            Optional. The way to sort the friend list. See the
     *                             {@link FriendSortField} section for more information.
     * @param nextPageRequestToken Optional. The continuation token to get the next friend list.
     * @return A {@link LineApiResponse} object. If the API call is successful, the
     * {@link LineApiResponse} object contains a {@link GetFriendsResponse} object that contains
     * up to 200 friends of the user. The {@link GetFriendsResponse} object also contains a
     * continuation token if there are remaining friends that the original API call didn't return.
     * If the API call fails, the payload of the {@link LineApiResponse} object is
     * <code>null</code>.
     * @see FriendSortField
     */
    @NonNull
    LineApiResponse<GetFriendsResponse> getFriendsApprovers(
            @NonNull FriendSortField sortField,
            @Nullable String nextPageRequestToken
    );

    /**
     * @hide
     * LINE internal use only. Gets groups that the user is a member of.
     * <p>
     * To call this method, you need a channel with the <code>SOCIAL_GRAPH</code> permission and an
     * access token with the <code>groups</code> scope.
     *
     * @param nextPageRequestToken Optional. The continuation token to get the next group list.
     * @return A {@link LineApiResponse} object. If the API call is successful, the
     * {@link LineApiResponse} object contains a {@link GetGroupsResponse} object that contains
     * up to 200 groups that the user is a member of. The {@link GetGroupsResponse} object also
     * contains a continuation token if there are remaining groups that the original API call
     * didn't return. If the API call fails, the payload of the {@link LineApiResponse} object is
     * <code>null</code>.
     */
    @NonNull
    LineApiResponse<GetGroupsResponse> getGroups(@Nullable String nextPageRequestToken);

    /**
     * @hide
     * LINE internal use only. Gets groups that the user is a member of.
     * <p>
     * To call this method, you need a channel with the <code>SOCIAL_GRAPH</code> permission and an
     * access token with the <code>groups</code> scope.
     *
     * @param nextPageRequestToken Optional. The continuation token to get the next group list.
     * @param isForOttShareMessage True if this API is called for sharing messages with OTT,
     *                             false otherwise.
     * @return A {@link LineApiResponse} object. If the API call is successful, the
     * {@link LineApiResponse} object contains a {@link GetGroupsResponse} object that contains
     * up to 200 groups that the user is a member of. The {@link GetGroupsResponse} object also
     * contains a continuation token if there are remaining groups that the original API call
     * didn't return. If the API call fails, the payload of the {@link LineApiResponse} object is
     * <code>null</code>.
     */
    @NonNull
    LineApiResponse<GetGroupsResponse> getGroups(
            @Nullable String nextPageRequestToken,
            boolean isForOttShareMessage
    );

    /**
     * @hide
     * LINE internal use only. Gets members of the specified group who have already authorized the
     * channel to use their profile information.
     * <p>
     * To call this method, you need a channel with the <code>SOCIAL_GRAPH</code> permission and an
     * access token with the <code>groups</code> scope.
     *
     * @param groupId              Required. The ID of the group to get its members.
     * @param nextPageRequestToken Optional. The continuation token to get the next member list.
     * @return A {@link LineApiResponse} object. If the API call is successful, the
     * {@link LineApiResponse} object contains a {@link GetFriendsResponse} object that contains
     * up to 200 members. The {@link GetFriendsResponse} object also contains a continuation token
     * if there are remaining members that the original API call didn't return. If the API call
     * fails, the payload of the {@link LineApiResponse} object is <code>null</code>.
     */
    @NonNull
    LineApiResponse<GetFriendsResponse> getGroupApprovers(
            @NonNull String groupId,
            @Nullable String nextPageRequestToken
    );

    // User Message API methods

    /**
     * @hide
     * LINE internal use only. Sends messages to a user or group on behalf of the current user.
     * <p>
     * In the following cases, messages are not delivered even though the API call is successful.
     * The response status is <code>discarded</code> for such API calls.
     * <ul>
     * <li>The recipient has blocked the current user.</li>
     * <li>The recipient has turned off messages from the channel.</li>
     * <li>The recipient hasn't authorized the channel to use their profile information and has
     * turned off messages from unauthorized channels.</li>
     * <li>The current user is not a friend of the recipient, who is a human and not a bot.</li>
     * </ul>
     * <p>
     * To call this method, you need a channel with the <code>MESSAGE</code> permission and an
     * access token with the <code>message.write</code> scope.
     *
     * @param targetUserId Required. The ID of the user or group that receives messages from the
     *                     current user.
     * @param messages     Required. The messages to send. Available message types are: <code>text</code>, <code>audio</code>,
     *                     <code>image</code>, <code>location</code>, <code>video</code>, and <code>template</code>. You can send up to five messages.
     * @return A {@link LineApiResponse} object. If the API call is successful, the
     * {@link LineApiResponse} object contains the delivery result. If the API call fails, the payload
     * of the {@link LineApiResponse} object is <code>null</code>. The delivery result is either of
     * the followings:
     * <ul>
     * <li><code>ok</code>: The messages have been delivered successfully.</li>
     * <li><code>discarded</code>: The messages have been discarded because one of the conditions
     * above is met. This code is returned only when you attempt to send messages to a user.
     * </li>
     * </ul>
     * @see MessageData
     */
    @NonNull
    LineApiResponse<String> sendMessage(
            @NonNull String targetUserId,
            @NonNull List<MessageData> messages
    );

    /**
     * @hide
     * LINE internal use only. Sends messages to multiple users using user IDs on behalf of the current
     * user. To know the message delivery result for each recipient, check the response data.
     * <p>
     * In the following cases, messages are not delivered even though the API call is successful.
     * The response status is <code>discarded</code> for such API calls.
     * <ul>
     * <li>The recipient has blocked the current user.</li>
     * <li>The recipient has turned off messages from the channel.</li>
     * <li>The recipient hasn't authorized the channel to use their profile information and has
     * turned off messages from unauthorized channels.</li>
     * <li>The current user is not a friend of the recipient, who is a human and not a bot.</li>
     * </ul>
     * <p>
     * To call this method, you need a channel with the <code>MESSAGE</code> permission and an
     * access token with the <code>message.write</code> scope.
     *
     * @param targetUserIds The IDs of the users that receive messages from the user. You can
     *                      specify up to 10 users.
     * @param messages      The messages to send. Available message types are: <code>text</code>, <code>audio</code>, <code>image</code>,
     *                      <code>location</code>, <code>video</code>, and <code>template</code>. You can send up to five messages.
     * @return A {@link LineApiResponse} object. If the API call is successful, the
     * {@link LineApiResponse} object contains the {@link SendMessageResponse} objects that contain
     * the delivery results. If the API call fails, the payload of the {@link LineApiResponse}
     * object is <code>null</code>. The delivery result is either of the followings:
     * <ul>
     * <li><code>ok</code>: The messages have been delivered successfully.</li>
     * <li><code>discarded</code>: The messages have been discarded because one of the conditions
     * above is met or a server error occurred.
     * </li>
     * </ul>
     * @see SendMessageResponse
     */
    @NonNull
    LineApiResponse<List<SendMessageResponse>> sendMessageToMultipleUsers(
            @NonNull List<String> targetUserIds,
            @NonNull List<MessageData> messages
    );

    /**
     * @hide
     * Sends messages to multiple users on behalf of the current user.
     * To know the message delivery result for each recipient, check the response data.
     * <p>
     * In the following cases, messages are not delivered even though the API call is successful.
     * The response status is <code>discarded</code> for such API calls.
     * <ul>
     * <li>The recipient has blocked the current user.</li>
     * <li>The recipient has turned off messages from the channel.</li>
     * <li>The recipient hasn't authorized the channel to use their profile information and has
     * turned off messages from unauthorized channels.</li>
     * <li>The current user is not a friend of the recipient, who is a human and not a bot.</li>
     * </ul>
     * <p>
     * To call this method, you need a channel with the <code>MESSAGE</code> permission and an
     * access token with the <code>message.write</code> scope.
     *
     * @param targetUserIds The IDs of the users that receive messages from the user. You can
     *                      specify up to 10 users.
     * @param messages      The messages to send. Available message types are: <code>text</code>, <code>audio</code>, <code>image</code>,
     *                      <code>location</code>, <code>video</code>, and <code>template</code>. You can send up to five messages.
     * @param isOttUsed     True if you want to send messages using OTT instead of using the user IDs;
     *                      false otherwise.
     * @return A {@link LineApiResponse} object. If the API call is successful, the
     * {@link LineApiResponse} object contains the {@link SendMessageResponse} objects that contain
     * the delivery results. If the API call fails, the payload of the {@link LineApiResponse}
     * object is <code>null</code>. The delivery result is either of the following:
     * <ul>
     * <li><code>ok</code>: The messages have been delivered successfully.</li>
     * <li><code>discarded</code>: The messages have been discarded because one of the conditions
     * above is met or a server error occurred.
     * </li>
     * </ul>
     * @see SendMessageResponse
     */
    @NonNull
    LineApiResponse<List<SendMessageResponse>> sendMessageToMultipleUsers(
            @NonNull List<String> targetUserIds,
            @NonNull List<MessageData> messages,
            boolean isOttUsed
    );

    /**
     * @hide
     */
    @NonNull
    LineApiResponse<Boolean> getOpenChatAgreementStatus();

    /**
     * @hide
     */
    @NonNull
    LineApiResponse<OpenChatRoomInfo> createOpenChatRoom(@NonNull OpenChatParameters openChatParameters);

    /**
     * @hide
     */
    @NonNull
    LineApiResponse<Boolean> joinOpenChatRoom(@NonNull String roomId, @NonNull String displayName);

    /**
     * @hide
     */
    @NonNull
    LineApiResponse<OpenChatRoomStatus> getOpenChatRoomStatus(@NonNull String roomId);

    /**
     * @hide
     */
    @NonNull
    LineApiResponse<OpenChatRoomJoinType> getOpenChatRoomJoinType(@NonNull String roomId);

    /**
     * @hide
     */
    @NonNull
    LineApiResponse<MembershipStatus> getOpenChatMembershipStatus(@NonNull String roomId);
}
