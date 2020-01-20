package com.linecorp.linesdk.internal.nwclient.core;

import android.net.Uri;
import androidx.annotation.NonNull;

import com.linecorp.linesdk.BuildConfig;
import com.linecorp.linesdk.LineApiError;
import com.linecorp.linesdk.LineApiResponse;
import com.linecorp.linesdk.LineApiResponseCode;
import com.linecorp.linesdk.TestConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;


/**
 * Test for {@link ChannelServiceHttpClient}.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = TestConfig.TARGET_SDK_VERSION)
public class ChannelServiceHttpClientTest {
    private static final String CHARSET = "UTF-8";
    private static final String USER_AGENT = "testUserAgent";
    private static final int CONNECT_TIMEOUT_MILLIS = 100;
    private static final int READ_TIMEOUT_MILLIS = 200;

    private ChannelServiceHttpClient target;
    @Mock
    private UserAgentGenerator userAgentGenerator;
    @Mock
    private HttpsURLConnection httpsURLConnection;
    private ByteArrayOutputStream connectionOutputStream;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        target = spy(new ChannelServiceHttpClient(userAgentGenerator));
        target.setConnectTimeoutMillis(CONNECT_TIMEOUT_MILLIS);
        target.setReadTimeoutMillis(READ_TIMEOUT_MILLIS);
        doReturn(httpsURLConnection).when(target).openHttpConnection(any(Uri.class));

        connectionOutputStream = new ByteArrayOutputStream();
        doReturn(connectionOutputStream).when(httpsURLConnection).getOutputStream();

        doReturn(USER_AGENT).when(userAgentGenerator).getUserAgent();
    }

    @Test
    public void testPost() throws Exception {
        Map<String, String> requestHeaders = new HashMap();
        requestHeaders.put("headerKey1", "headerValue1");
        requestHeaders.put("headerKey2", "headerValue2");
        RequestParameter requestParameter = new RequestParameter()
                .put("param1", "value1")
                .put("param2", "value2&=");
        String postData = "param1=value1&param2=value2%26%3D";
        setResponseData("test".getBytes(CHARSET));

        LineApiResponse<String> responseData = target.post(
                Uri.parse("https://test"),
                requestHeaders,
                requestParameter.getMapData(),
                new StringResponseParser());

        verify(target).openHttpConnection(Uri.parse("https://test"));
        verify(httpsURLConnection).setRequestMethod("POST");
        verify(httpsURLConnection).setDoOutput(true);
        verify(httpsURLConnection).setRequestProperty("User-Agent", USER_AGENT);
        verify(httpsURLConnection).setRequestProperty("Accept-Encoding", "gzip");
        verify(httpsURLConnection).setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        verify(httpsURLConnection).setRequestProperty("Content-Length", String.valueOf(postData.getBytes(CHARSET).length));
        verify(httpsURLConnection).setRequestProperty("headerKey1", "headerValue1");
        verify(httpsURLConnection).setRequestProperty("headerKey2", "headerValue2");
        verify(httpsURLConnection).setConnectTimeout(CONNECT_TIMEOUT_MILLIS);
        verify(httpsURLConnection).setReadTimeout(READ_TIMEOUT_MILLIS);

        assertEquals(postData, connectionOutputStream.toString(CHARSET));

        assertTrue(responseData.isSuccess());
        assertEquals("test", responseData.getResponseData());
        assertEquals(LineApiError.DEFAULT, responseData.getErrorData());
    }

    @Test
    public void testPostWithJson() throws Exception {
        Map<String, String> requestHeaders = new HashMap();
        requestHeaders.put("headerKey1", "headerValue1");
        requestHeaders.put("headerKey2", "headerValue2");
        String postData = "{\"text\": \"hello\"}";
        setResponseData("test".getBytes(CHARSET));

        LineApiResponse<String> responseData = target.postWithJson(
                Uri.parse("https://test"),
                requestHeaders,
                postData,
                new StringResponseParser());

        verify(target).openHttpConnection(Uri.parse("https://test"));
        verify(httpsURLConnection).setRequestMethod("POST");
        verify(httpsURLConnection).setDoOutput(true);
        verify(httpsURLConnection).setRequestProperty("User-Agent", USER_AGENT);
        verify(httpsURLConnection).setRequestProperty("Accept-Encoding", "gzip");
        verify(httpsURLConnection).setRequestProperty("Content-Type", "application/json");
        verify(httpsURLConnection).setRequestProperty("Content-Length", String.valueOf(postData.getBytes().length));
        verify(httpsURLConnection).setRequestProperty("headerKey1", "headerValue1");
        verify(httpsURLConnection).setRequestProperty("headerKey2", "headerValue2");
        verify(httpsURLConnection).setConnectTimeout(CONNECT_TIMEOUT_MILLIS);
        verify(httpsURLConnection).setReadTimeout(READ_TIMEOUT_MILLIS);

        assertEquals(postData, connectionOutputStream.toString(CHARSET));

        assertTrue(responseData.isSuccess());
        assertEquals("test", responseData.getResponseData());
        assertEquals(LineApiError.DEFAULT, responseData.getErrorData());
    }

    @Test
    public void testGet() throws Exception {
        Map<String, String> requestHeaders = new HashMap();
        requestHeaders.put("headerKey1", "headerValue1");
        requestHeaders.put("headerKey2", "headerValue2");
        RequestParameter requestParameter = new RequestParameter()
                .put("param1", "value1")
                .put("param2", "value2&=");
        String query = "param1=value1&param2=value2%26%3D";
        setResponseData("test".getBytes(CHARSET));

        LineApiResponse<String> responseData = target.get(
                Uri.parse("https://test"),
                requestHeaders,
                requestParameter.getMapData(),
                new StringResponseParser());

        verify(target).openHttpConnection(Uri.parse("https://test?" + query));
        verify(httpsURLConnection).setRequestMethod("GET");
        verify(httpsURLConnection).setRequestProperty("User-Agent", USER_AGENT);
        verify(httpsURLConnection).setRequestProperty("Accept-Encoding", "gzip");
        verify(httpsURLConnection).setConnectTimeout(CONNECT_TIMEOUT_MILLIS);
        verify(httpsURLConnection).setReadTimeout(READ_TIMEOUT_MILLIS);

        assertTrue(responseData.isSuccess());
        assertEquals("test", responseData.getResponseData());
        assertEquals(LineApiError.DEFAULT, responseData.getErrorData());
    }

    @Test
    public void testDelete() throws Exception {
        Map<String, String> requestHeaders = new HashMap();
        requestHeaders.put("headerKey1", "headerValue1");
        requestHeaders.put("headerKey2", "headerValue2");
        setResponseData("test".getBytes(CHARSET));

        LineApiResponse<String> responseData = target.delete(
                Uri.parse("https://test"),
                requestHeaders,
                new StringResponseParser());

        verify(target).openHttpConnection(Uri.parse("https://test"));
        verify(httpsURLConnection).setRequestMethod("DELETE");
        verify(httpsURLConnection).setRequestProperty("User-Agent", USER_AGENT);
        verify(httpsURLConnection).setRequestProperty("Accept-Encoding", "gzip");
        verify(httpsURLConnection).setConnectTimeout(CONNECT_TIMEOUT_MILLIS);
        verify(httpsURLConnection).setReadTimeout(READ_TIMEOUT_MILLIS);

        assertTrue(responseData.isSuccess());
        assertEquals("test", responseData.getResponseData());
        assertEquals(LineApiError.DEFAULT, responseData.getErrorData());
    }

    @Test
    public void testNetworkErrorByGet() throws Exception {
        IOException ioException = new IOException();
        doThrow(ioException).when(httpsURLConnection).connect();

        LineApiResponse<String> responseData = target.get(
                Uri.parse("https://test"),
                Collections.<String, String>emptyMap() /* requestHeaders */,
                Collections.<String, String>emptyMap() /* queryParameters */,
                new StringResponseParser());

        assertFalse(responseData.isSuccess());
        assertEquals(LineApiResponseCode.NETWORK_ERROR, responseData.getResponseCode());
        assertEquals(new LineApiError(ioException), responseData.getErrorData());
    }

    @Test
    public void testNetworkErrorByDelete() throws Exception {
        IOException ioException = new IOException();
        doThrow(ioException).when(httpsURLConnection).connect();

        LineApiResponse<String> responseData = target.delete(
                Uri.parse("https://test"),
                Collections.<String, String>emptyMap() /* requestHeaders */,
                new StringResponseParser());

        assertFalse(responseData.isSuccess());
        assertEquals(LineApiResponseCode.NETWORK_ERROR, responseData.getResponseCode());
        assertEquals(new LineApiError(ioException), responseData.getErrorData());
    }

    @Test
    public void testNetworkErrorByPost() throws Exception {
        IOException ioException = new IOException();
        doThrow(ioException).when(httpsURLConnection).connect();

        LineApiResponse<String> responseData = target.post(
                Uri.parse("https://test"),
                Collections.<String, String>emptyMap() /* requestHeaders */,
                Collections.<String, String>emptyMap() /* postData */,
                new StringResponseParser());

        assertFalse(responseData.isSuccess());
        assertEquals(LineApiResponseCode.NETWORK_ERROR, responseData.getResponseCode());
        assertEquals(new LineApiError(ioException), responseData.getErrorData());
    }

    @Test
    public void testNetworkErrorByPostWithJson() throws Exception {
        IOException ioException = new IOException();
        doThrow(ioException).when(httpsURLConnection).connect();

        LineApiResponse<String> responseData = target.postWithJson(
                Uri.parse("https://test"),
                Collections.<String, String>emptyMap() /* requestHeaders */,
                "" /* postData */,
                new StringResponseParser());

        assertFalse(responseData.isSuccess());
        assertEquals(LineApiResponseCode.NETWORK_ERROR, responseData.getResponseCode());
        assertEquals(new LineApiError(ioException), responseData.getErrorData());
    }

    @Test
    public void testServerErrorByGet() throws Exception {
        setErrorData(400, "testErrorMessage".getBytes(CHARSET));

        LineApiResponse<String> responseData = target.get(
                Uri.parse("https://test"),
                Collections.<String, String>emptyMap() /* requestHeaders */,
                Collections.<String, String>emptyMap() /* queryParameters */,
                new StringResponseParser());

        assertFalse(responseData.isSuccess());
        assertEquals(LineApiResponseCode.SERVER_ERROR, responseData.getResponseCode());
        assertEquals(400, responseData.getErrorData().getHttpResponseCode());
        assertEquals("testErrorMessage", responseData.getErrorData().getMessage());
    }

    @Test
    public void testServerErrorByDelete() throws Exception {
        setErrorData(400, "testErrorMessage".getBytes(CHARSET));

        LineApiResponse<String> responseData = target.delete(
                Uri.parse("https://test"),
                Collections.<String, String>emptyMap() /* requestHeaders */,
                new StringResponseParser());

        assertFalse(responseData.isSuccess());
        assertEquals(LineApiResponseCode.SERVER_ERROR, responseData.getResponseCode());
        assertEquals(400, responseData.getErrorData().getHttpResponseCode());
        assertEquals("testErrorMessage", responseData.getErrorData().getMessage());
    }

    @Test
    public void testServerErrorByPost() throws Exception {
        setErrorData(400, "testErrorMessage".getBytes(CHARSET));

        LineApiResponse<String> responseData = target.post(
                Uri.parse("https://test"),
                Collections.<String, String>emptyMap() /* requestHeaders */,
                Collections.<String, String>emptyMap() /* postData */,
                new StringResponseParser());

        assertFalse(responseData.isSuccess());
        assertEquals(LineApiResponseCode.SERVER_ERROR, responseData.getResponseCode());
        assertEquals(400, responseData.getErrorData().getHttpResponseCode());
        assertEquals("testErrorMessage", responseData.getErrorData().getMessage());
    }

    @Test
    public void testServerErrorByPostWithJson() throws Exception {
        setErrorData(400, "testErrorMessage".getBytes(CHARSET));

        LineApiResponse<String> responseData = target.postWithJson(
                Uri.parse("https://test"),
                Collections.<String, String>emptyMap() /* requestHeaders */,
                ""  /* postData */,
                new StringResponseParser());

        assertFalse(responseData.isSuccess());
        assertEquals(LineApiResponseCode.SERVER_ERROR, responseData.getResponseCode());
        assertEquals(400, responseData.getErrorData().getHttpResponseCode());
        assertEquals("testErrorMessage", responseData.getErrorData().getMessage());
    }

    @Test
    public void testAccessTokenExpiredByGet() throws Exception {
        setErrorData(HttpURLConnection.HTTP_UNAUTHORIZED, "testErrorMessage".getBytes(CHARSET));

        LineApiResponse<String> responseData = target.get(
                Uri.parse("https://test"),
                Collections.<String, String>emptyMap() /* requestHeaders */,
                Collections.<String, String>emptyMap() /* queryParameters */,
                new StringResponseParser());

        assertFalse(responseData.isSuccess());
        assertEquals(LineApiResponseCode.SERVER_ERROR, responseData.getResponseCode());
        assertEquals(401, responseData.getErrorData().getHttpResponseCode());
        assertEquals("testErrorMessage", responseData.getErrorData().getMessage());
    }

    @Test
    public void testAccessTokenExpiredByDelete() throws Exception {
        setErrorData(HttpURLConnection.HTTP_UNAUTHORIZED, "testErrorMessage".getBytes(CHARSET));

        LineApiResponse<String> responseData = target.delete(
                Uri.parse("https://test"),
                Collections.<String, String>emptyMap() /* requestHeaders */,
                new StringResponseParser());

        assertFalse(responseData.isSuccess());
        assertEquals(LineApiResponseCode.SERVER_ERROR, responseData.getResponseCode());
        assertEquals(401, responseData.getErrorData().getHttpResponseCode());
        assertEquals("testErrorMessage", responseData.getErrorData().getMessage());
    }

    @Test
    public void testAccessTokenExpiredByPost() throws Exception {
        setErrorData(HttpURLConnection.HTTP_UNAUTHORIZED, "testErrorMessage".getBytes(CHARSET));

        LineApiResponse<String> responseData = target.post(
                Uri.parse("https://test"),
                Collections.<String, String>emptyMap() /* requestHeaders */,
                Collections.<String, String>emptyMap() /* postData */,
                new StringResponseParser());

        assertFalse(responseData.isSuccess());
        assertEquals(LineApiResponseCode.SERVER_ERROR, responseData.getResponseCode());
        assertEquals(401, responseData.getErrorData().getHttpResponseCode());
        assertEquals("testErrorMessage", responseData.getErrorData().getMessage());
    }

    @Test
    public void testAccessTokenExpiredByPostWithJson() throws Exception {
        setErrorData(HttpURLConnection.HTTP_UNAUTHORIZED, "testErrorMessage".getBytes(CHARSET));

        LineApiResponse<String> responseData = target.postWithJson(
                Uri.parse("https://test"),
                Collections.<String, String>emptyMap() /* requestHeaders */,
                ""  /* postData */,
                new StringResponseParser());

        assertFalse(responseData.isSuccess());
        assertEquals(LineApiResponseCode.SERVER_ERROR, responseData.getResponseCode());
        assertEquals(401, responseData.getErrorData().getHttpResponseCode());
        assertEquals("testErrorMessage", responseData.getErrorData().getMessage());
    }

    @Test
    public void testIllegalResponseDataByGet() throws Exception {
        setErrorData(400, "testErrorMessage".getBytes(CHARSET));

        LineApiResponse<String> responseData = target.get(
                Uri.parse("https://test"),
                Collections.<String, String>emptyMap() /* requestHeaders */,
                Collections.<String, String>emptyMap() /* queryParameters */,
                new StringResponseParser());

        assertFalse(responseData.isSuccess());
        assertEquals(LineApiResponseCode.SERVER_ERROR, responseData.getResponseCode());
        assertEquals(400, responseData.getErrorData().getHttpResponseCode());
        assertEquals("testErrorMessage", responseData.getErrorData().getMessage());
    }

    @Test
    public void testIllegalResponseDataByDelete() throws Exception {
        setErrorData(400, "testErrorMessage".getBytes(CHARSET));

        LineApiResponse<String> responseData = target.delete(
                Uri.parse("https://test"),
                Collections.<String, String>emptyMap() /* requestHeaders */,
                new StringResponseParser());

        assertFalse(responseData.isSuccess());
        assertEquals(LineApiResponseCode.SERVER_ERROR, responseData.getResponseCode());
        assertEquals(400, responseData.getErrorData().getHttpResponseCode());
        assertEquals("testErrorMessage", responseData.getErrorData().getMessage());
    }

    @Test
    public void testIllegalResponseDataByPost() throws Exception {
        setErrorData(400, "testErrorMessage".getBytes(CHARSET));

        LineApiResponse<String> responseData = target.post(
                Uri.parse("https://test"),
                Collections.<String, String>emptyMap() /* requestHeaders */,
                Collections.<String, String>emptyMap() /* postData */,
                new StringResponseParser());

        assertFalse(responseData.isSuccess());
        assertEquals(LineApiResponseCode.SERVER_ERROR, responseData.getResponseCode());
        assertEquals(400, responseData.getErrorData().getHttpResponseCode());
        assertEquals("testErrorMessage", responseData.getErrorData().getMessage());
    }

    @Test
    public void testIllegalResponseDataByPostWithJson() throws Exception {
        setErrorData(400, "testErrorMessage".getBytes(CHARSET));

        LineApiResponse<String> responseData = target.postWithJson(
                Uri.parse("https://test"),
                Collections.<String, String>emptyMap() /* requestHeaders */,
                "" /* postData */,
                new StringResponseParser());

        assertFalse(responseData.isSuccess());
        assertEquals(LineApiResponseCode.SERVER_ERROR, responseData.getResponseCode());
        assertEquals(400, responseData.getErrorData().getHttpResponseCode());
        assertEquals("testErrorMessage", responseData.getErrorData().getMessage());
    }

    private void setResponseData(@NonNull byte[] byteArray) throws IOException {
        InputStream inputStream = new ByteArrayInputStream(byteArray);
        doReturn(inputStream).when(httpsURLConnection).getInputStream();
        doReturn(HttpURLConnection.HTTP_OK).when(httpsURLConnection).getResponseCode();
    }

    private void setErrorData(int httpErrorCode, @NonNull byte[] byteArray) throws IOException {
        InputStream inputStream = new ByteArrayInputStream(byteArray);
        doReturn(inputStream).when(httpsURLConnection).getErrorStream();
        doReturn(httpErrorCode).when(httpsURLConnection).getResponseCode();
    }

    private static class RequestParameter {
        @NonNull
        private final Map<String, String> parameterMap;

        private RequestParameter() {
            parameterMap = new HashMap<>();
        }

        @NonNull
        private RequestParameter put(@NonNull String key, @NonNull String value) {
            parameterMap.put(key, value);
            return this;
        }

        @NonNull
        private Map<String, String> getMapData() {
            return parameterMap;
        }
    }
}
