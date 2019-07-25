package com.linecorp.linesdktest.apiclient;

import android.content.Context;
import android.net.Uri;
import androidx.annotation.NonNull;

import com.linecorp.linesdk.utils.UriUtils;
import com.linecorp.linesdktest.BuildConfig;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import static com.linecorp.linesdk.utils.UriUtils.buildParams;

/**
 * Internal LINE OAUTH API client to process internal process such as building request data and
 * parsing response data.
 */
public class LineOauthApiClientForTest {
    private static final Uri API_SERVER_BASE_URL = Uri.parse(BuildConfig.API_SERVER_BASE_URI);

    private static final String BASE_PATH_OAUTH_V21_API = "oauth2/v2.1";

    @NonNull
    private final ChannelServiceHttpClientForTest httpClient;

    public LineOauthApiClientForTest(@NonNull final Context applicationContext) {
        httpClient = new ChannelServiceHttpClientForTest(applicationContext);
    }

    @NonNull
    public String revokeAccessToken(@NonNull final String channelId,
                                    @NonNull final String accessToken) throws IOException {
        final Uri uri = UriUtils.buildUri(API_SERVER_BASE_URL, BASE_PATH_OAUTH_V21_API, "revoke");
        final Map<String, String> postData =
                buildParams("access_token", accessToken,
                            "client_id", channelId);
        return httpClient.post(
                uri,
                Collections.emptyMap() /* requestHeaders */,
                postData);
    }
}
