package com.linecorp.linesdktest.apiclient;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import android.util.Log;

import com.linecorp.linesdk.LineApiResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import static com.linecorp.linesdk.utils.UriUtils.appendQueryParams;

/**
 * A HTTP-based network client to access the Channel server for test.
 */
public class ChannelServiceHttpClientForTest {
    private static final String TAG = "HttpClientForTest";

    private static final String DEFAULT_PACKAGE_NAME = "UNK";
    private static final String DEFAULT_VERSION_NAME = "UNK";
    private static final String LINE_SDK_VERSION_FOR_TEST = "4.0";

    private static final byte[] EMPTY_DATA = new byte[0];
    private static final int DEFAULT_CONNECT_TIMEOUT_MILLIS = 90 * 1000;
    private static final int DEFAULT_READ_TIMEOUT_MILLIS = 90 * 1000;

    private static final String SERVER_SIDE_CHARSET = "UTF-8";

    @NonNull
    private final PackageInfo packageInfo;

    private int connectTimeoutMillis;
    private int readTimeoutMillis;

    @Nullable
    private String cachedUserAgent;

    public ChannelServiceHttpClientForTest(@NonNull Context context) {
        packageInfo = getPackageInfo(context);
        connectTimeoutMillis = DEFAULT_CONNECT_TIMEOUT_MILLIS;
        readTimeoutMillis = DEFAULT_READ_TIMEOUT_MILLIS;
    }

    @Nullable
    private static PackageInfo getPackageInfo(@NonNull Context context) {
        try {
            return context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            throw null;
        }
    }

    public void setConnectTimeoutMillis(int connectTimeoutMillis) {
        this.connectTimeoutMillis = connectTimeoutMillis;
    }

    public void setReadTimeoutMillis(int readTimeoutMillis) {
        this.readTimeoutMillis = readTimeoutMillis;
    }

    @WorkerThread
    @NonNull
    public String post(
            @NonNull Uri uri,
            @NonNull Map<String, String> requestHeaders,
            @NonNull Map<String, String> postData) throws IOException {
        byte[] postDataBytes = convertPostDataToBytes(postData);
        HttpURLConnection conn = null;
        try {
            conn = openPostConnection(uri, postDataBytes.length);
            setRequestHeaders(conn, requestHeaders);
            logRequestForDebug(conn, postDataBytes);
            conn.connect();

            OutputStream os = conn.getOutputStream();
            os.write(postDataBytes);
            os.flush();
            logResponseHeadersForDebug(conn);

            return getChannelServiceResponse(conn);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    @WorkerThread
    @NonNull
    public String get(
            @NonNull Uri uri,
            @NonNull Map<String, String> requestHeaders,
            @NonNull Map<String, String> queryParameters) throws IOException {
        final Uri fullUri = appendQueryParams(uri, queryParameters);
        HttpURLConnection conn = null;
        try {
            conn = openGetConnection(fullUri);
            setRequestHeaders(conn, requestHeaders);
            logRequestForDebug(conn, null /* requestBody */);
            conn.connect();
            logResponseHeadersForDebug(conn);
            return getChannelServiceResponse(conn);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    @WorkerThread
    @NonNull
    public String delete(
            @NonNull Uri uri,
            @NonNull Map<String, String> requestHeaders) throws IOException {
        HttpURLConnection conn = null;
        try {
            conn = openDeleteConnection(uri);
            setRequestHeaders(conn, requestHeaders);
            logRequestForDebug(conn, null /* requestBody */);
            conn.connect();
            logResponseHeadersForDebug(conn);
            return getChannelServiceResponse(conn);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    @NonNull
    private HttpURLConnection openPostConnection(
            @NonNull Uri uri, int postDataSizeByte) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(uri.toString()).openConnection();
        conn.setInstanceFollowRedirects(true);
        conn.setRequestProperty("Accept-Encoding", "gzip");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", String.valueOf(postDataSizeByte));
        conn.setConnectTimeout(connectTimeoutMillis);
        conn.setReadTimeout(readTimeoutMillis);
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        return conn;
    }

    @NonNull
    private HttpURLConnection openGetConnection(@NonNull Uri uri) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(uri.toString()).openConnection();
        conn.setInstanceFollowRedirects(true);
        conn.setRequestProperty("Accept-Encoding", "gzip");
        conn.setConnectTimeout(connectTimeoutMillis);
        conn.setReadTimeout(readTimeoutMillis);
        conn.setRequestMethod("GET");
        return conn;
    }

    @NonNull
    private HttpURLConnection openDeleteConnection(@NonNull Uri uri) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(uri.toString()).openConnection();
        conn.setInstanceFollowRedirects(true);
        conn.setRequestProperty("Accept-Encoding", "gzip");
        conn.setConnectTimeout(connectTimeoutMillis);
        conn.setReadTimeout(readTimeoutMillis);
        conn.setRequestMethod("DELETE");
        return conn;
    }

    @NonNull
    public String getUserAgent() {
        if (cachedUserAgent != null) {
            return cachedUserAgent;
        }

        String packageName = packageInfo == null ? DEFAULT_PACKAGE_NAME : packageInfo.packageName;
        String versionName = packageInfo == null ? DEFAULT_VERSION_NAME : packageInfo.versionName;
        Locale locale = Locale.getDefault();

        cachedUserAgent = packageName + "/" + versionName
                + " ChannelSDK/" + LINE_SDK_VERSION_FOR_TEST
                + " (Linux; U; Android " + Build.VERSION.RELEASE + "; "
                + locale.getLanguage() + "-" + locale.getCountry() + "; "
                + Build.MODEL
                + " Build/" + Build.ID + ")";
        return cachedUserAgent;
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
    private static String getChannelServiceResponse(
            @NonNull HttpURLConnection conn) throws IOException {
        InputStream inputStream = getInputStreamFrom(conn);
        StringBuilder stringData = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(inputStream, SERVER_SIDE_CHARSET));
            String line;
            while ((line = reader.readLine()) != null) {
                stringData.append(line);
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        String responseBody = stringData.toString();
        Log.d(TAG, responseBody);
        return responseBody;
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
        return inputStream;
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

    private static void logExceptionForDebug(
            @NonNull LineApiResponse<?> response, @NonNull Exception e) {
        Log.d(TAG, "response : " + response, e);
    }
}
