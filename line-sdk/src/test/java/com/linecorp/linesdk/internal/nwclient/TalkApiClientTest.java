package com.linecorp.linesdk.internal.nwclient;

import android.net.Uri;
import androidx.annotation.NonNull;

import com.linecorp.linesdk.BuildConfig;
import com.linecorp.linesdk.FriendSortField;
import com.linecorp.linesdk.GetFriendsResponse;
import com.linecorp.linesdk.GetGroupsResponse;
import com.linecorp.linesdk.LineApiError;
import com.linecorp.linesdk.LineApiResponse;
import com.linecorp.linesdk.LineApiResponseCode;
import com.linecorp.linesdk.LineFriendProfile;
import com.linecorp.linesdk.LineFriendshipStatus;
import com.linecorp.linesdk.LineProfile;
import com.linecorp.linesdk.SendMessageResponse;
import com.linecorp.linesdk.TestConfig;
import com.linecorp.linesdk.TestJsonDataBuilder;
import com.linecorp.linesdk.TestStringInputStream;
import com.linecorp.linesdk.internal.InternalAccessToken;
import com.linecorp.linesdk.internal.nwclient.core.ChannelServiceHttpClient;
import com.linecorp.linesdk.internal.nwclient.core.ResponseDataParser;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.linecorp.linesdk.internal.nwclient.TalkApiClient.BASE_PATH_GRAPH_API;
import static com.linecorp.linesdk.internal.nwclient.TalkApiClient.BASE_PATH_MESSAGE_API;
import static com.linecorp.linesdk.internal.nwclient.TalkApiClient.PATH_OTS_FRIENDS;
import static com.linecorp.linesdk.internal.nwclient.TalkApiClient.PATH_OTS_GROUPS;
import static com.linecorp.linesdk.internal.nwclient.TalkApiClient.PATH_OTT_ISSUE;
import static com.linecorp.linesdk.internal.nwclient.TalkApiClient.PATH_OTT_SHARE;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyMapOf;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Test for {@link TalkApiClient}.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = TestConfig.TARGET_SDK_VERSION)
public class TalkApiClientTest {
    private static final String CHARSET_NAME = "UTF-8";
    private static final String API_BASE_URL = "https://test";
    private static final InternalAccessToken ACCESS_TOKEN =
            new InternalAccessToken("accessToken", 10L, 100L, "refreshToken");
    private static final LineApiResponse<?> EXPECTED_RESULT = LineApiResponse.createAsSuccess(null);

    @Mock
    private ChannelServiceHttpClient httpClient;
    @Captor
    private ArgumentCaptor<ResponseDataParser<?>> responseParserCaptor;

