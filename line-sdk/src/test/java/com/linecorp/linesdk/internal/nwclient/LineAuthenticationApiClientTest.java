package com.linecorp.linesdk.internal.nwclient;

import android.net.Uri;
import androidx.annotation.NonNull;

import com.linecorp.linesdk.BuildConfig;
import com.linecorp.linesdk.LineApiResponse;
import com.linecorp.linesdk.Scope;
import com.linecorp.linesdk.TestConfig;
import com.linecorp.linesdk.TestStringInputStream;
import com.linecorp.linesdk.internal.AccessTokenVerificationResult;
import com.linecorp.linesdk.internal.IdTokenKeyType;
import com.linecorp.linesdk.internal.InternalAccessToken;
import com.linecorp.linesdk.internal.IssueAccessTokenResult;
import com.linecorp.linesdk.internal.RefreshTokenResult;
import com.linecorp.linesdk.internal.nwclient.core.ChannelServiceHttpClient;
import com.linecorp.linesdk.internal.nwclient.core.ResponseDataParser;
import com.linecorp.linesdk.internal.pkce.PKCECode;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyMapOf;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


/**
 * Test for {@link LineAuthenticationApiClient}.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = TestConfig.TARGET_SDK_VERSION)
public class LineAuthenticationApiClientTest {
    private static final String CHARSET_NAME = "UTF-8";
    private static final String CHANNEL_ID = "123";
    private static final PKCECode PKCE_CODE = PKCECode.newCode();
    private static final String OPENID_DISCOVERY_DOC_URL = "https://test/.well-known/openid-configuration";
    private static final String API_BASE_URL = "https://test";
    private static final InternalAccessToken ACCESS_TOKEN =
            new InternalAccessToken("accessToken", 10L, 100L, "refreshToken");
    private static final LineApiResponse<?> EXPECTED_RESULT = LineApiResponse.createAsSuccess(null);

    @Mock
    private ChannelServiceHttpClient httpClient;
    @Captor
    private ArgumentCaptor<ResponseDataParser<?>> responseParserCaptor;

    private LineAuthenticationApiClient target;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        target = new LineAuthenticationApiClient(Uri.parse(OPENID_DISCOVERY_DOC_URL),
                                                 Uri.parse(API_BASE_URL),
                                                 httpClient);
    }

    @Test
    public void testIssueAccessToken() throws Exception {
        doReturn(EXPECTED_RESULT).when(httpClient).post(
                any(Uri.class),
                anyMapOf(String.class, String.class),
                anyMapOf(String.class, String.class),
                any(ResponseDataParser.class));

        LineApiResponse<IssueAccessTokenResult> actualResult =
                target.issueAccessToken(
                        CHANNEL_ID,
                        "testRequestToken",
                        PKCE_CODE,
                        "testRedirectUri");

        assertSame(EXPECTED_RESULT, actualResult);

        Map<String, String> expectedPostData = new HashMap<>(7);
        expectedPostData.put("grant_type", "authorization_code");
        expectedPostData.put("code", "testRequestToken");
        expectedPostData.put("redirect_uri", "testRedirectUri");
        expectedPostData.put("client_id", CHANNEL_ID);
        expectedPostData.put("code_verifier", PKCE_CODE.getVerifier());
        expectedPostData.put("id_token_key_type", IdTokenKeyType.JWK.name());
        expectedPostData.put("client_version", "LINE SDK Android v" + BuildConfig.VERSION_NAME);
        verify(httpClient, times(1)).post(
                eq(Uri.parse(API_BASE_URL + "/oauth2/v2.1/token")),
                eq(Collections.emptyMap()),
                eq(expectedPostData),
                responseParserCaptor.capture());

        verifyIssueAccessTokenResultParser(responseParserCaptor.getValue());
    }

    private static void verifyIssueAccessTokenResultParser(
            @NonNull ResponseDataParser<?> responseDataParser) throws Exception {
        ResponseDataParser<IssueAccessTokenResult> target =
                (ResponseDataParser<IssueAccessTokenResult>) responseDataParser;

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("access_token", "testAccessToken");
        jsonObject.put("expires_in", 1234L);
        jsonObject.put("token_type", "Bearer");
        jsonObject.put("refresh_token", "testRefreshToken");
        jsonObject.put("scope", "friends groups message.write");
        jsonObject.put("id_token_key_type", IdTokenKeyType.JWK.name());
        jsonObject.put("client_version", "LINE SDK Android v" + BuildConfig.VERSION_NAME);
        IssueAccessTokenResult issueAccessTokenResult = target.getResponseData(
                new TestStringInputStream(jsonObject.toString(), CHARSET_NAME));
        assertEquals("testAccessToken",
                issueAccessTokenResult.getAccessToken().getAccessToken());
        assertEquals("testRefreshToken",
                issueAccessTokenResult.getAccessToken().getRefreshToken());
        assertEquals(1234000,
                issueAccessTokenResult.getAccessToken().getExpiresInMillis());
        assertEquals(Arrays.asList(Scope.FRIEND, Scope.GROUP, Scope.MESSAGE),
                issueAccessTokenResult.getScopes());

        try {
            JSONObject emptyOptionalFieldData = new JSONObject()
                    .put("access_token", "testAccessToken")
                    .put("expires_in", 1234L)
                    .put("token_type", "Bearer");
            target.getResponseData(
                    new TestStringInputStream(emptyOptionalFieldData.toString(), CHARSET_NAME));
            assertTrue(false); // Test failure
        } catch (IOException e) {
            // Test success
        }
    }

    @Test
    public void testVerifyAccessToken() throws Exception {
        doReturn(EXPECTED_RESULT).when(httpClient).get(
                any(Uri.class),
                anyMapOf(String.class, String.class),
                anyMapOf(String.class, String.class),
                any(ResponseDataParser.class));

        LineApiResponse<AccessTokenVerificationResult> actualResult =
                target.verifyAccessToken(ACCESS_TOKEN);

        assertSame(EXPECTED_RESULT, actualResult);

        Map<String, String> expectedQueryParams = new HashMap<>(1);
        expectedQueryParams.put("access_token", ACCESS_TOKEN.getAccessToken());
        verify(httpClient, times(1)).get(
                eq(Uri.parse(API_BASE_URL + "/oauth2/v2.1/verify")),
                eq(Collections.<String, String>emptyMap()),
                eq(expectedQueryParams),
                responseParserCaptor.capture());

        verifyVerificationResultParser(responseParserCaptor.getValue());
    }

    private static void verifyVerificationResultParser(
            @NonNull ResponseDataParser<?> responseDataParser) throws Exception {
        ResponseDataParser<AccessTokenVerificationResult> target =
                (ResponseDataParser<AccessTokenVerificationResult>) responseDataParser;

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("client_id", "123");
        jsonObject.put("expires_in", 234L);
        jsonObject.put("scope", "friends groups");
        AccessTokenVerificationResult verificationResult = target.getResponseData(
                new TestStringInputStream(jsonObject.toString(), CHARSET_NAME));
        assertEquals("123", verificationResult.getChannelId());
        assertEquals(234000L, verificationResult.getExpiresInMillis());
        assertEquals(Arrays.asList(Scope.FRIEND, Scope.GROUP), verificationResult.getScopes());
    }

    @Test
    public void testRefreshToken() throws Exception {
        doReturn(EXPECTED_RESULT).when(httpClient).post(
                any(Uri.class),
                anyMapOf(String.class, String.class),
                anyMapOf(String.class, String.class),
                any(ResponseDataParser.class));

        LineApiResponse<RefreshTokenResult> actualResult =
                target.refreshToken(CHANNEL_ID, ACCESS_TOKEN);

        assertSame(EXPECTED_RESULT, actualResult);

        Map<String, String> expectedPostData = new HashMap<>(3);
        expectedPostData.put("grant_type", "refresh_token");
        expectedPostData.put("refresh_token", ACCESS_TOKEN.getRefreshToken());
        expectedPostData.put("client_id", CHANNEL_ID);
        verify(httpClient, times(1)).post(
                eq(Uri.parse(API_BASE_URL + "/oauth2/v2.1/token")),
                eq(Collections.<String, String>emptyMap()),
                eq(expectedPostData),
                responseParserCaptor.capture());

        verifyRefreshTokenResultParser(responseParserCaptor.getValue());
    }

    private static void verifyRefreshTokenResultParser(
            @NonNull ResponseDataParser<?> responseDataParser) throws Exception {
        ResponseDataParser<RefreshTokenResult> target =
                (ResponseDataParser<RefreshTokenResult>) responseDataParser;

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("access_token", "testAccessToken");
        jsonObject.put("expires_in", 123);
        jsonObject.put("token_type", "Bearer");
        jsonObject.put("refresh_token", "testRefreshToken");
        jsonObject.put("scope", "profile friends");

        RefreshTokenResult refreshTokenResult = target.getResponseData(
                new TestStringInputStream(jsonObject.toString(), CHARSET_NAME));
        assertEquals("testAccessToken", refreshTokenResult.getAccessToken());
        assertEquals(123000L, refreshTokenResult.getExpiresInMillis());
        assertEquals("testRefreshToken", refreshTokenResult.getRefreshToken());
        assertEquals(Arrays.asList(Scope.PROFILE, Scope.FRIEND), refreshTokenResult.getScopes());
    }

    @Test
    public void testRevokeAccessToken() throws Exception {
        LineApiResponse<?> expectedResponse = LineApiResponse.createAsSuccess(new Object());
        doReturn(expectedResponse).when(httpClient).post(
                any(Uri.class),
                anyMapOf(String.class, String.class),
                anyMapOf(String.class, String.class),
                any(ResponseDataParser.class));

        LineApiResponse<?> actualResponse = target.revokeAccessToken(CHANNEL_ID, ACCESS_TOKEN);

        assertSame(expectedResponse, actualResponse);

        Map<String, String> expectedPostData = new HashMap<>(2);
        expectedPostData.put("access_token", ACCESS_TOKEN.getAccessToken());
        expectedPostData.put("client_id", CHANNEL_ID);

        verify(httpClient, times(1)).post(
                eq(Uri.parse(API_BASE_URL + "/oauth2/v2.1/revoke")),
                eq(Collections.<String, String>emptyMap()),
                eq(expectedPostData),
                any(ResponseDataParser.class));
    }

    @Test
    public void testRevokeRefreshToken() throws Exception {
        LineApiResponse<?> expectedResponse = LineApiResponse.createAsSuccess(new Object());
        doReturn(expectedResponse).when(httpClient).post(
                any(Uri.class),
                anyMapOf(String.class, String.class),
                anyMapOf(String.class, String.class),
                any(ResponseDataParser.class));

        LineApiResponse<?> actualResponse = target.revokeRefreshToken(CHANNEL_ID, ACCESS_TOKEN);

        assertSame(expectedResponse, actualResponse);

        Map<String, String> expectedPostData = new HashMap<>(2);
        expectedPostData.put("refresh_token", ACCESS_TOKEN.getRefreshToken());
        expectedPostData.put("client_id", CHANNEL_ID);

        verify(httpClient, times(1)).post(
                eq(Uri.parse(API_BASE_URL + "/oauth2/v2.1/revoke")),
                eq(Collections.<String, String>emptyMap()),
                eq(expectedPostData),
                any(ResponseDataParser.class));
    }
}
