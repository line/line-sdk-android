package com.linecorp.linesdk.auth.internal;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;

import com.linecorp.linesdk.BuildConfig;
import com.linecorp.linesdk.Scope;
import com.linecorp.linesdk.TestConfig;
import com.linecorp.linesdk.auth.LineAuthenticationConfig;
import com.linecorp.linesdk.auth.LineAuthenticationParams;
import com.linecorp.linesdk.internal.pkce.PKCECode;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Test for {@link BrowserAuthenticationApi}.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = TestConfig.TARGET_SDK_VERSION)
public class BrowserAuthenticationApiTest {
    private static final String PACKAGE_NAME_SDK_CLIENT = "testPackageName";
    private static final String PACKAGE_NAME_LINE = "jp.naver.line.android";
    private static final String PACKAGE_BROWSER1 = "browser1";
    private static final String PACKAGE_BROWSER2 = "browser2";

    private static final String CHANNEL_ID = "123";
    private static final Uri WEB_LOGIN_PAGE_URL = Uri.parse("https://line.me");
    private static final Scope[] SCOPE_ARRAY = { Scope.FRIEND, Scope.GROUP };
    private static final List<Scope> SCOPE_LIST = Arrays.asList(SCOPE_ARRAY);
    private static final LineAuthenticationParams LINE_AUTH_PARAMS = new LineAuthenticationParams.Builder()
            .scopes(SCOPE_LIST)
            .build();
    private static final String STATE = "testState";
    private static final String NONCE = "testNonce";
    private static final String REDIRECT_URI = "test://redirect.uri";

    private static final PKCECode PKCE_CODE = PKCECode.newCode();

    private LineAuthenticationStatus authenticationStatus;
    private BrowserAuthenticationApi target;

    @Mock
    private Context context;
    @Mock
    private PackageManager packageManager;
    @Mock
    private PackageInfo linePackageInfo;
    @Mock
    private LineAuthenticationConfig config;

    @Before
    public void setUp() {
        authenticationStatus = new LineAuthenticationStatus();
        target = spy(new BrowserAuthenticationApi(authenticationStatus));
        MockitoAnnotations.initMocks(this);
        doReturn(CHANNEL_ID).when(config).getChannelId();
        doReturn(WEB_LOGIN_PAGE_URL).when(config).getWebLoginPageUrl();
        doReturn(true).when(config).isLineAppAuthenticationDisabled();
        doReturn(PACKAGE_NAME_SDK_CLIENT).when(context).getPackageName();
        setLineAppVersion("6.9.0");
    }

    @Test
    public void testGetRequest() throws Exception {
        doReturn(false).when(target).isChromeCustomTabSupported();
        Intent intent = new Intent();
        Bundle startActivityOption = new Bundle();
        BrowserAuthenticationApi.AuthenticationIntentHolder intentHolder =
                new BrowserAuthenticationApi.AuthenticationIntentHolder(
                        intent, startActivityOption, true /* isLineAppAuthentication */);
        doReturn(intentHolder).when(target)
                .getAuthenticationIntentHolder(
                        any(Context.class),
                        any(Uri.class),
                        anyBoolean() /* isLineAppAuthDisabled */);
        doReturn(REDIRECT_URI).when(target).createRedirectUri(any(Context.class));
        Uri loginUri = Uri.parse("https://test");
        doReturn(loginUri).when(target)
                .createLoginUrl(
                        any(LineAuthenticationConfig.class),
                        any(PKCECode.class),
                        any(LineAuthenticationParams.class) /* params */,
                        any(String.class) /* oAuthState */,
                        any(String.class) /* openIdNonce */,
                        any(String.class) /* redirectUri */);

        BrowserAuthenticationApi.Request request =
                target.getRequest(context, config, PKCE_CODE, LINE_AUTH_PARAMS);

        assertSame(intent, request.getIntent());
        assertSame(startActivityOption, request.getStartActivityOptions());
        assertTrue(request.isLineAppAuthentication());

        verify(target, times(1)).getAuthenticationIntentHolder(
                eq(context), any(Uri.class), eq(true) /* isLineAppAuthDisabled */);

        verify(target, times(1)).createLoginUrl(
                eq(config), eq(PKCE_CODE), eq(LINE_AUTH_PARAMS), anyString(), any(), eq(REDIRECT_URI)
        );
    }

