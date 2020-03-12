package com.linecorp.linesdk.testconfig;

public class AndroidTestsConfig {
    // Timeout
    public static final long TIMEOUT = 30000L;

    // API success log
    public static final String SUCCESS_GET_PROFILE = "== getProfile == SUCCESS";

    // API error log
    public static final String ERROR_LOGOUT = "== logout == INTERNAL_ERROR";
    public static final String ERROR_REFRESH_TOKEN = "== refreshToken == INTERNAL_ERROR";
    public static final String ERROR_VERIFY_TOKEN = "== verifyToken == INTERNAL_ERROR";
    public static final String ERROR_GET_CURRENT_TOKEN = "== getCurrentToken == INTERNAL_ERROR";
    public static final String ERROR_GET_PROFILE = "== getProfile == INTERNAL_ERROR";
    public static final String ERROR_GET_FRIENDSHIP_STATUS = "== getFriendshipStatus == INTERNAL_ERROR";

    // LINE package name
    public static final String LINE_PACKAGE_NAME = "jp.naver.line.android";

    // LINE web url
    public static final String LINE_WEB_URL = "https://access.line.me";

    // Android widget
    public static final String ANDROID_WIDGET_BUTTON = "android.widget.Button";

    // Chrome
    public static final String CHROME_CLOSE_BUTTON = "com.android.chrome:id/close_button";
}
