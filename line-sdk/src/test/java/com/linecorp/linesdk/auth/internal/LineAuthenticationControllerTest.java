package com.linecorp.linesdk.auth.internal;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.linecorp.linesdk.LineAccessToken;
import com.linecorp.linesdk.LineApiError;
import com.linecorp.linesdk.LineApiResponse;
import com.linecorp.linesdk.LineApiResponseCode;
import com.linecorp.linesdk.LineCredential;
import com.linecorp.linesdk.LineIdToken;
import com.linecorp.linesdk.LineProfile;
import com.linecorp.linesdk.Scope;
import com.linecorp.linesdk.TestConfig;
import com.linecorp.linesdk.TestStringCipher;
import com.linecorp.linesdk.auth.LineAuthenticationConfig;
import com.linecorp.linesdk.auth.LineAuthenticationParams;
import com.linecorp.linesdk.auth.LineLoginResult;
import com.linecorp.linesdk.internal.AccessTokenCache;
import com.linecorp.linesdk.internal.InternalAccessToken;
import com.linecorp.linesdk.internal.IssueAccessTokenResult;
import com.linecorp.linesdk.internal.OpenIdDiscoveryDocument;
import com.linecorp.linesdk.internal.nwclient.LineAuthenticationApiClient;
import com.linecorp.linesdk.internal.nwclient.TalkApiClient;
import com.linecorp.linesdk.internal.pkce.PKCECode;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Test for {@link LineAuthenticationController}.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = TestConfig.TARGET_SDK_VERSION)
public class LineAuthenticationControllerTest {
    private static final String ISSUER = "https://access.line.me";
    private static final String CHANNEL_ID = "testChannelId";

    private static final String USER_ID = "userId";

    private static final PKCECode PKCE_CODE = PKCECode.newCode();
    private static final String NONCE = "testNonce";
    private static final String REDIRECT_URI = "test://redirect.uri";

    private static final String REQUEST_TOKEN_STR = "requestToken";
    private static final String ACCESS_TOKEN_STR = "accessToken";
    private static final long EXPIRES_IN = 1000;
    private static final long ISSUED_CLIENT_TIME = 2000;
    private static final String REFRESH_TOKEN_STR = "refreshToken";
    private static final InternalAccessToken ACCESS_TOKEN = new InternalAccessToken(
            ACCESS_TOKEN_STR, EXPIRES_IN, ISSUED_CLIENT_TIME, REFRESH_TOKEN_STR);

    private static final Date NOW = new Date();
    private static final Date ONE_HOUR_LATER = new Date(NOW.getTime() + 60 * 60 * 1000);

    private static final String ID_TOKEN_RAW_STR = "ID_TOKEN_RAW_STR";
    private static final LineIdToken ID_TOKEN = new LineIdToken
            .Builder()
            .rawString(ID_TOKEN_RAW_STR)
            .issuer(ISSUER)
            .subject(USER_ID)
            .audience(CHANNEL_ID)
            .issuedAt(NOW)
            .expiresAt(ONE_HOUR_LATER)
            .nonce(NONCE)
            .build();

    private static final Scope[] SCOPE_ARRAY = { Scope.PROFILE, Scope.FRIEND, Scope.GROUP };
    private static final List<Scope> SCOPE_LIST = Arrays.asList(SCOPE_ARRAY);

    private static final LineAuthenticationParams LINE_AUTH_PARAMS =
            new LineAuthenticationParams.Builder()
                    .scopes(SCOPE_LIST)
                    .build();

    private static final IssueAccessTokenResult ISSUE_ACCESS_TOKEN_RESULT =
            new IssueAccessTokenResult(ACCESS_TOKEN, SCOPE_LIST, ID_TOKEN);

    private static final String DISPLAY_NAME = "displayName";
    private static final Uri PICTURE_URL = Uri.parse("http://line.me/test");
    private static final String STATUS_MESSAGE = "statusMessage";

