package com.linecorp.linesdk.api.internal;

import androidx.annotation.NonNull;

import com.linecorp.linesdk.LineAccessToken;
import com.linecorp.linesdk.LineApiResponse;
import com.linecorp.linesdk.api.LineApiClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.HttpURLConnection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Factory to create {@link LineApiClient} with token auto refresh feature.
 * Token auto refreshing is available when the following conditions are satisfied.
 *  - The instance of {@link LineApiClient} is created by {@link #newProxy(LineApiClient)}.
 *  - The method is annotated with {@link TokenAutoRefresh}.
 *  - The method returns authentication error that is HTTP response code of 401(UNAUTHORIZED).
 */
public class AutoRefreshLineApiClientProxy {
    private AutoRefreshLineApiClientProxy() {
        // To prevent instantiation
    }

    @NonNull
    public static LineApiClient newProxy(@NonNull LineApiClient target) {
        return (LineApiClient) Proxy.newProxyInstance(
                target.getClass().getClassLoader(),
                new Class[]{LineApiClient.class},
                new TokenAutoRefreshInvocationHandler(target));
    }

    private static class TokenAutoRefreshInvocationHandler implements InvocationHandler {
        @NonNull
        private final LineApiClient target;
        @NonNull
        private final Map<Method, Boolean> autoRefreshStateCache;

        private TokenAutoRefreshInvocationHandler(@NonNull LineApiClient target) {
            this.target = target;
            autoRefreshStateCache = new ConcurrentHashMap<>(0);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Object result;
            try {
                result = method.invoke(target, args);
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            }

            if (!isAutoRefreshEnabled(method) || !shouldRefreshToken(result)) {
                return result;
            }

            LineApiResponse<LineAccessToken> refreshTokenResponse = target.refreshAccessToken();
            if (!refreshTokenResponse.isSuccess()) {
                return refreshTokenResponse.isNetworkError() ? refreshTokenResponse : result;
            }

            try {
                return method.invoke(target, args);
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
        }

        private static boolean shouldRefreshToken(@NonNull Object result) {
            if (!(result instanceof LineApiResponse)) {
                return false;
            }
            int httpResponseCode =
                    ((LineApiResponse<?>) result).getErrorData().getHttpResponseCode();
            return httpResponseCode == HttpURLConnection.HTTP_UNAUTHORIZED;
        }

        private boolean isAutoRefreshEnabled(@NonNull Method method) {
            Boolean isAutoRefreshEnabled = autoRefreshStateCache.get(method);
            if (isAutoRefreshEnabled != null) {
                return isAutoRefreshEnabled;
            }

            String methodName = method.getName();
            Class<?>[] parameterTypes = method.getParameterTypes();

            Class<?> currentClass = target.getClass();
            while (currentClass != null) {
                try {
                    TokenAutoRefresh tokenAutoRefresh = currentClass
                            .getDeclaredMethod(methodName, parameterTypes)
                            .getAnnotation(TokenAutoRefresh.class);
                    if (tokenAutoRefresh != null) {
                        autoRefreshStateCache.put(method, true);
                        return true;
                    }
                } catch (NoSuchMethodException e) {
                    // Just ignore NoSuchMethodException.
                    // The exception is thrown when the target (or a parent) does not overrides the
                    // method to refresh token.
                }
                currentClass = currentClass.getSuperclass();
            }
            autoRefreshStateCache.put(method, false);
            return false;
        }
    }
}
