package com.linecorp.linesdk.dialog.internal;

import androidx.annotation.Nullable;

import com.linecorp.linesdk.LineApiResponse;
import com.linecorp.linesdk.SendMessageResponse;
import com.linecorp.linesdk.api.LineApiClient;
import com.linecorp.linesdk.message.MessageData;

import java.util.ArrayList;
import java.util.List;

public class SendMessageTask extends android.os.AsyncTask<List<TargetUser>, Void,
        LineApiResponse<List<SendMessageResponse>>> {
    private LineApiClient lineApiClient;
    private List<MessageData> messages;
    @Nullable
    private ApiStatusListener apiStatusListener;

    SendMessageTask(LineApiClient lineApiClient, List<MessageData> messageDataList) {
        this(lineApiClient, messageDataList, null);
    }

    SendMessageTask(
            LineApiClient lineApiClient,
            List<MessageData> messages,
            @Nullable ApiStatusListener apiStatusListener
    ) {
        this.lineApiClient = lineApiClient;
        this.messages = messages;
        this.apiStatusListener = apiStatusListener;
    }

    @Override
    protected LineApiResponse<List<SendMessageResponse>> doInBackground(List<TargetUser>... targetUsers) {
        List<String> targetUserIds = new ArrayList<>();
        for (TargetUser targetUser : targetUsers[0]) {
            targetUserIds.add(targetUser.getId());
        }
        return lineApiClient.sendMessageToMultipleUsers(targetUserIds, messages, true);
    }


    @Override
    protected void onPostExecute(LineApiResponse<List<SendMessageResponse>> lineApiResponse) {
        if (apiStatusListener != null) {
            if (lineApiResponse.isSuccess()) {
                apiStatusListener.onSuccess();
            } else {
                apiStatusListener.onFailure();
            }
        }
    }
}