    @Test
    public void testCreateLoginUri() throws Exception {
        Uri loginUri = target.createLoginUrl(config, PKCE_CODE, LINE_AUTH_PARAMS, STATE, NONCE, REDIRECT_URI);
        assertEquals(WEB_LOGIN_PAGE_URL.getScheme(), loginUri.getScheme());
        assertEquals(WEB_LOGIN_PAGE_URL.getAuthority(), loginUri.getAuthority());
        assertEquals(WEB_LOGIN_PAGE_URL.getPath(), loginUri.getPath());

        assertEquals(config.getChannelId(), loginUri.getQueryParameter("loginChannelId"));

        Uri returnUri = Uri.parse(loginUri.getQueryParameter("returnUri"));
        assertEquals("code", returnUri.getQueryParameter("response_type"));
        assertEquals(CHANNEL_ID, returnUri.getQueryParameter("client_id"));
        assertEquals(STATE, returnUri.getQueryParameter("state"));
        assertEquals(NONCE, returnUri.getQueryParameter("nonce"));
        assertEquals(PKCE_CODE.getChallenge(), returnUri.getQueryParameter("code_challenge"));
        assertEquals("S256", returnUri.getQueryParameter("code_challenge_method"));
        assertEquals(REDIRECT_URI, returnUri.getQueryParameter("redirect_uri"));
        assertEquals(BuildConfig.VERSION_NAME, returnUri.getQueryParameter("sdk_ver"));
        assertEquals("friends groups", returnUri.getQueryParameter("scope"));
    }

    @Test
    public void testCreateRedirectUri() throws Exception {
        assertEquals(
                "intent://result#Intent;package=" + PACKAGE_NAME_SDK_CLIENT + ";scheme=lineauth;end",
                target.createRedirectUri(context));
    }

    @Test
    public void testConvertToCompatibleIntent() throws Exception {
        Uri loginUri = Uri.parse("https://test");
        @SuppressWarnings("TooBroadScope")
        BrowserAuthenticationApi.AuthenticationIntentHolder intentHolder;
        Parcelable[] targetIntents;
        Intent intent;

        // Test for LINE and single target
        setLineAppVersion("6.9.0");
        setResultOfQueryIntentActivities(PACKAGE_NAME_LINE);
        doReturn(new ComponentName(PACKAGE_NAME_LINE, "name")).when(target).resolveActivity(any(Context.class), any(Intent.class));
        intentHolder = target.getAuthenticationIntentHolder(
                context, loginUri, false /* isLineAppAuthDisabled */);

        intent = intentHolder.getIntent();
        assertEquals(Intent.ACTION_VIEW, intent.getAction());
        assertEquals(PACKAGE_NAME_LINE, intent.getPackage());
        assertEquals(loginUri, intent.getData());
        assertTrue(intentHolder.isLineAppAuthentication());

        // Test for LINE and multiple target
        setLineAppVersion("6.9.0");
        setResultOfQueryIntentActivities(PACKAGE_NAME_LINE, PACKAGE_BROWSER1);
        intentHolder = target.getAuthenticationIntentHolder(
                context, loginUri, false /* isLineAppAuthDisabled */);

        intent = intentHolder.getIntent();
        assertEquals(Intent.ACTION_VIEW, intent.getAction());
        assertEquals(PACKAGE_NAME_LINE, intent.getPackage());
        assertEquals(loginUri, intent.getData());
        assertTrue(intentHolder.isLineAppAuthentication());
        assertNull(intentHolder.getStartActivityOptions());

        // Test for no installed LINE and single target
        setLineAppVersion("");
        setResultOfQueryIntentActivities(PACKAGE_BROWSER1);
        doReturn(null).when(target).resolveActivity(any(Context.class), any(Intent.class));
        intentHolder = target.getAuthenticationIntentHolder(
                context, loginUri, false /* isLineAppAuthDisabled */);

        intent = intentHolder.getIntent();
        assertEquals(PACKAGE_BROWSER1, intent.getPackage());
        assertEquals(loginUri, intent.getData());
        assertFalse(intentHolder.isLineAppAuthentication());

        // Test for no installed LINE and multiple target
        setLineAppVersion("");
        setResultOfQueryIntentActivities(PACKAGE_BROWSER1, PACKAGE_BROWSER2);
        intentHolder = target.getAuthenticationIntentHolder(
                context, loginUri, false /* isLineAppAuthDisabled */);

        intent = intentHolder.getIntent();
        assertEquals(Intent.ACTION_CHOOSER, intent.getAction());
        Intent firstsIntent = intent.getParcelableExtra(Intent.EXTRA_INTENT);
        assertEquals(PACKAGE_BROWSER1, firstsIntent.getPackage());
        assertEquals(loginUri, firstsIntent.getData());

        targetIntents =
                intent.getParcelableArrayExtra(Intent.EXTRA_INITIAL_INTENTS);
        assertEquals(1, targetIntents.length);
        intent = (Intent) targetIntents[0];
        assertEquals(PACKAGE_BROWSER2, intent.getPackage());
        assertEquals(loginUri, intent.getData());

        assertFalse(intentHolder.isLineAppAuthentication());
    }

