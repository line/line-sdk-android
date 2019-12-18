package com.linecorp.linesdk.internal.nwclient.core;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * {@link ResponseDataParser} to parse a response data to {@link String}.
 */
public class StringResponseParser implements ResponseDataParser<String> {
    private static final String DEFAULT_CHARSET_NAME = "UTF-8";

    @NonNull
    private final String charsetName;

    public StringResponseParser() {
        this(DEFAULT_CHARSET_NAME);
    }

    public StringResponseParser(@NonNull String charsetName) {
        this.charsetName = charsetName;
    }

    @Override
    @NonNull
    public String getResponseData(@NonNull InputStream inputStream) throws IOException {
        StringBuilder stringData = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(inputStream, charsetName));
            String line;
            while ((line = reader.readLine()) != null) {
                stringData.append(line);
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return stringData.toString();
    }
}
