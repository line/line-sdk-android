package com.linecorp.linesdk.utils;

import android.net.Uri;
import android.net.Uri.Builder;
import androidx.annotation.NonNull;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Utility class to support Uri operations.
 */
public final class UriUtils {
    private UriUtils() { }

    public static Uri.Builder uriBuilder(@NonNull final String baseUri,
                                         @NonNull final String... newPathSegments) {
        return uriBuilder(Uri.parse(baseUri), newPathSegments);
    }

    public static Uri.Builder uriBuilder(@NonNull final Uri baseUri,
                                         @NonNull final String... newPathSegments) {
        final Builder builder = baseUri.buildUpon();

        for (final String path : newPathSegments) {
            builder.appendEncodedPath(path);
        }

        return builder;
    }

    public static Uri.Builder appendQueryParams(@NonNull final Uri.Builder uriBuilder,
                                                @NonNull final Map<String, String> queryParams) {
        for (final Map.Entry<String, String> param : queryParams.entrySet()) {
            uriBuilder.appendQueryParameter(param.getKey(), param.getValue());
        }

        return uriBuilder;
    }

    public static Uri buildUri(@NonNull final String baseUri,
                               @NonNull final String... newPathSegments) {
        return uriBuilder(baseUri, newPathSegments)
                .build();
    }

    public static Uri buildUri(@NonNull final Uri baseUri,
                               @NonNull final String... newPathSegments) {
        return uriBuilder(baseUri, newPathSegments)
                .build();
    }

    public static Uri appendQueryParams(@NonNull final String baseUri,
                                        @NonNull final Map<String, String> queryParams) {
        return appendQueryParams(Uri.parse(baseUri), queryParams);
    }

    public static Uri appendQueryParams(@NonNull final Uri baseUri,
                                        @NonNull final Map<String, String> queryParams) {
        return appendQueryParams(uriBuilder(baseUri), queryParams)
                .build();
    }

    public static Map<String, String> buildParams(final String... keyAndValue) {
        if (keyAndValue.length % 2 != 0) {
            throw new IllegalArgumentException("Odd number of key and Value");
        }

        final LinkedHashMap<String, String> map = new LinkedHashMap<>();
        for (int i = 0; i < keyAndValue.length; i += 2) {
            map.put(keyAndValue[i], keyAndValue[i + 1]);
        }

        return map;
    }
}
