package com.linecorp.linesdk.api.internal;

import android.net.Uri;

import com.linecorp.linesdk.LineAccessToken;
import com.linecorp.linesdk.LineApiError;
import com.linecorp.linesdk.LineApiResponse;
import com.linecorp.linesdk.LineApiResponseCode;
import com.linecorp.linesdk.LineCredential;
import com.linecorp.linesdk.LineFriendshipStatus;
import com.linecorp.linesdk.LineProfile;
import com.linecorp.linesdk.Scope;
import com.linecorp.linesdk.TestConfig;
import com.linecorp.linesdk.TestStringCipher;
import com.linecorp.linesdk.api.LineApiClient;
import com.linecorp.linesdk.internal.AccessTokenCache;
import com.linecorp.linesdk.internal.AccessTokenVerificationResult;
import com.linecorp.linesdk.internal.InternalAccessToken;
import com.linecorp.linesdk.internal.RefreshTokenResult;
import com.linecorp.linesdk.internal.nwclient.LineAuthenticationApiClient;
import com.linecorp.linesdk.internal.nwclient.TalkApiClient;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Test for {@link LineApiClient}.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = TestConfig.TARGET_SDK_VERSION)
public class LineApiClientImplTest {
    private static final String CHANNEL_ID = "123";
    private static final InternalAccessToken ACCESS_TOKEN =
            new InternalAccessToken("accessToken", 10L, 100L, "refreshToken");
    private static final LineProfile PROFILE = new LineProfile(
            "mid", "displayName", Uri.parse("https://picture.url"), "statusMessage");
    private static final LineFriendshipStatus FRIENDSHIP_STATUS = new LineFriendshipStatus(true);

    @Mock
    private LineAuthenticationApiClient internalOauthApiClient;
    @Mock
    private TalkApiClient internalTalkApiClient;
    private AccessTokenCache accessTokenCache;