    private TalkApiClient target;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        target = Mockito.spy(new TalkApiClient(Uri.parse(API_BASE_URL), httpClient));
    }

    @Test
    public void testGetProfile() {
        doReturn(EXPECTED_RESULT).when(httpClient).get(
                any(Uri.class),
                anyMapOf(String.class, String.class),
                anyMapOf(String.class, String.class),
                any(ResponseDataParser.class));

        LineApiResponse<LineProfile> actualResult = target.getProfile(ACCESS_TOKEN);

        assertSame(EXPECTED_RESULT, actualResult);

        verify(httpClient, times(1)).get(
                eq(Uri.parse(API_BASE_URL + "/v2/profile")),
                eq(Collections.singletonMap("Authorization", "Bearer " + ACCESS_TOKEN.getAccessToken())),
                eq(Collections.emptyMap()),
                responseParserCaptor.capture());

        assertTrue(responseParserCaptor.getValue() instanceof TalkApiClient.ProfileParser);
    }

    @Test
    public void testProfileParser() throws IOException {
        TalkApiClient.ProfileParser target = new TalkApiClient.ProfileParser();
        verifyResponseDataParser(
                target,
                new LineProfile(
                        "testMid",
                        "testDisplayName",
                        Uri.parse("testPictureUrl"),
                        "testStatusMessage"),
                new TestJsonDataBuilder()
                        .put("userId", "testMid")
                        .put("displayName", "testDisplayName")
                        .put("pictureUrl", "testPictureUrl")
                        .put("statusMessage", "testStatusMessage")
                        .buildAsString()
        );
        verifyResponseDataParser(
                target,
                new LineProfile(
                        "testMid",
                        "testDisplayName",
                        null /* pictureUrl */,
                        null /* statucMessage */),
                new TestJsonDataBuilder()
                        .put("userId", "testMid")
                        .put("displayName", "testDisplayName")
                        .buildAsString()
        );
        verifyToThrowException(
                target,
                new TestJsonDataBuilder()
                        .put("displayName", "testDisplayName")
                        .buildAsString()
        );
        verifyToThrowException(
                target,
                new TestJsonDataBuilder()
                        .put("userId", "testMid")
                        .buildAsString()
        );
    }

    @Test
    public void testFriendProfileParser() throws IOException {
        TalkApiClient.FriendProfileParser target = new TalkApiClient.FriendProfileParser();
        verifyResponseDataParser(
                target,
                new LineFriendProfile(
                        "testMid",
                        "testDisplayName",
                        Uri.parse("testPictureUrl"),
                        "testStatusMessage",
                        "testOverriddenDisplayName"),
                new TestJsonDataBuilder()
                        .put("userId", "testMid")
                        .put("displayName", "testDisplayName")
                        .put("pictureUrl", "testPictureUrl")
                        .put("statusMessage", "testStatusMessage")
                        .put("displayNameOverridden", "testOverriddenDisplayName")
                        .buildAsString()
        );
        verifyResponseDataParser(
                target,
                new LineFriendProfile(
                        "testMid",
                        "testDisplayName",
                        null /* pictureUrl */,
                        null /* statucMessage */,
                        null),
                new TestJsonDataBuilder()
                        .put("userId", "testMid")
                        .put("displayName", "testDisplayName")
                        .buildAsString()
        );
        verifyToThrowException(
                target,
                new TestJsonDataBuilder()
                        .put("displayName", "testDisplayName")
                        .buildAsString()
        );
        verifyToThrowException(
                target,
                new TestJsonDataBuilder()
                        .put("userId", "testMid")
                        .buildAsString()
        );
    }

    @Test
    public void testGetFriendshipStatus() {
        doReturn(EXPECTED_RESULT).when(httpClient).get(
                any(Uri.class),
                anyMapOf(String.class, String.class),
                anyMapOf(String.class, String.class),
                any(ResponseDataParser.class));

        LineApiResponse<LineFriendshipStatus> actualResult = target.getFriendshipStatus(ACCESS_TOKEN);

        assertSame(EXPECTED_RESULT, actualResult);

        verify(httpClient, times(1)).get(
                eq(Uri.parse(API_BASE_URL + "/friendship/v1/status")),
                eq(Collections.singletonMap("Authorization", "Bearer " + ACCESS_TOKEN.getAccessToken())),
                eq(Collections.emptyMap()),
                responseParserCaptor.capture());

        assertTrue(responseParserCaptor.getValue() instanceof TalkApiClient.FriendshipStatusParser);
    }

    @Test
    public void testFriendshipStatusParser() throws IOException {
        TalkApiClient.FriendshipStatusParser target = new TalkApiClient.FriendshipStatusParser();
        verifyResponseDataParser(
                target,
                new LineFriendshipStatus(true),
                new TestJsonDataBuilder()
                        .put("friendFlag", "true")
                        .buildAsString()
        );
        verifyResponseDataParser(
                target,
                new LineFriendshipStatus(false),
                new TestJsonDataBuilder()
                        .put("friendFlag", "false")
                        .buildAsString()
        );
        verifyToThrowException(
                target,
                new TestJsonDataBuilder()
                        .put("friendFlag", "abcd")
                        .buildAsString()
        );
        verifyToThrowException(
                target,
                new TestJsonDataBuilder()
                        .buildAsString()
        );
    }

    @Test
    public void testGetFriends_shareMessageWithOtt() {
        doReturn(EXPECTED_RESULT).when(httpClient).get(
                any(Uri.class),
                anyMapOf(String.class, String.class),
                anyMapOf(String.class, String.class),
                any(ResponseDataParser.class));

        LineApiResponse<GetFriendsResponse> actualResult =
                target.getFriends(ACCESS_TOKEN, FriendSortField.NAME, "pageToken01", true);

        assertSame(EXPECTED_RESULT, actualResult);

        Map<String, String> expectedQueryParams = new HashMap<>();
        expectedQueryParams.put("sort", "name");
        expectedQueryParams.put("pageToken", "pageToken01");

        verify(httpClient, times(1)).get(
                eq(Uri.parse(API_BASE_URL + "/" + BASE_PATH_GRAPH_API + "/" + PATH_OTS_FRIENDS)),
                eq(Collections.singletonMap("Authorization", "Bearer " + ACCESS_TOKEN.getAccessToken())),
                eq(expectedQueryParams),
                responseParserCaptor.capture());

        assertTrue(responseParserCaptor.getValue() instanceof TalkApiClient.FriendsParser);
    }

    @Test
    public void testGetFriends_notShareMessageWithOtt() {
        doReturn(EXPECTED_RESULT).when(httpClient).get(
                any(Uri.class),
                anyMapOf(String.class, String.class),
                anyMapOf(String.class, String.class),
                any(ResponseDataParser.class));

        LineApiResponse<GetFriendsResponse> actualResult =
                target.getFriends(ACCESS_TOKEN, FriendSortField.NAME, "pageToken01", false);

        assertSame(EXPECTED_RESULT, actualResult);

        Map<String, String> expectedQueryParams = new HashMap<>();
        expectedQueryParams.put("sort", "name");
        expectedQueryParams.put("pageToken", "pageToken01");

        verify(httpClient, times(1)).get(
                eq(Uri.parse(API_BASE_URL + "/graph/v2/friends")),
                eq(Collections.singletonMap("Authorization", "Bearer " + ACCESS_TOKEN.getAccessToken())),
                eq(expectedQueryParams),
                responseParserCaptor.capture());

        assertTrue(responseParserCaptor.getValue() instanceof TalkApiClient.FriendsParser);
    }

    @Test
    public void testGetFriendsApprovers() {
        doReturn(EXPECTED_RESULT).when(httpClient).get(
                any(Uri.class),
                anyMapOf(String.class, String.class),
                anyMapOf(String.class, String.class),
                any(ResponseDataParser.class));

        LineApiResponse<GetFriendsResponse> actualResult =
                target.getFriendsApprovers(ACCESS_TOKEN, FriendSortField.NAME, "pageToken01");

        assertSame(EXPECTED_RESULT, actualResult);

        Map<String, String> expectedQueryParams = new HashMap<>();
        expectedQueryParams.put("sort", "name");
        expectedQueryParams.put("pageToken", "pageToken01");

        verify(httpClient, times(1)).get(
                eq(Uri.parse(API_BASE_URL + "/graph/v2/friends/approvers")),
                eq(Collections.singletonMap("Authorization", "Bearer " + ACCESS_TOKEN.getAccessToken())),
                eq(expectedQueryParams),
                responseParserCaptor.capture());

        assertTrue(responseParserCaptor.getValue() instanceof TalkApiClient.FriendsParser);
    }

    @Test
    public void testGetGroups_shareMessageWithOtt() {
        doReturn(EXPECTED_RESULT).when(httpClient).get(
                any(Uri.class),
                anyMapOf(String.class, String.class),
                anyMapOf(String.class, String.class),
                any(ResponseDataParser.class));

        LineApiResponse<GetGroupsResponse> actualResult =
                target.getGroups(ACCESS_TOKEN, "pageToken01", true);

        assertSame(EXPECTED_RESULT, actualResult);

        Map<String, String> expectedQueryParams = new HashMap<>();
        expectedQueryParams.put("pageToken", "pageToken01");

        verify(httpClient, times(1)).get(
                eq(Uri.parse(API_BASE_URL + "/" + BASE_PATH_GRAPH_API + "/" + PATH_OTS_GROUPS)),
                eq(Collections.singletonMap("Authorization", "Bearer " + ACCESS_TOKEN.getAccessToken())),
                eq(expectedQueryParams),
                responseParserCaptor.capture());

        assertTrue(responseParserCaptor.getValue() instanceof TalkApiClient.GroupParser);
    }

    @Test
    public void testGetGroups_notShareMessageWithOtt() {
        doReturn(EXPECTED_RESULT).when(httpClient).get(
                any(Uri.class),
                anyMapOf(String.class, String.class),
                anyMapOf(String.class, String.class),
                any(ResponseDataParser.class));

        LineApiResponse<GetGroupsResponse> actualResult =
                target.getGroups(ACCESS_TOKEN, "pageToken01", false);

        assertSame(EXPECTED_RESULT, actualResult);

        Map<String, String> expectedQueryParams = new HashMap<>();
        expectedQueryParams.put("pageToken", "pageToken01");

        verify(httpClient, times(1)).get(
                eq(Uri.parse(API_BASE_URL + "/graph/v2/groups")),
                eq(Collections.singletonMap("Authorization", "Bearer " + ACCESS_TOKEN.getAccessToken())),
                eq(expectedQueryParams),
                responseParserCaptor.capture());

        assertTrue(responseParserCaptor.getValue() instanceof TalkApiClient.GroupParser);
    }

    @Test
    public void testGetGroupApprovers() {
        doReturn(EXPECTED_RESULT).when(httpClient).get(
                any(Uri.class),
                anyMapOf(String.class, String.class),
                anyMapOf(String.class, String.class),
                any(ResponseDataParser.class));

        LineApiResponse<GetFriendsResponse> actualResult =
                target.getGroupApprovers(ACCESS_TOKEN, "groupId01", "pageToken01");

        assertSame(EXPECTED_RESULT, actualResult);

        Map<String, String> expectedQueryParams = new HashMap<>();
        expectedQueryParams.put("pageToken", "pageToken01");

        verify(httpClient, times(1)).get(
                eq(Uri.parse(API_BASE_URL + "/graph/v2/groups/groupId01/approvers")),
                eq(Collections.singletonMap("Authorization", "Bearer " + ACCESS_TOKEN.getAccessToken())),
                eq(expectedQueryParams),
                responseParserCaptor.capture());

        assertTrue(responseParserCaptor.getValue() instanceof TalkApiClient.FriendsParser);
    }

    @Test
    public void testSendMessageToMultipleUsers_withoutIsOttUsedParameter() {
        target.sendMessageToMultipleUsers(ACCESS_TOKEN, Collections.emptyList(), Collections.emptyList());
        verify(target, times(1)).sendMessageToMultipleUsers(
                eq(ACCESS_TOKEN),
                eq(Collections.emptyList()),
                eq(Collections.emptyList()),
                eq(false));
    }

    @Test
    public void testSendMessageToMultipleUsersUsingTargetUserIds() {
        final LineApiResponse<?> mockResponse = LineApiResponse.createAsSuccess("");
        givenPostWithJsonApiResponse(mockResponse);

        LineApiResponse<List<SendMessageResponse>> actualResult =
                target.sendMessageToMultipleUsers(
                        ACCESS_TOKEN,
                        Collections.emptyList(),
                        Collections.emptyList(),
                        false);

        assertThat(actualResult, sameInstance(mockResponse));
        verifyApiCallPostWithJson("/message/v3/multisend");
        responseParserInstanceShouldBe(TalkApiClient.MultiSendResponseParser.class);
    }

    @Test
    public void testSendMessageToMultipleUsersUsingOtt() {
        final LineApiResponse<?> mockResponse = LineApiResponse.createAsSuccess("");
        givenPostWithJsonApiResponse(mockResponse);

        LineApiResponse<List<SendMessageResponse>> actualResult =
                target.sendMessageToMultipleUsers(
                        ACCESS_TOKEN,
                        Collections.emptyList(),
                        Collections.emptyList(),
                        true);

        assertThat(actualResult, sameInstance(mockResponse));

        // Gets ott by target user ids
        verifyApiCallPostWithJson("/" + BASE_PATH_MESSAGE_API + "/" + PATH_OTT_ISSUE);
        responseParserInstanceShouldBe(TalkApiClient.StringParser.class);

        // Send the message using ott
        verifyApiCallPostWithJson("/" + BASE_PATH_MESSAGE_API + "/" + PATH_OTT_SHARE);
        responseParserInstanceShouldBe(TalkApiClient.MultiSendResponseParser.class);
    }

    @Test
    public void testSendMessageToMultipleUsersUsingOtt_getOtt_failed() {
        final LineApiResponse<?> mockResponse = LineApiResponse.createAsError(
                LineApiResponseCode.NETWORK_ERROR, new LineApiError(""));
        givenPostWithJsonApiResponse(mockResponse);

        LineApiResponse<List<SendMessageResponse>> actualApiResponse =
                target.sendMessageToMultipleUsers(
                        ACCESS_TOKEN,
                        Collections.emptyList(),
                        Collections.emptyList(),
                        true);

        assertThat(actualApiResponse.getResponseCode(), sameInstance(mockResponse.getResponseCode()));
        assertThat(actualApiResponse.getErrorData(), sameInstance(mockResponse.getErrorData()));

        // Gets ott by target user ids
        verifyApiCallPostWithJson("/" + BASE_PATH_MESSAGE_API + "/" + PATH_OTT_ISSUE);
        responseParserInstanceShouldBe(TalkApiClient.StringParser.class);

        // Should not send message due to failure of getting ott
        verify(target, never()).sendMessageToMultipleUsersUsingOtt(any(), any(), any());
    }

    private void givenPostWithJsonApiResponse(LineApiResponse<?> apiResponse) {
        doReturn(apiResponse).when(httpClient).postWithJson(
                any(Uri.class),
                anyMapOf(String.class, String.class),
                any(String.class),
                any(ResponseDataParser.class));
    }

    private void verifyApiCallPostWithJson(String pathSegment) {
        verify(httpClient, times(1)).postWithJson(
                eq(Uri.parse(API_BASE_URL + pathSegment)),
                eq(Collections.singletonMap("Authorization", "Bearer " + ACCESS_TOKEN.getAccessToken())),
                anyString(),
                responseParserCaptor.capture());
    }

    private void responseParserInstanceShouldBe(Class clazz) {
        assertThat(responseParserCaptor.getValue(), instanceOf(clazz));
    }

    private static <T> void verifyResponseDataParser(
            @NonNull ResponseDataParser<T> parser,
            @NonNull T expectedData,
            @NonNull String jsonData) throws IOException {
        InputStream inputStream;
        try {
            inputStream = new TestStringInputStream(jsonData, CHARSET_NAME);
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError(e);
        }
        assertEquals(expectedData, parser.getResponseData(inputStream));
    }

    private static <T> void verifyToThrowException(
            @NonNull ResponseDataParser<T> parser, @NonNull String jsonData) {
        InputStream inputStream;
        try {
            inputStream = new TestStringInputStream(jsonData, CHARSET_NAME);
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError(e);
        }
        try {
            parser.getResponseData(inputStream);
            fail();
        } catch (IOException e) {
            // Do nothing
        }
    }
}
