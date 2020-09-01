package com.linecorp.linesdk.api.internal;

import com.linecorp.linesdk.LineApiError;
import com.linecorp.linesdk.LineApiResponse;
import com.linecorp.linesdk.LineApiResponseCode;
import com.linecorp.linesdk.LineCredential;
import com.linecorp.linesdk.LineProfile;
import com.linecorp.linesdk.TestConfig;
import com.linecorp.linesdk.api.LineApiClient;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.net.HttpURLConnection;

import androidx.annotation.NonNull;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test for {@link AutoRefreshLineApiClientProxy}.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = TestConfig.TARGET_SDK_VERSION)
public class AutoRefreshLineApiClientProxyTest {
    private static class Results {
        private static final LineApiResponse<?> UNAUTHORIZED = LineApiResponse.createAsError(
                LineApiResponseCode.SERVER_ERROR,
                LineApiError.createWithHttpResponseCode(HttpURLConnection.HTTP_UNAUTHORIZED, "errorMessage"));
        private static final LineApiResponse<?> NETWORK_ERROR = LineApiResponse.createAsError(
                LineApiResponseCode.NETWORK_ERROR,
                LineApiError.DEFAULT);
        private static final LineApiResponse<?> SUCCESS = LineApiResponse.createAsSuccess(null);

        @NonNull
        private static <T> LineApiResponse<T> unauthorized() {
            return (LineApiResponse<T>) UNAUTHORIZED;
        }

        @NonNull
        private static <T> LineApiResponse<T> networkError() {
            return (LineApiResponse<T>) NETWORK_ERROR;
        }

        @NonNull
        private static <T> LineApiResponse<T> success() {
            return (LineApiResponse<T>) SUCCESS;
        }
    }

    @Mock
    private LineApiClientImpl lineApiClientImpl;
    private LineApiClient target;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        target = AutoRefreshLineApiClientProxy.newProxy(lineApiClientImpl);
    }

    @Test
    public void testAutoRefreshWithUnauthorizedError() throws Exception {
        when(lineApiClientImpl.getProfile()).thenReturn(
                Results.unauthorized(),
                Results.success());
        when(lineApiClientImpl.refreshAccessToken()).thenReturn(
                Results.success());

        LineApiResponse<LineProfile> response = target.getProfile();

        assertTrue(response.isSuccess());
        verify(lineApiClientImpl, times(1)).refreshAccessToken();
        verify(lineApiClientImpl, times(2)).getProfile();
    }

    @Test
    public void testNoRefreshWithoutAnnotation() throws Exception {
        when(lineApiClientImpl.verifyToken()).thenReturn(
                Results.unauthorized());

        LineApiResponse<LineCredential> response = target.verifyToken();

        assertEquals(LineApiResponseCode.SERVER_ERROR, response.getResponseCode());
        assertEquals(HttpURLConnection.HTTP_UNAUTHORIZED,
                response.getErrorData().getHttpResponseCode());
        verify(lineApiClientImpl, never()).refreshAccessToken();
        verify(lineApiClientImpl, times(1)).verifyToken();
    }

    @Test
    public void testNoRefreshWithApiSuccess() throws Exception {
        when(lineApiClientImpl.getProfile()).thenReturn(
                Results.success());

        LineApiResponse<LineProfile> response = target.getProfile();

        assertTrue(response.isSuccess());
        verify(lineApiClientImpl, never()).refreshAccessToken();
        verify(lineApiClientImpl, times(1)).getProfile();
    }

    @Test
    public void testRefreshErrorByNetworkError() throws Exception {
        when(lineApiClientImpl.getProfile()).thenReturn(
                Results.unauthorized());
        when(lineApiClientImpl.refreshAccessToken()).thenReturn(
                Results.networkError());

        LineApiResponse<LineProfile> response = target.getProfile();

        assertTrue(response.isNetworkError());
        verify(lineApiClientImpl, times(1)).refreshAccessToken();
        verify(lineApiClientImpl, times(1)).getProfile();
    }

    @Test
    public void testRefreshErrorByServerError() throws Exception {
        when(lineApiClientImpl.getProfile()).thenReturn(
                Results.unauthorized());
        when(lineApiClientImpl.refreshAccessToken()).thenReturn(
                Results.unauthorized());

        LineApiResponse<LineProfile> response = target.getProfile();

        assertEquals(LineApiResponseCode.SERVER_ERROR, response.getResponseCode());
        assertEquals(HttpURLConnection.HTTP_UNAUTHORIZED,
                response.getErrorData().getHttpResponseCode());
        verify(lineApiClientImpl, times(1)).refreshAccessToken();
        verify(lineApiClientImpl, times(1)).getProfile();
    }

    @Test(expected = NullPointerException.class)
    public void testThrowingException() throws Exception {
        when(lineApiClientImpl.getProfile()).thenThrow(new NullPointerException("testException"));
        target.getProfile();
    }
}
