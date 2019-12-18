package com.linecorp.linesdk.internal.nwclient.core;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;

/**
 * interface to parse response data.
 */
public interface ResponseDataParser<T> {
    @NonNull
    T getResponseData(@NonNull InputStream inputStream) throws IOException;
}
