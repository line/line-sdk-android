package com.linecorp.linesdk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.NoSuchElementException;

/**
 * Represents a response from the Social API.
 */
// You can create a success response through
// {@link #createAsSuccess(Object)} or an error response through
// {@link #createAsError(LineApiResponseCode, LineApiError)}. Besides, you can check if the
// response is successful through {@link #isSuccess()} and get the response date through
// {@link #getResponseData()}. If the response is not successful, you can check the error
// through {@link #isServerError()} or {@link #isNetworkError()}, and get the error data through
// {@link #getErrorData()}.
public class LineApiResponse<R> {
    private static final LineApiResponse<?> EMPTY_RESULT_SUCCESS =
            new LineApiResponse<>(LineApiResponseCode.SUCCESS, null, LineApiError.DEFAULT);

    @NonNull
    private final LineApiResponseCode responseCode;
    @Nullable
    private final R responseData;
    @NonNull
    private final LineApiError errorData;

    private LineApiResponse(
            @NonNull LineApiResponseCode responseCode,
            @Nullable R responseData,
            @NonNull LineApiError errorData) {
        this.responseCode = responseCode;
        this.responseData = responseData;
        this.errorData = errorData;
    }

    /**
     * @hide
     * Creates a success {@link LineApiResponse} with the given <i>responseData</i>. You can get the
     * {@link LineApiResponseCode#SUCCESS} response code through {@link #getResponseCode()},
     * the given <i>responseData</i> through {@link #getResponseData()}, and the
     * {@link LineApiError#DEFAULT} response code through {@link #getErrorData()}.
     *
     * @param responseData The response data to construct the {@link LineApiResponse} with.
     * @return The {@link LineApiResponse} with {@link LineApiResponseCode#SUCCESS} response code,
     * the given <i>responseData</i>, and the {@link LineApiError#DEFAULT} error data.
     */
    @SuppressWarnings("unchecked")
    @NonNull
    public static <T> LineApiResponse<T> createAsSuccess(@Nullable T responseData) {
        return responseData == null
                ? (LineApiResponse<T>) EMPTY_RESULT_SUCCESS
                : new LineApiResponse<>(LineApiResponseCode.SUCCESS, responseData, LineApiError.DEFAULT);
    }

    /**
     * @hide
     * Creates an error {@link LineApiResponse} with the given <i>responseCode</i> and
     * <i>errorData</i>. You can get the given <i>responseCode</i> through
     * {@link #getResponseCode()}, get the <code>null</code> response data through
     * {@link #getResponseData()}, and get the given <i>errorData</i> through {@link #getErrorData()}
     *
     * @param responseCode The response code to construct the {@link LineApiResponse} with.
     * @param errorData    The error data to construct the {@link LineApiResponse} with.
     * @return The {@link LineApiResponse} with the given <i>responseCode</i>, the <code>null</code>
     * response data, and the given <i>errorData</i>.
     * @throws AssertionError If the given <i>responseCode</i> is {@link LineApiResponseCode#SUCCESS}
     *                        and the current build is debug build.
     */
    @NonNull
    public static <T> LineApiResponse<T> createAsError(
            @NonNull LineApiResponseCode responseCode,
            @NonNull LineApiError errorData
    ) {
        if (BuildConfig.DEBUG && responseCode == LineApiResponseCode.SUCCESS) {
            throw new AssertionError();
        }
        return new LineApiResponse<>(responseCode, null, errorData);
    }

    /**
     * Checks if the API call is successful.
     *
     * @return True if the API call is successful; false otherwise.
     */
    public boolean isSuccess() {
        return responseCode == LineApiResponseCode.SUCCESS;
    }

    /**
     * Checks if the API call fails with a network error.
     *
     * @return True if the API call fails with a network error; false otherwise.
     */
    public boolean isNetworkError() {
        return responseCode == LineApiResponseCode.NETWORK_ERROR;
    }

    /**
     * Checks if the API call fails with a server error.
     *
     * @return True if the API call fails with a server error; false otherwise.
     */
    public boolean isServerError() {
        return responseCode == LineApiResponseCode.SERVER_ERROR;
    }

    /**
     * Gets the response code that is returned.
     *
     * @return The {@link LineApiResponseCode} object containing the HTTP status code which
     * indicates if the API call is successful.
     */
    @NonNull
    public LineApiResponseCode getResponseCode() {
        return responseCode;
    }

    /**
     * Gets data that is associated with the response if it exists. If no data is associated
     * with the response, it throws {@link NoSuchElementException}. You must check if the response
     * succeeded by using the {@link #isSuccess()} method before calling this method.
     *
     * @return The response data in the same format as the generic type associated with the
     * {@link LineApiResponse} class.
     * @throws NoSuchElementException if the response data is <code>null</code>.
     */
    @NonNull
    public R getResponseData() {
        if (responseData == null) {
            throw new NoSuchElementException(
                    "response data is null. Please check result by isSuccess before.");
        }
        return responseData;
    }

    /**
     * Gets information about an API error occurred. This method should only be called if
     * an API call has failed.
     *
     * @return The {@link LineApiError} object that contains information about the error. If no
     * error occurs, the {@link LineApiError} object will not contain any useful information.
     */
    @NonNull
    public LineApiError getErrorData() {
        return errorData;
    }

    /**
     * @hide
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LineApiResponse<?> that = (LineApiResponse<?>) o;

        if (responseCode != that.responseCode) return false;
        if (responseData != null ? !responseData.equals(that.responseData) : that.responseData != null)
            return false;
        return errorData.equals(that.errorData);
    }

    /**
     * @hide
     */
    @Override
    public int hashCode() {
        int result = responseCode.hashCode();
        result = 31 * result + (responseData != null ? responseData.hashCode() : 0);
        result = 31 * result + errorData.hashCode();
        return result;
    }

    /**
     * @hide
     */
    @Override
    public String toString() {
        return "LineApiResponse{" +
                "errorData=" + errorData +
                ", responseCode=" + responseCode +
                ", responseData=" + responseData +
                '}';
    }
}
