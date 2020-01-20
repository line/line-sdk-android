package com.linecorp.linesdk;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * {@link InputStream} of string data for test.
 * This class is not thread safe.
 */
public class TestStringInputStream extends InputStream {
    @NonNull
    private final byte[] byteData;
    private int currentIndex;

    public TestStringInputStream(@NonNull String stringData, @NonNull String charsetName) throws UnsupportedEncodingException {
        byteData = stringData.getBytes(charsetName);
        currentIndex = 0;
    }

    @Override
    public int read() throws IOException {
        if (byteData.length <= currentIndex) {
            return -1;
        }
        return byteData[currentIndex++];
    }
}
