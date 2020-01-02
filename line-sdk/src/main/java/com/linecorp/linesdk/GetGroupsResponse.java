package com.linecorp.linesdk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

/**
 * @hide
 * This is a class to represent the response from GetGroups API
 */
public class GetGroupsResponse {
    @NonNull
    private List<LineGroup> groups;

    @Nullable
    private String nextPageRequestToken;

    public GetGroupsResponse(@NonNull List<LineGroup> groups) {
        this.groups = groups;
    }

    public GetGroupsResponse(@NonNull List<LineGroup> groups, @Nullable String pageToken) {
        this.groups = groups;
        this.nextPageRequestToken = pageToken;
    }

    /**
     * Gets a list of LineGroups.
     * @return
     */
    @NonNull
    public List<LineGroup> getGroups() {
        return groups;
    }

    /**
     * Gets a page request token, for requesting next page.
     * @return
     */
    @Nullable
    public String getNextPageRequestToken() {
        return nextPageRequestToken;
    }

    @Override
    public String toString() {
        return "GetFriendsResponse{" +
                       "groups=" + groups +
                       ", nextPageRequestToken='" + nextPageRequestToken + '\'' +
                       '}';
    }
}
