package com.linecorp.linesdk.internal.nwclient;

import androidx.annotation.NonNull;

import com.linecorp.linesdk.internal.nwclient.core.ResponseDataParser;

import java.io.IOException;
import java.io.InputStream;

/**
 * {@link ResponseDataParser} for in case of no result.
 */
final class NoResultResponseParser implements ResponseDataParser<Object> {
    private static final Object NO_RESULT = new Object();

    @NonNull
    @Override
    public Object getResponseData(@NonNull InputStream inputStream) throws IOException {
        return NO_RESULT;
    }
}
