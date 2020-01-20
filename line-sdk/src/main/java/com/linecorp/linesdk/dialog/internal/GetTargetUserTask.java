package com.linecorp.linesdk.dialog.internal;

import android.os.AsyncTask;
import androidx.annotation.NonNull;

import com.linecorp.linesdk.FriendSortField;
import com.linecorp.linesdk.GetFriendsResponse;
import com.linecorp.linesdk.GetGroupsResponse;
import com.linecorp.linesdk.LineApiResponse;
import com.linecorp.linesdk.LineFriendProfile;
import com.linecorp.linesdk.LineGroup;
import com.linecorp.linesdk.api.LineApiClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GetTargetUserTask extends AsyncTask<Void, List<TargetUser>, Void> {
    private TargetUser.Type type;
    private LineApiClient lineApiClient;
    private NextAction nextAction;

    public GetTargetUserTask(TargetUser.Type type, LineApiClient lineApiClient, NextAction action) {
        this.type = type;
        this.lineApiClient = lineApiClient;
        this.nextAction = action;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if (type == TargetUser.Type.FRIEND) {
            getAllFriends();
        } else if (type == TargetUser.Type.GROUP) {
            getAllGroups();
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(List<TargetUser>... values) {
        List<TargetUser> targetUserList = values[0];
        nextAction.run(targetUserList);
    }


    @FunctionalInterface
    public interface NextAction {
        void run(final List<TargetUser> targetUserList);
    }

    @NonNull
    private List<TargetUser> convertFriendsToTargetUsers(List<LineFriendProfile> friends) {
        List<TargetUser> targetUsers = new ArrayList<>();
        for (LineFriendProfile friend : friends) {
            targetUsers.add(TargetUser.createInstance(friend));
        }
        return targetUsers;
    }

    @NonNull
    private List<TargetUser> convertGroupsToTargetUsers(List<LineGroup> groups) {
        List<TargetUser> targetUsers = new ArrayList<>();
        for (LineGroup group : groups) {
            targetUsers.add(TargetUser.createInstance(group));
        }
        return targetUsers;
    }

    private void getAllFriends() {
        String nextPageToken = "";
        while (nextPageToken != null) {
            LineApiResponse<GetFriendsResponse> response =
                    lineApiClient.getFriends(FriendSortField.RELATION, nextPageToken, true);
            if (!response.isSuccess()) {
                publishProgress(Collections.emptyList());
                return;
            }

            GetFriendsResponse getFriendsResponse = response.getResponseData();
            publishProgress(convertFriendsToTargetUsers(getFriendsResponse.getFriends()));
            nextPageToken = getFriendsResponse.getNextPageRequestToken();
        }
    }

    private void getAllGroups() {
        String nextPageToken = "";
        while (nextPageToken != null) {
            LineApiResponse<GetGroupsResponse> response =
                    lineApiClient.getGroups(nextPageToken, true);
            if (!response.isSuccess()) {
                publishProgress(Collections.emptyList());
                return;
            }

            GetGroupsResponse getGroupsResponse = response.getResponseData();
            publishProgress(convertGroupsToTargetUsers(getGroupsResponse.getGroups()));
            nextPageToken = getGroupsResponse.getNextPageRequestToken();
        }
    }

}
