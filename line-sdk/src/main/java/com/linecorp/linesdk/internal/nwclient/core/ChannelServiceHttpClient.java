package com.linecorp.linesdk.internal.nwclient.core;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.linecorp.android.security.TLSSocketFactory;
import com.linecorp.linesdk.BuildConfig;
import com.linecorp.linesdk.LineApiError;
import com.linecorp.linesdk.LineApiResponse;
import com.linecorp.linesdk.LineApiResponseCode;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HttpsURLConnection;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.annotation.WorkerThread;

import static com.linecorp.linesdk.utils.UriUtils.appendQueryParams;

/**
 * A HTTP-based network client to access the Channel server.
 */
public class ChannelServiceHttpClient {
    private static final String TAG = "ChannelHttpClient";

    private static final byte[] EMPTY_DATA = new byte[0];
    private static final int DEFAULT_CONNECT_TIMEOUT_MILLIS = 90 * 1000;
    private static final int DEFAULT_READ_TIMEOUT_MILLIS = 90 * 1000;

    private static final String SERVER_SIDE_CHARSET = "UTF-8";

    @NonNull
    private final UserAgentGenerator userAgentGenerator;
    @NonNull
    private final StringResponseParser errorResponseParser;

    private int connectTimeoutMillis;
    private int readTimeoutMillis;

    public ChannelServiceHttpClient(@NonNull Context context, @NonNull String lineSdkVersion) {
        this(new UserAgentGenerator(context, lineSdkVersion));
    }

    @VisibleForTesting
    protected ChannelServiceHttpClient(@NonNull UserAgentGenerator userAgentGenerator) {
        this.userAgentGenerator = userAgentGenerator;
        errorResponseParser = new StringResponseParser(SERVER_SIDE_CHARSET);
        connectTimeoutMillis = DEFAULT_CONNECT_TIMEOUT_MILLIS;
        readTimeoutMillis = DEFAULT_READ_TIMEOUT_MILLIS;
    }

    public void setConnectTimeoutMillis(int connectTimeoutMillis) {
        this.connectTimeoutMillis = connectTimeoutMillis;
    }

    public void setReadTimeoutMillis(int readTimeoutMillis) {
        this.readTimeoutMillis = readTimeoutMillis;
    }

    private enum HttpMethod {
        POST, GET, DELETE, PUT
    }