    private static final LineProfile ACCOUNT_INFO = new LineProfile(
            USER_ID, DISPLAY_NAME, PICTURE_URL, STATUS_MESSAGE);

    private static final OpenIdDiscoveryDocument OPEN_ID_DISCOVERY_DOCUMENT =
            new OpenIdDiscoveryDocument.Builder()
                    .issuer(ISSUER)
                    .build();

    private static final Intent LOGIN_INTENT = new Intent();

    private LineAuthenticationController target;

    private LineAuthenticationConfig config;
    @Mock
    private LineAuthenticationActivity activity;
    @Mock
    private LineAuthenticationApiClient authApiClient;
    @Mock
    private TalkApiClient talkApiClient;
    @Mock
    private BrowserAuthenticationApi browserAuthenticationApi;
    private AccessTokenCache accessTokenCache;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        config = new LineAuthenticationConfig.Builder(CHANNEL_ID).build();
        accessTokenCache = new AccessTokenCache(
                RuntimeEnvironment.application, CHANNEL_ID, new TestStringCipher());
        LineAuthenticationStatus authenticationStatus = new LineAuthenticationStatus();
        authenticationStatus.setOpenIdNonce(NONCE);

        final LineAuthenticationController controller = new LineAuthenticationController(
                activity,
                config,
                authApiClient,
                talkApiClient,
                browserAuthenticationApi,
                accessTokenCache,
                authenticationStatus,
                LINE_AUTH_PARAMS);
        target =  Mockito.spy(controller);
        doReturn(PKCE_CODE).when(target).createPKCECode();

