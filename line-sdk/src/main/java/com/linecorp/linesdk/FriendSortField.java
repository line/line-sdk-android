package com.linecorp.linesdk;

import androidx.annotation.NonNull;

/**
 * @hide
 * Sorting options for the user's friends that are retrieved by the
 * {@link com.linecorp.linesdk.api.LineApiClient#getFriends(FriendSortField, String)} method and the
 * {@link com.linecorp.linesdk.api.LineApiClient#getFriendsApprovers(FriendSortField, String)}
 * method.
 */
public enum FriendSortField {

    /**
     * Sorts the user's friends by their display name. This is the default option.
     */
    NAME("name"),
    RELATION("relation");

    @NonNull
    private final String serverKey;

    FriendSortField(@NonNull String key) {
        this.serverKey = key;
    }

    @NonNull
    public String getServerKey() {
        return serverKey;
    }
}