    @Test
    public void getResultFrom() throws Exception {
        BrowserAuthenticationApi.Result result;
        // Test for null data
        Intent nullDataIntent = new Intent();
        result = target.getAuthenticationResultFrom(nullDataIntent);
        assertFalse(result.isSuccess());
        assertFalse(result.isAuthenticationAgentError());
        assertNotNull(result.getLineApiError().getMessage());

        // Test for illegal uri
        Intent illegalUriIntent = new Intent();
        illegalUriIntent.setData(Uri.parse(
                "lineauth://result?test=aaaa"));
        result = target.getAuthenticationResultFrom(nullDataIntent);
        assertFalse(result.isSuccess());
        assertFalse(result.isAuthenticationAgentError());
        assertNotNull(result.getLineApiError().getMessage());

        // Test for empty "state" uri
        Intent emptyStateIntent = new Intent();
        emptyStateIntent.setData(Uri.parse(
                "lineauth://result?state=&code=testCode"));
        result = target.getAuthenticationResultFrom(nullDataIntent);
        assertFalse(result.isSuccess());
        assertFalse(result.isAuthenticationAgentError());
        assertNotNull(result.getLineApiError().getMessage());

        // Test for illegal "state" uri
        Intent illegalStateIntent = new Intent();
        illegalStateIntent.setData(Uri.parse(
                "lineauth://result?state=illegalState&code=testCode"));
        result = target.getAuthenticationResultFrom(nullDataIntent);
        assertFalse(result.isSuccess());
        assertFalse(result.isAuthenticationAgentError());
        assertNotNull(result.getLineApiError().getMessage());

        // Test for correct uri
        authenticationStatus.setOAuthState(STATE);
        Intent correctUriIntent = new Intent();
        correctUriIntent.setData(Uri.parse(
                "lineauth://result?state=" + STATE + "&code=testCode"));
        result = target.getAuthenticationResultFrom(correctUriIntent);
        assertTrue(result.isSuccess());
        assertFalse(result.isAuthenticationAgentError());
        assertEquals("testCode", result.getRequestToken());

        // Test for server error
        Intent serverErrorIntent = new Intent();
        serverErrorIntent.setData(Uri.parse(
                "lineauth://result?state=" + STATE + "&error=testError&error_description=testErrorDescription"));
        result = target.getAuthenticationResultFrom(serverErrorIntent);
        assertFalse(result.isSuccess());
        assertTrue(result.isAuthenticationAgentError());
        assertEquals(
                new JSONObject()
                        .put("error", "testError")
                        .put("error_description", "testErrorDescription")
                        .toString(),
                result.getLineApiError().getMessage());
    }

    private void setLineAppVersion(@Nullable String versionName) {
        try {
            doReturn(packageManager).when(context).getPackageManager();
            doReturn(linePackageInfo).when(packageManager)
                    .getPackageInfo(PACKAGE_NAME_LINE, PackageManager.GET_META_DATA);
            Field field = PackageInfo.class.getDeclaredField("versionName");
            field.setAccessible(true);
            field.set(linePackageInfo, versionName);
        } catch (PackageManager.NameNotFoundException | IllegalAccessException | NoSuchFieldException e) {
            throw new AssertionError(e);
        }
    }

    private void setResultOfQueryIntentActivities(@NonNull String... packageNames) {
        Collection<ResolveInfo> targetApps = new ArrayList<>(packageNames.length);
        for (String packageName : packageNames) {
            ResolveInfo resolveInfo = new ResolveInfo();
            resolveInfo.activityInfo = new ActivityInfo();
            resolveInfo.activityInfo.packageName = packageName;
            targetApps.add(resolveInfo);
        }
        //noinspection WrongConstant
        doReturn(targetApps).when(packageManager)
                .queryIntentActivities(any(Intent.class), anyInt());
    }

    @After
    public void tearDown() throws Exception {

    }
}
