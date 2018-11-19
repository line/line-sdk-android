package com.linecorp.linesdk;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

/**
 * @hide
 * This is a class to represent response from GetFriend API
 */
public class GetFriendsResponse {
    @NonNull
    private List<LineProfile> friends;

    @Nullable
    private String nextPageRequestToken;

    public GetFriendsResponse(@NonNull List<LineProfile> friends) {
        this.friends = friends;
    }

    public GetFriendsResponse(@NonNull List<LineProfile> friends, @Nullable String pageToken) {
        this.friends = friends;
        this.nextPageRequestToken = pageToken;
    }

    @NonNull
    public List<LineProfile> getFriends() {
        return friends;
    }

    @Nullable
    public String getNextPageRequestToken() {
        return nextPageRequestToken;
    }

    @Override
    public String toString() {
        return "GetFriendsResponse{" +
                       "friends=" + friends +
                       ", nextPageRequestToken='" + nextPageRequestToken + '\'' +
                       '}';
    }
}