        doReturn(RuntimeEnvironment.application).when(activity).getApplicationContext();
        doReturn(new BrowserAuthenticationApi.Request(
                LOGIN_INTENT, null /* startActivityOption */, REDIRECT_URI, false /* isLineAppAuthentication */))
                .when(browserAuthenticationApi)
                .getRequest(
                        any(Context.class),
                        any(LineAuthenticationConfig.class),
                        any(PKCECode.class),
                        any(LineAuthenticationParams.class));
    }

    @Test
    public void testErrorOfRequestTokenProvider() throws Exception {
        Intent newIntentData = new Intent();
        doReturn(BrowserAuthenticationApi.Result.createAsInternalError("internalErrorMessage"))
                .when(browserAuthenticationApi)
                .getAuthenticationResultFrom(newIntentData);

        target.startLineAuthentication();

        Robolectric.getBackgroundThreadScheduler().runOneTask();
        Robolectric.getForegroundThreadScheduler().runOneTask();

        verify(browserAuthenticationApi, times(1))
                .getRequest(activity, config, PKCE_CODE, LINE_AUTH_PARAMS);

        target.handleIntentFromLineApp(newIntentData);

        verify(activity, times(1)).onAuthenticationFinished(
                LineLoginResult.error(LineApiResponseCode.INTERNAL_ERROR, any()));
    }

    @Test
    public void testNetworkErrorOfGettingAccessToken() throws Exception {
        Intent newIntentData = new Intent();
        doReturn(BrowserAuthenticationApi.Result.createAsSuccess(REQUEST_TOKEN_STR, false))
                .when(browserAuthenticationApi)
                .getAuthenticationResultFrom(newIntentData);
        doReturn(LineApiResponse.createAsError(LineApiResponseCode.NETWORK_ERROR, LineApiError.DEFAULT))
                .when(authApiClient)
                .issueAccessToken(CHANNEL_ID, REQUEST_TOKEN_STR, PKCE_CODE, REDIRECT_URI);

        target.startLineAuthentication();

        Robolectric.getBackgroundThreadScheduler().runOneTask();
        Robolectric.getForegroundThreadScheduler().runOneTask();

        verify(browserAuthenticationApi, times(1))
                .getRequest(activity, config, PKCE_CODE, LINE_AUTH_PARAMS);

        target.handleIntentFromLineApp(newIntentData);

        Robolectric.getBackgroundThreadScheduler().runOneTask();
        Robolectric.getForegroundThreadScheduler().runOneTask();

        verify(activity, times(1)).onAuthenticationFinished(
                LineLoginResult.error(LineApiResponseCode.NETWORK_ERROR, LineApiError.DEFAULT));
    }

    @Test
    public void testInternalErrorOfGettingAccessToken() throws Exception {
        Intent newIntentData = new Intent();
        doReturn(BrowserAuthenticationApi.Result.createAsSuccess(REQUEST_TOKEN_STR, null))
                .when(browserAuthenticationApi)
                .getAuthenticationResultFrom(newIntentData);
        doReturn(LineApiResponse.createAsError(LineApiResponseCode.INTERNAL_ERROR, LineApiError.DEFAULT))
                .when(authApiClient)
                .issueAccessToken(CHANNEL_ID, REQUEST_TOKEN_STR, PKCE_CODE, REDIRECT_URI);

        target.startLineAuthentication();

        Robolectric.getBackgroundThreadScheduler().runOneTask();
        Robolectric.getForegroundThreadScheduler().runOneTask();

        verify(browserAuthenticationApi, times(1))
                .getRequest(activity, config, PKCE_CODE, LINE_AUTH_PARAMS);

        target.handleIntentFromLineApp(newIntentData);

        Robolectric.getBackgroundThreadScheduler().runOneTask();
        Robolectric.getForegroundThreadScheduler().runOneTask();

        verify(activity, times(1)).onAuthenticationFinished(
                LineLoginResult.error(LineApiResponseCode.INTERNAL_ERROR, LineApiError.DEFAULT));
    }

    @Test
    public void testNetworkErrorOfGettingAccountInfo() throws Exception {
        Intent newIntentData = new Intent();
        doReturn(BrowserAuthenticationApi.Result.createAsSuccess(REQUEST_TOKEN_STR, null))
                .when(browserAuthenticationApi)
                .getAuthenticationResultFrom(newIntentData);
        doReturn(LineApiResponse.createAsSuccess(ISSUE_ACCESS_TOKEN_RESULT))
                .when(authApiClient)
                .issueAccessToken(CHANNEL_ID, REQUEST_TOKEN_STR, PKCE_CODE, REDIRECT_URI);
        doReturn(LineApiResponse.createAsError(LineApiResponseCode.NETWORK_ERROR, LineApiError.DEFAULT))
                .when(talkApiClient)
                .getProfile(ACCESS_TOKEN);

        target.startLineAuthentication();

        Robolectric.getBackgroundThreadScheduler().runOneTask();
        Robolectric.getForegroundThreadScheduler().runOneTask();

        verify(browserAuthenticationApi, times(1))
                .getRequest(activity, config, PKCE_CODE, LINE_AUTH_PARAMS);

        target.handleIntentFromLineApp(newIntentData);

        Robolectric.getBackgroundThreadScheduler().runOneTask();
        Robolectric.getForegroundThreadScheduler().runOneTask();

        verify(activity, times(1)).onAuthenticationFinished(
                LineLoginResult.error(LineApiResponseCode.NETWORK_ERROR, LineApiError.DEFAULT));
    }

    @Test
    public void testInternalErrorOfGettingAccountInfo() throws Exception {
        Intent newIntentData = new Intent();
        doReturn(BrowserAuthenticationApi.Result.createAsSuccess(REQUEST_TOKEN_STR, null))
                .when(browserAuthenticationApi)
                .getAuthenticationResultFrom(newIntentData);
        doReturn(LineApiResponse.createAsSuccess(ISSUE_ACCESS_TOKEN_RESULT))
                .when(authApiClient)
                .issueAccessToken(CHANNEL_ID, REQUEST_TOKEN_STR, PKCE_CODE, REDIRECT_URI);
        doReturn(LineApiResponse.createAsError(LineApiResponseCode.INTERNAL_ERROR, LineApiError.DEFAULT))
                .when(talkApiClient)
                .getProfile(ACCESS_TOKEN);

        target.startLineAuthentication();

        Robolectric.getBackgroundThreadScheduler().runOneTask();
        Robolectric.getForegroundThreadScheduler().runOneTask();

        verify(browserAuthenticationApi, times(1))
                .getRequest(activity, config, PKCE_CODE, LINE_AUTH_PARAMS);

        target.handleIntentFromLineApp(newIntentData);

        Robolectric.getBackgroundThreadScheduler().runOneTask();
        Robolectric.getForegroundThreadScheduler().runOneTask();

        verify(activity, times(1)).onAuthenticationFinished(
                LineLoginResult.error(LineApiResponseCode.INTERNAL_ERROR, LineApiError.DEFAULT));
    }

    @Test
    public void testSuccess() throws Exception {
        Intent newIntentData = new Intent();
        doReturn(BrowserAuthenticationApi.Result.createAsSuccess(REQUEST_TOKEN_STR, null))
                .when(browserAuthenticationApi)
                .getAuthenticationResultFrom(newIntentData);
        doReturn(LineApiResponse.createAsSuccess(ISSUE_ACCESS_TOKEN_RESULT))
                .when(authApiClient)
                .issueAccessToken(CHANNEL_ID, REQUEST_TOKEN_STR, PKCE_CODE, REDIRECT_URI);
        doReturn(LineApiResponse.createAsSuccess(ACCOUNT_INFO))
                .when(talkApiClient)
                .getProfile(ACCESS_TOKEN);
        doReturn(LineApiResponse.createAsSuccess(OPEN_ID_DISCOVERY_DOCUMENT))
                .when(authApiClient)
                .getOpenIdDiscoveryDocument();

        target.startLineAuthentication();

        Robolectric.getBackgroundThreadScheduler().runOneTask();
        Robolectric.getForegroundThreadScheduler().runOneTask();

        verify(browserAuthenticationApi, times(1))
                .getRequest(activity, config, PKCE_CODE, LINE_AUTH_PARAMS);

        target.onActivityResult(3 /* requestCode */, 0 /* resultCode */, null /* data */);
        target.handleIntentFromLineApp(newIntentData);

        Robolectric.getBackgroundThreadScheduler().runOneTask();
        Robolectric.getForegroundThreadScheduler().runOneTask();

        verify(activity, times(1)).onAuthenticationFinished(
                new LineLoginResult.Builder()
                        .responseCode(LineApiResponseCode.SUCCESS)
                        .nonce(NONCE)
                        .lineProfile(new LineProfile(USER_ID, DISPLAY_NAME, PICTURE_URL, STATUS_MESSAGE))
                        .lineIdToken(ID_TOKEN)
                        .friendshipStatusChanged(null)
                        .lineCredential(new LineCredential(
                                new LineAccessToken(ACCESS_TOKEN_STR, EXPIRES_IN, ISSUED_CLIENT_TIME),
                                SCOPE_LIST))
                        .errorData(LineApiError.DEFAULT)
                        .build()
        );

        assertEquals(ACCESS_TOKEN, accessTokenCache.getAccessToken());
    }

    @Test
    public void testCancel() throws Exception {
        target.startLineAuthentication();

        Robolectric.getBackgroundThreadScheduler().runOneTask();
        Robolectric.getForegroundThreadScheduler().runOneTask();

        verify(browserAuthenticationApi, times(1))
                .getRequest(activity, config, PKCE_CODE, LINE_AUTH_PARAMS);

        target.onActivityResult(3 /* requestCode */, 0 /* resultCode */, null /* data */);

        verify(activity, never()).onAuthenticationFinished(any(LineLoginResult.class));

        Robolectric.getForegroundThreadScheduler().runOneTask();

        verify(activity, times(1)).onAuthenticationFinished(LineLoginResult.canceledError());
    }
}