    @WorkerThread
    @NonNull
    public <T> LineApiResponse<T> post(
            @NonNull Uri uri,
            @NonNull Map<String, String> requestHeaders,
            @NonNull Map<String, String> postData,
            @NonNull ResponseDataParser<T> responseDataParser) {
        byte[] postDataBytes = convertPostDataToBytes(postData);
        HttpURLConnection conn = null;
        try {
            conn = openPostConnection(uri, postDataBytes.length);
            setRequestHeaders(conn, requestHeaders);
            if (BuildConfig.DEBUG) {
                logRequestForDebug(conn, postDataBytes);
            }
            conn.connect();

            OutputStream os = conn.getOutputStream();
            os.write(postDataBytes);
            os.flush();
            if (BuildConfig.DEBUG) {
                logResponseHeadersForDebug(conn);
            }

            return getChannelServiceResponse(conn, responseDataParser, errorResponseParser);
        } catch (IOException e) {
            LineApiResponse<T> response = LineApiResponse.createAsError(
                    LineApiResponseCode.NETWORK_ERROR, new LineApiError(e));
            logExceptionForDebug(response, e);
            return response;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    @WorkerThread
    @NonNull
    public <T> LineApiResponse<T> postWithJson(
            @NonNull Uri uri,
            @NonNull Map<String, String> requestHeaders,
            @NonNull String postData,
            @NonNull ResponseDataParser<T> responseDataParser) {
        return sendRequestWithJson(HttpMethod.POST, uri, requestHeaders, postData, responseDataParser);
    }

    @WorkerThread
    @NonNull
    public <T> LineApiResponse<T> putWithJson(
            @NonNull Uri uri,
            @NonNull Map<String, String> requestHeaders,
            @NonNull String postData,
            @Nullable ResponseDataParser<T> responseDataParser) {
        return sendRequestWithJson(HttpMethod.PUT, uri, requestHeaders, postData, responseDataParser);
    }

    @WorkerThread
    @NonNull
    private <T> LineApiResponse<T> sendRequestWithJson(
            @NonNull HttpMethod method,
            @NonNull Uri uri,
            @NonNull Map<String, String> requestHeaders,
            @NonNull String postData,
            @Nullable ResponseDataParser<T> responseDataParser) {
        byte[] postDataBytes = postData.getBytes();
        HttpURLConnection conn = null;
        try {
            conn = openConnectionWithJson(uri, postDataBytes.length, method);
            setRequestHeaders(conn, requestHeaders);
            if (BuildConfig.DEBUG) {
                logRequestForDebug(conn, postDataBytes);
            }
            conn.connect();

            OutputStream os = conn.getOutputStream();
            os.write(postDataBytes);
            os.flush();
            if (BuildConfig.DEBUG) {
                logResponseHeadersForDebug(conn);
            }

            return getChannelServiceResponse(conn, responseDataParser, errorResponseParser);
        } catch (IOException e) {
            LineApiResponse<T> response = LineApiResponse.createAsError(
                    LineApiResponseCode.NETWORK_ERROR, new LineApiError(e));
            logExceptionForDebug(response, e);
            return response;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    @WorkerThread
    @NonNull
    public <T> LineApiResponse<T> get(
            @NonNull Uri uri,
            @NonNull Map<String, String> requestHeaders,
            @NonNull Map<String, String> queryParameters,
            @Nullable ResponseDataParser<T> responseDataParser) {
        final Uri fullUri = appendQueryParams(uri, queryParameters);

        HttpURLConnection conn = null;
        try {
            conn = openGetConnection(fullUri);
            setRequestHeaders(conn, requestHeaders);
            if (BuildConfig.DEBUG) {
                logRequestForDebug(conn, null /* requestBody */);
            }
            conn.connect();
            if (BuildConfig.DEBUG) {
                logResponseHeadersForDebug(conn);
            }
            return getChannelServiceResponse(conn, responseDataParser, errorResponseParser);
        } catch (IOException e) {
            LineApiResponse<T> response = LineApiResponse.createAsError(
                    LineApiResponseCode.NETWORK_ERROR, new LineApiError(e));
            logExceptionForDebug(response, e);
            return response;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    @WorkerThread
    @NonNull
    public <T> LineApiResponse<T> delete(
            @NonNull Uri uri,
            @NonNull Map<String, String> requestHeaders,
            @Nullable ResponseDataParser<T> responseDataParser) {
        HttpURLConnection conn = null;
        try {
            conn = openDeleteConnection(uri);
            setRequestHeaders(conn, requestHeaders);
            if (BuildConfig.DEBUG) {
                logRequestForDebug(conn, null /* requestBody */);
            }
            conn.connect();
            if (BuildConfig.DEBUG) {
                logResponseHeadersForDebug(conn);
            }
            return getChannelServiceResponse(conn, responseDataParser, errorResponseParser);
        } catch (IOException e) {
            LineApiResponse<T> response = LineApiResponse.createAsError(
                    LineApiResponseCode.NETWORK_ERROR, new LineApiError(e));
            logExceptionForDebug(response, e);
            return response;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    @NonNull
    private HttpURLConnection openPostConnection(
            @NonNull Uri uri, int postDataSizeByte) throws IOException {
        HttpURLConnection conn = openHttpConnection(uri);
        conn.setInstanceFollowRedirects(true);
        conn.setRequestProperty("User-Agent", userAgentGenerator.getUserAgent());
        conn.setRequestProperty("Accept-Encoding", "gzip");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", String.valueOf(postDataSizeByte));
        conn.setConnectTimeout(connectTimeoutMillis);
        conn.setReadTimeout(readTimeoutMillis);
        conn.setRequestMethod(HttpMethod.POST.name());
        conn.setDoOutput(true);
        return conn;
    }

    @NonNull
    private HttpURLConnection openConnectionWithJson(
            @NonNull Uri uri, int postDataSizeByte, HttpMethod method) throws IOException {
        HttpURLConnection conn = openHttpConnection(uri);
        conn.setInstanceFollowRedirects(true);
        conn.setRequestProperty("User-Agent", userAgentGenerator.getUserAgent());
        conn.setRequestProperty("Accept-Encoding", "gzip");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Content-Length", String.valueOf(postDataSizeByte));
        conn.setConnectTimeout(connectTimeoutMillis);
        conn.setReadTimeout(readTimeoutMillis);
        conn.setRequestMethod(method.name());
        conn.setDoOutput(true);
        return conn;
    }

    @NonNull
    private HttpURLConnection openGetConnection(@NonNull Uri uri) throws IOException {
        HttpURLConnection conn = openHttpConnection(uri);
        conn.setInstanceFollowRedirects(true);
        conn.setRequestProperty("User-Agent", userAgentGenerator.getUserAgent());
        conn.setRequestProperty("Accept-Encoding", "gzip");
        conn.setConnectTimeout(connectTimeoutMillis);
        conn.setReadTimeout(readTimeoutMillis);
        conn.setRequestMethod(HttpMethod.GET.name());
        return conn;
    }

    @NonNull
    private HttpURLConnection openDeleteConnection(@NonNull Uri uri) throws IOException {
        HttpURLConnection conn = openHttpConnection(uri);
        conn.setInstanceFollowRedirects(true);
        conn.setRequestProperty("User-Agent", userAgentGenerator.getUserAgent());
        conn.setRequestProperty("Accept-Encoding", "gzip");
        conn.setConnectTimeout(connectTimeoutMillis);
        conn.setReadTimeout(readTimeoutMillis);
        conn.setRequestMethod(HttpMethod.DELETE.name());
        return conn;
    }

    @VisibleForTesting
    @NonNull
    protected HttpURLConnection openHttpConnection(@NonNull Uri uri) throws IOException {
        URLConnection urlConnection = new URL(uri.toString()).openConnection();
        if (!(urlConnection instanceof HttpsURLConnection)) {
            if (BuildConfig.DEBUG) {
                return (HttpURLConnection) urlConnection;
            }
            throw new IllegalArgumentException("The scheme of the server url must be https." + uri);
        }

        // Android 6.0 doesn't enable SSLv3 by default, but it does enable some weak ciphers like
        // TLS_ECDHE_RSA_WITH_RC4_128_SHA. The custom socket factory (TLSSocketFactory) also disables
        // weak ciphers, so apply this. Android 7.0 uses safe defaults so doesn't apply this.
        if (Build.VERSION.SDK_INT >= 24 /* Build.VERSION_CODES.N */) {
            return (HttpURLConnection) urlConnection;
        }
        HttpsURLConnection httpsURLConnection = (HttpsURLConnection) urlConnection;
        httpsURLConnection.setSSLSocketFactory(
                new TLSSocketFactory(httpsURLConnection.getSSLSocketFactory()));
        return httpsURLConnection;
    }

    private static void setRequestHeaders(
            @NonNull HttpURLConnection conn, @NonNull Map<String, String> requestHeaders) {
        for (Map.Entry<String, String> headerEntry : requestHeaders.entrySet()) {
            conn.setRequestProperty(headerEntry.getKey(), headerEntry.getValue());
        }
    }

    @NonNull
    private static byte[] convertPostDataToBytes(@NonNull Map<String, String> postData) {
        if (postData.isEmpty()) {
            return EMPTY_DATA;
        }
        Uri uri = appendQueryParams("", postData);
        try {
            return uri.getEncodedQuery().getBytes(SERVER_SIDE_CHARSET);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @NonNull
    private static <T> LineApiResponse<T> getChannelServiceResponse(
            @NonNull HttpURLConnection conn,
            @Nullable ResponseDataParser<T> responseDataParser,
            @NonNull ResponseDataParser<String> errorResponseParser) throws IOException {
        InputStream inputStream = getInputStreamFrom(conn);
        int httpResponseCode = conn.getResponseCode();
        try {
            if (httpResponseCode != HttpURLConnection.HTTP_OK
                    && httpResponseCode != HttpURLConnection.HTTP_NO_CONTENT) {
                return LineApiResponse.createAsError(
                        LineApiResponseCode.SERVER_ERROR,
                        LineApiError.createWithHttpResponseCode(
                                httpResponseCode,
                                errorResponseParser.getResponseData(inputStream)));
            }

            if (responseDataParser == null) {
                return LineApiResponse.createAsSuccess(null);
            }

            return LineApiResponse.createAsSuccess(responseDataParser.getResponseData(inputStream));
        } catch (IOException e) {
            // Evaluates response data parsing error as INTERNAL_ERROR
            return LineApiResponse.createAsError(
                    LineApiResponseCode.INTERNAL_ERROR,
                    new LineApiError(e, LineApiError.ErrorCode.HTTP_RESPONSE_PARSE_ERROR)
            );
        }
    }

    @NonNull
    private static InputStream getInputStreamFrom(
            @NonNull HttpURLConnection httpURLConnection) throws IOException {
        int httpResponseCode = httpURLConnection.getResponseCode();
        InputStream inputStream = httpResponseCode < 400
                ? httpURLConnection.getInputStream()
                : httpURLConnection.getErrorStream();
        inputStream = isGzipUsed(httpURLConnection)
                ? new GZIPInputStream(inputStream)
                : inputStream;
        return BuildConfig.DEBUG ? logResponseBodyForDebug(inputStream) : inputStream;
    }

    private static boolean isGzipUsed(@NonNull HttpURLConnection conn) {
        List<String> contentEncodings = conn.getHeaderFields().get("Content-Encoding");
        if (contentEncodings == null || contentEncodings.isEmpty()) {
            return false;
        }
        for (int i = 0; i < contentEncodings.size(); ++i) {
            String contentEncoding = contentEncodings.get(i);
            if (contentEncoding.equalsIgnoreCase("gzip")) {
                return true;
            }
        }
        return false;
    }

    private static void logRequestForDebug(@NonNull HttpURLConnection conn, @Nullable byte[] requestBody) {
        Log.d(TAG, conn.getRequestMethod() + " : " + conn.getURL());
        Map<String, List<String>> properties = conn.getRequestProperties();
        for (Map.Entry<String, List<String>> property : properties.entrySet()) {
            Log.d(TAG, "    "
                    + property.getKey() + " : " + Arrays.toString(property.getValue().toArray()));
        }
        if (requestBody != null) {
            try {
                Log.d(TAG, "== Request body ==");
                Log.d(TAG, new String(requestBody, "utf-8"));
            } catch (UnsupportedEncodingException e) {
                // ignore
            }
        }
    }

    private static void logResponseHeadersForDebug(@NonNull HttpURLConnection conn) throws IOException {
        Log.d(TAG, conn.getResponseCode() + " : " + conn.getResponseMessage());
        Map<String, List<String>> responseHeaders = conn.getHeaderFields();
        for (Map.Entry<String, List<String>> property : responseHeaders.entrySet()) {
            Log.d(TAG, "    "
                    + property.getKey() + " : " + Arrays.toString(property.getValue().toArray()));
        }
    }

    @NonNull
    private static InputStream logResponseBodyForDebug(
            @NonNull InputStream inputStream) throws IOException {
        byte[] responseBodyByteArray = toByteArray(inputStream);
        Log.d(TAG, "== response body ==");
        Log.d(TAG, new StringResponseParser().getResponseData(
                new ByteArrayInputStream(responseBodyByteArray)));
        return new ByteArrayInputStream(responseBodyByteArray);
    }

    @NonNull
    private static byte[] toByteArray(@NonNull InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        while (true) {
            int length = inputStream.read(buffer);
            if (length < 0) {
                break;
            }
            byteArrayOutputStream.write(buffer, 0, length);
        }
        byteArrayOutputStream.flush();
        return byteArrayOutputStream.toByteArray();
    }

    private static void logExceptionForDebug(
            @NonNull LineApiResponse<?> response, @NonNull Exception e) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "response : " + response, e);
        }
    }
}
