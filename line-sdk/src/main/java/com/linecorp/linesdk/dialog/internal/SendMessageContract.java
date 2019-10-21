package com.linecorp.linesdk.dialog.internal;

import com.linecorp.linesdk.message.MessageData;

public interface SendMessageContract {
    interface Presenter {
        void removeTargetUser(TargetUser targetUser);
        void addTargetUser(TargetUser targetUser);
        void sendMessage(MessageData messageData);
        void release();

        int getTargetUserListSize();

        void getFriends(GetTargetUserTask.NextAction nextAction);
        void getGroups(GetTargetUserTask.NextAction nextAction);
    }

    interface View {
        void onTargetUserRemoved(TargetUser targetUser);
        void onTargetUserAdded(TargetUser targetUser);
        void onExceedMaxTargetUserCount(int count);
        void onSendMessageSuccess();
        void onSendMessageFailure();
    }
}
