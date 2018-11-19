package com.linecorp.linesdk;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;

/**
 * Represents a scope. A scope is a permission that the user grants your app during the login
 * process.
 */
public class Scope {
    /**
     * Key: Scope code <br></br>
     * Value: Scope instance
     */
    private static final Map<String, Scope> scopeInstanceMap = new HashMap<>();

    /**
     * Permission to get the user's profile information.
     */
    public static final Scope PROFILE = new Scope("profile");

    /**
     * @hide
     * LINE internal use only. Permission to get the user's friends.
     */
    public static final Scope FRIEND = new Scope("friends");

    /**
     * @hide
     * LINE internal use only. Permission to get groups that the user is a member of.
     */
    public static final Scope GROUP = new Scope("groups");

    /**
     * @hide
     * LINE internal use only. Permission to send messages on behalf of the user.
     */
    public static final Scope MESSAGE = new Scope("message.write");

    /**
     * Permission to get an ID token that includes the user information.
     */
    public static final Scope OPENID_CONNECT = new Scope("openid");

    /**
     * Permission to get the user's email address.
     */
    public static final Scope OC_EMAIL = new Scope("email");

    /**
     * @hide
     * LINE internal use only. Permission to get the user's phone number.
     */
    public static final Scope OC_PHONE_NUMBER = new Scope("phone");

    /**
     * @hide
     * LINE internal use only. Permission to get the user's gender.
     */
    public static final Scope OC_GENDER = new Scope("gender");

    /**
     * @hide
     * LINE internal use only. Permission to get the user's birthdate.
     */
    public static final Scope OC_BIRTHDATE = new Scope("birthdate");

    /**
     * @hide
     * LINE internal use only. Permission to get the user's address.
     */
    public static final Scope OC_ADDRESS = new Scope("address");

    /**
     * @hide
     * LINE internal use only. Permission to get the user's real name.
     */
    public static final Scope OC_REAL_NAME = new Scope("real_name");

    private static final String SCOPE_DELIMITER = " ";

    @NonNull
    private final String code;

    /**
     * @hide
     */
    protected Scope(@NonNull final String code) {
        if (scopeInstanceMap.containsKey(code)) {
            throw new IllegalArgumentException("Scope code already exists: " + code);
        }

        this.code = code;
        scopeInstanceMap.put(code, this);
    }

    /**
     * @hide
     */
    @Nullable
    public static Scope findScope(final String scopeCode) {
        return scopeInstanceMap.get(scopeCode);
    }

    /**
     * @hide
     */
    public static String join(@Nullable final List<Scope> scopes) {
        if (scopes == null || scopes.isEmpty()) {
            return null;
        }

        final List<String> scopeCodeList = convertToCodeList(scopes);
        return TextUtils.join(SCOPE_DELIMITER, scopeCodeList);
    }

    /**
     * @hide
     * Parses a space-separated scope string into a Scope instance list.
     * @param scopeCodeStr A space separated string, that lists several scopes.
     * @return The Scope instance list.
     */
    public static List<Scope> parseToList(@Nullable final String scopeCodeStr) {
        if (TextUtils.isEmpty(scopeCodeStr)) {
            return emptyList();
        }

        final List<String> scopeCodeList = Arrays.asList(scopeCodeStr.split(SCOPE_DELIMITER));
        return convertToScopeList(scopeCodeList);
    }

    /**
     * @hide
     */
    public static List<Scope> convertToScopeList(final List<String> scopeCodeList) {
        final List<Scope> scopeList = new ArrayList<>();
        for (final String scopeCode : scopeCodeList) {
            final Scope scope = findScope(scopeCode);
            if (scope != null) {
                scopeList.add(scope);
            }
        }

        return scopeList;
    }

    /**
     * @hide
     */
    public static List<String> convertToCodeList(final List<Scope> scopeList) {
        final List<String> scopeCodeList = new ArrayList<>();
        for (final Scope scope : scopeList) {
            scopeCodeList.add(scope.code);
        }

        return scopeCodeList;
    }

    /**
     * @hide
     */
    @NonNull
    public String getCode() {
        return code;
    }

    /**
     * @hide
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        final Scope scope = (Scope) o;

        return code.equals(scope.code);
    }

    /**
     * @hide
     */
    @Override
    public int hashCode() {
        return code.hashCode();
    }

    /**
     * @hide
     */
    @Override
    public String toString() {
        return "Scope{" +
               "code='" + code + '\'' +
               '}';
    }
}
