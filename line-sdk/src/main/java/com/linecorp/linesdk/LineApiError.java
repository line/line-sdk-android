package com.linecorp.linesdk;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Represents an error that is thrown by the Social API.
 */
public class LineApiError implements Parcelable {
    public static final Parcelable.Creator<LineApiError> CREATOR = new Parcelable.Creator<LineApiError>() {
        @Override
        public LineApiError createFromParcel(Parcel in) {
            return new LineApiError(in);
        }

        @Override
        public LineApiError[] newArray(int size) {
            return new LineApiError[size];
        }
    };

    private static final int DEFAULT_HTTP_RESPONSE_CODE = -1;
    public static final LineApiError DEFAULT = new LineApiError(
            DEFAULT_HTTP_RESPONSE_CODE,
            "" /* message */);

    private final int httpResponseCode;
    @Nullable
    private final String message;

    public LineApiError(@Nullable Exception e) {
        this(DEFAULT_HTTP_RESPONSE_CODE, toString(e));
    }

    public LineApiError(@Nullable String message) {
        this(DEFAULT_HTTP_RESPONSE_CODE, message);
    }

    public LineApiError(int httpResponseCode, @Nullable Exception e) {
        this(httpResponseCode, toString(e));
    }

    public LineApiError(int httpResponseCode, @Nullable String message) {
        this.httpResponseCode = httpResponseCode;
        this.message = message;
    }

    private LineApiError(@NonNull Parcel in) {
        httpResponseCode = in.readInt();
        message = in.readString();
    }

    /**
     * @hide
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(httpResponseCode);
        dest.writeString(message);
    }

    /**
     * @hide
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * @hide
     */
    @Nullable
    private static String toString(@Nullable Exception e) {
        if (e == null) {
            return null;
        }
        StringWriter stringWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }

    /**
     * Gets the response code associated with the API error.
     *
     * @return The HTTP response code.
     */
    public int getHttpResponseCode() {
        return httpResponseCode;
    }

    /**
     * Gets the error message associated with the API error.
     *
     * @return The error message associated with the API error.
     */
    @Nullable
    public String getMessage() {
        return message;
    }

    /**
     * @hide
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LineApiError that = (LineApiError) o;

        if (httpResponseCode != that.httpResponseCode) return false;
        return message != null ? message.equals(that.message) : that.message == null;
    }

    /**
     * @hide
     */
    @Override
    public int hashCode() {
        int result = httpResponseCode;
        result = 31 * result + (message != null ? message.hashCode() : 0);
        return result;
    }

    /**
     * @hide
     */
    @Override
    public String toString() {
        return "LineApiError{" +
                "httpResponseCode=" + httpResponseCode +
                ", message='" + message + '\'' +
                '}';
    }
}