    private LineApiClient target;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        accessTokenCache = new AccessTokenCache(
                RuntimeEnvironment.application, CHANNEL_ID, new TestStringCipher());
        target = new LineApiClientImpl(
                CHANNEL_ID,
                internalOauthApiClient,
                internalTalkApiClient,
                accessTokenCache);
    }

    @Test
    public void testLogout() {
        LineApiResponse<?> expectedResponse = LineApiResponse.createAsSuccess(new Object());
        doReturn(expectedResponse)
                .when(internalOauthApiClient)
                .revokeRefreshToken(anyString(), any(InternalAccessToken.class));
        accessTokenCache.saveAccessToken(ACCESS_TOKEN);

        LineApiResponse<?> actualResponse = target.logout();

        assertSame(expectedResponse, actualResponse);
        verify(internalOauthApiClient, times(1)).revokeRefreshToken(CHANNEL_ID, ACCESS_TOKEN);
        assertNull(accessTokenCache.getAccessToken());
    }

    @Test
    public void testLogoutWithApiCallError() {
        LineApiResponse<?> expectedResponse = LineApiResponse.createAsError(
                LineApiResponseCode.INTERNAL_ERROR, LineApiError.DEFAULT);
        doReturn(expectedResponse)
                .when(internalOauthApiClient)
                .revokeRefreshToken(anyString(), any(InternalAccessToken.class));
        accessTokenCache.saveAccessToken(ACCESS_TOKEN);

        LineApiResponse<?> actualResponse = target.logout();

        assertSame(expectedResponse, actualResponse);
        verify(internalOauthApiClient, times(1)).revokeRefreshToken(CHANNEL_ID, ACCESS_TOKEN);
        assertNull(accessTokenCache.getAccessToken());
    }

    @Test
    public void testLogoutWithNoAccessToken() {
        LineApiResponse<?> response = target.logout();
        assertFalse(response.isSuccess());
        assertSame(response.getResponseCode(), LineApiResponseCode.INTERNAL_ERROR);
    }

    @Test
    public void testRefreshToken() {
        accessTokenCache.saveAccessToken(ACCESS_TOKEN);
        LineApiResponse<RefreshTokenResult> refreshTokenResponse =
                LineApiResponse.createAsSuccess(new RefreshTokenResult(
                        "newAccessToken", 999L, "newRefreshToken", Collections.emptyList()));
        doReturn(refreshTokenResponse)
                .when(internalOauthApiClient)
                .refreshToken(anyString(), any(InternalAccessToken.class));

        LineApiResponse<LineAccessToken> actualResponse = target.refreshAccessToken();

        verify(internalOauthApiClient, times(1)).refreshToken(CHANNEL_ID, ACCESS_TOKEN);
        assertTrue(actualResponse.isSuccess());
        assertNotNull(actualResponse.getResponseData());
        assertEquals("newAccessToken", actualResponse.getResponseData().getTokenString());
        assertEquals(999L, actualResponse.getResponseData().getExpiresInMillis());
        InternalAccessToken newAccessToken =
                accessTokenCache.getAccessToken();
        assertNotNull(newAccessToken);
        assertEquals("newAccessToken", newAccessToken.getAccessToken());
        assertEquals(999L, newAccessToken.getExpiresInMillis());
        assertTrue(newAccessToken.getIssuedClientTimeMillis() > 0);
        assertEquals("newRefreshToken", newAccessToken.getRefreshToken());
    }

    @Test
    public void testRefreshTokenWithNoRefreshToken() {
        accessTokenCache.saveAccessToken(ACCESS_TOKEN);
        LineApiResponse<RefreshTokenResult> refreshTokenResponse =
                LineApiResponse.createAsSuccess(new RefreshTokenResult(
                        "newAccessToken", 999L, null, Collections.emptyList()));
        doReturn(refreshTokenResponse)
                .when(internalOauthApiClient)
                .refreshToken(anyString(), any(InternalAccessToken.class));

        LineApiResponse<LineAccessToken> actualResponse = target.refreshAccessToken();

        verify(internalOauthApiClient, times(1)).refreshToken(CHANNEL_ID, ACCESS_TOKEN);
        assertTrue(actualResponse.isSuccess());
        assertNotNull(actualResponse.getResponseData());
        assertEquals("newAccessToken", actualResponse.getResponseData().getTokenString());
        assertEquals(999L, actualResponse.getResponseData().getExpiresInMillis());
        assertTrue(actualResponse.getResponseData().getIssuedClientTimeMillis() > 0);
        InternalAccessToken newAccessToken =
                accessTokenCache.getAccessToken();
        assertNotNull(newAccessToken);
        assertEquals("newAccessToken", newAccessToken.getAccessToken());
        assertEquals(999L, newAccessToken.getExpiresInMillis());
        assertTrue(newAccessToken.getIssuedClientTimeMillis() > 0);
        assertEquals(ACCESS_TOKEN.getRefreshToken(), newAccessToken.getRefreshToken());
    }

    @Test
    public void testRefreshTokenWithApiError() {
        accessTokenCache.saveAccessToken(ACCESS_TOKEN);
        LineApiResponse<RefreshTokenResult> refreshTokenResponse =
                LineApiResponse.createAsError(
                        LineApiResponseCode.INTERNAL_ERROR, LineApiError.DEFAULT);
        doReturn(refreshTokenResponse)
                .when(internalOauthApiClient)
                .refreshToken(anyString(), any(InternalAccessToken.class));

        LineApiResponse<LineAccessToken> actualResponse = target.refreshAccessToken();

        verify(internalOauthApiClient, times(1)).refreshToken(CHANNEL_ID, ACCESS_TOKEN);
        assertFalse(actualResponse.isSuccess());
        assertEquals(ACCESS_TOKEN, accessTokenCache.getAccessToken());
    }

    @Test
    public void testGetProfile() {
        LineApiResponse<LineProfile> expectedResponse = LineApiResponse.createAsSuccess(PROFILE);
        doReturn(expectedResponse)
                .when(internalTalkApiClient)
                .getProfile(any(InternalAccessToken.class));
        accessTokenCache.saveAccessToken(ACCESS_TOKEN);

        LineApiResponse<LineProfile> actualResponse = target.getProfile();

        assertSame(expectedResponse, actualResponse);
        verify(internalTalkApiClient, times(1)).getProfile(ACCESS_TOKEN);
    }

    @Test
    public void testGetProfileWithApiCallError() {
        LineApiResponse<LineProfile> expectedResponse = LineApiResponse.createAsError(
                LineApiResponseCode.INTERNAL_ERROR, LineApiError.DEFAULT);
        doReturn(expectedResponse)
                .when(internalTalkApiClient)
                .getProfile(any(InternalAccessToken.class));
        accessTokenCache.saveAccessToken(ACCESS_TOKEN);

        LineApiResponse<LineProfile> actualResponse = target.getProfile();

        assertSame(expectedResponse, actualResponse);
        verify(internalTalkApiClient, times(1)).getProfile(ACCESS_TOKEN);
    }

    @Test
    public void testGetProfileWithNoAccessToken() {
        LineApiResponse<?> response = target.getProfile();
        assertFalse(response.isSuccess());
        assertSame(response.getResponseCode(), LineApiResponseCode.INTERNAL_ERROR);
    }

    @Test
    public void testGetFriendshipStatus() {
        LineApiResponse<LineFriendshipStatus> expectedResponse =
                LineApiResponse.createAsSuccess(FRIENDSHIP_STATUS);
        doReturn(expectedResponse)
                .when(internalTalkApiClient)
                .getFriendshipStatus(any(InternalAccessToken.class));
        accessTokenCache.saveAccessToken(ACCESS_TOKEN);

        LineApiResponse<LineFriendshipStatus> actualResponse = target.getFriendshipStatus();

        assertSame(expectedResponse, actualResponse);
        verify(internalTalkApiClient, times(1)).getFriendshipStatus(ACCESS_TOKEN);
    }

    @Test
    public void testGetFriendshipStatusWithApiCallError() {
        LineApiResponse<LineFriendshipStatus> expectedResponse = LineApiResponse.createAsError(
                LineApiResponseCode.INTERNAL_ERROR, LineApiError.DEFAULT);
        doReturn(expectedResponse)
                .when(internalTalkApiClient)
                .getFriendshipStatus(any(InternalAccessToken.class));
        accessTokenCache.saveAccessToken(ACCESS_TOKEN);

        LineApiResponse<LineFriendshipStatus> actualResponse = target.getFriendshipStatus();

        assertSame(expectedResponse, actualResponse);
        verify(internalTalkApiClient, times(1)).getFriendshipStatus(ACCESS_TOKEN);
    }

    @Test
    public void testGetFriendshipStatusWithNoAccessToken() {
        LineApiResponse<?> response = target.getFriendshipStatus();
        assertFalse(response.isSuccess());
        assertSame(response.getResponseCode(), LineApiResponseCode.INTERNAL_ERROR);
    }

    @Test
    public void testVerifyToken() {
        LineApiResponse<AccessTokenVerificationResult> expectedResponse =
                LineApiResponse.createAsSuccess(new AccessTokenVerificationResult(
                        "1234", 3L, Arrays.asList(Scope.FRIEND, Scope.GROUP)));
        doReturn(expectedResponse)
                .when(internalOauthApiClient)
                .verifyAccessToken(any(InternalAccessToken.class));
        accessTokenCache.saveAccessToken(ACCESS_TOKEN);

        LineApiResponse<LineCredential> actualResponse = target.verifyToken();

        assertTrue(actualResponse.isSuccess());
        verify(internalOauthApiClient, times(1)).verifyAccessToken(ACCESS_TOKEN);

        LineAccessToken verifiedAccessToken = actualResponse.getResponseData().getAccessToken();
        assertEquals(ACCESS_TOKEN.getAccessToken(), verifiedAccessToken.getTokenString());
        assertEquals(3L, verifiedAccessToken.getExpiresInMillis());
        assertTrue(ACCESS_TOKEN.getIssuedClientTimeMillis()
                   != verifiedAccessToken.getIssuedClientTimeMillis());
        assertEquals(3L, verifiedAccessToken.getExpiresInMillis());
        assertEquals(Arrays.asList(Scope.FRIEND, Scope.GROUP),
                     actualResponse.getResponseData().getScopes());

        InternalAccessToken newAccessToken = accessTokenCache.getAccessToken();
        assertEquals(ACCESS_TOKEN.getAccessToken(), newAccessToken.getAccessToken());
        assertEquals(3L, newAccessToken.getExpiresInMillis());
        assertTrue(newAccessToken.getIssuedClientTimeMillis()
                   != ACCESS_TOKEN.getIssuedClientTimeMillis());
        assertEquals(ACCESS_TOKEN.getRefreshToken(), newAccessToken.getRefreshToken());
    }

    @Test
    public void testVerifyTokenWithNoAccessToken() {
        LineApiResponse<LineCredential> response = target.verifyToken();
        assertFalse(response.isSuccess());
        assertSame(response.getResponseCode(), LineApiResponseCode.INTERNAL_ERROR);
    }

    @Test
    public void testGetCurrentAccessToken() {
        LineApiResponse<LineAccessToken> response;
        LineAccessToken actualAccessToken;

        // the cached access token is null.
        response = target.getCurrentAccessToken();
        assertFalse(response.isSuccess());

        // the cached access token is not null.
        accessTokenCache.saveAccessToken(ACCESS_TOKEN);
        response = target.getCurrentAccessToken();

        assertTrue(response.isSuccess());
        actualAccessToken = response.getResponseData();
        assertNotNull(actualAccessToken);
        assertEquals(ACCESS_TOKEN.getAccessToken(), actualAccessToken.getTokenString());
        assertEquals(ACCESS_TOKEN.getExpiresInMillis(),
                     actualAccessToken.getExpiresInMillis());
        assertEquals(ACCESS_TOKEN.getIssuedClientTimeMillis(),
                     actualAccessToken.getIssuedClientTimeMillis());
    }
}
