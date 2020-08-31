package com.linecorp.linesdk;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.PrintWriter;
import java.io.StringWriter;

import androidx.annotation.Nullable;

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

    public enum ErrorCode {
        LOGIN_ACTIVITY_NOT_FOUND,
        HTTP_RESPONSE_PARSE_ERROR,
        NOT_DEFINED,
    }

    private static final int DEFAULT_HTTP_RESPONSE_CODE = -1;
    public static final LineApiError DEFAULT = new LineApiError(
            DEFAULT_HTTP_RESPONSE_CODE,
            "" /* message */,
            ErrorCode.NOT_DEFINED);

    private final int httpResponseCode;
    @Nullable
    private final String message;

    private final ErrorCode errorCode;

    public LineApiError(@Nullable Exception e) {
        this(DEFAULT_HTTP_RESPONSE_CODE, toString(e), ErrorCode.NOT_DEFINED);
    }

    public LineApiError(@Nullable String message) {
        this(DEFAULT_HTTP_RESPONSE_CODE, message, ErrorCode.NOT_DEFINED);
    }

    public static LineApiError createWithHttpResponseCode(int httpResponseCode, @Nullable String errorString) {
        return new LineApiError(httpResponseCode, errorString, ErrorCode.NOT_DEFINED);
    }

    public static LineApiError createWithHttpResponseCode(int httpResponseCode, @Nullable Exception e) {
        return LineApiError.createWithHttpResponseCode(httpResponseCode, toString(e));
    }

    public LineApiError(@Nullable Exception e, ErrorCode errorCode) {
        this(DEFAULT_HTTP_RESPONSE_CODE, toString(e), errorCode);
    }

    public LineApiError(int httpResponseCode, @Nullable String message, ErrorCode errorCode) {
        this.httpResponseCode = httpResponseCode;
        this.message = message;
        this.errorCode = errorCode;
    }

    /**
     * @hide
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(httpResponseCode);
        dest.writeString(message);
        dest.writeInt(errorCode == null ? -1 : errorCode.ordinal());
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
    protected LineApiError(Parcel in) {
        this.httpResponseCode = in.readInt();
        this.message = in.readString();
        int tmpErrorCode = in.readInt();
        this.errorCode = tmpErrorCode == -1 ? null : ErrorCode.values()[tmpErrorCode];
    }

    /**
     * @hide
     */
    @Override
    public String toString() {
        return "LineApiError{" +
                "httpResponseCode=" + httpResponseCode +
                ", message='" + message + '\'' +
                ", errorCode='" + errorCode + '\'' +
                '}';
    }
}
