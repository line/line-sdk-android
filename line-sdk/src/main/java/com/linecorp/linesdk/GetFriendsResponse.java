package com.linecorp.linesdk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

/**
 * @hide
 * This is a class to represent response from GetFriend API
 */
public class GetFriendsResponse {
    @NonNull
    private List<LineFriendProfile> friends;

    @Nullable
    private String nextPageRequestToken;

    public GetFriendsResponse(@NonNull List<LineFriendProfile> friends) {
        this.friends = friends;
    }

    public GetFriendsResponse(@NonNull List<LineFriendProfile> friends, @Nullable String pageToken) {
        this.friends = friends;
        this.nextPageRequestToken = pageToken;
    }

    @NonNull
    public List<LineFriendProfile> getFriends() {
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
