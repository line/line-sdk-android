package com.linecorp.linesdk.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.google.android.material.tabs.TabLayout;
import com.linecorp.linesdk.R;
import com.linecorp.linesdk.api.LineApiClient;
import com.linecorp.linesdk.dialog.internal.SendMessageContract;
import com.linecorp.linesdk.dialog.internal.SendMessagePresenter;
import com.linecorp.linesdk.dialog.internal.SendMessageTargetPagerAdapter;
import com.linecorp.linesdk.dialog.internal.TargetUser;
import com.linecorp.linesdk.dialog.internal.UserThumbnailView;
import com.linecorp.linesdk.message.MessageData;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialog;
import androidx.viewpager.widget.ViewPager;

/**
 * @hide
 * A subclass of Dialog that shows the friend and group list for users to pick and send the
 * passed-in messageData. You can register a listener through
 * {@link #setOnSendListener(OnSendListener)} know whether the messageData is sent or not.
 * <pre>
 * MessageData messageData = new TextMessage("Default Text Message", new MessageSender(...));
 *
 * SendMessageDialog sendMessageDialog = new SendMessageDialog(context, lineApiClient);
 * sendMessageDialog.setMessageData(messageData);
 * sendMessageDialog.setOnSendListener(dialog -> {...});
 * sendMessageDialog.setOnCancelListener(dialog -> {...});
 * sendMessageDialog.show();
 * </pre>
 */
public class SendMessageDialog extends AppCompatDialog implements SendMessageContract.View {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Button buttonConfirm;
    private LinearLayout linearLayoutTargetUser;
    private HorizontalScrollView horizontalScrollView;

    private MessageData messageData;
    private SendMessageTargetPagerAdapter sendMessageTargetAdapter;
    private Map<String, View> targetUserViewCacheMap = new HashMap<>();

    private OnSendListener onSendListener;

    private LinearLayout.LayoutParams layoutParams =
            new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);

    private SendMessagePresenter presenter;

    public void setMessageData(MessageData messageData) {
        this.messageData = messageData;
    }

    public SendMessageDialog(@NonNull Context context, @NonNull LineApiClient lineApiClient) {
        super(context, R.style.DialogTheme);
        presenter = new SendMessagePresenter(lineApiClient, this);
        sendMessageTargetAdapter = new SendMessageTargetPagerAdapter(context, presenter, presenter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View content = LayoutInflater.from(getContext()).inflate(R.layout.dialog_send_message, null);
        setContentView(content);

        viewPager = content.findViewById(R.id.viewPager);
        tabLayout = content.findViewById(R.id.tabLayout);
        buttonConfirm = content.findViewById(R.id.buttonConfirm);
        linearLayoutTargetUser = content.findViewById(R.id.linearLayoutTargetUserList);
        horizontalScrollView = content.findViewById(R.id.horizontalScrollView);

        setupUi();
    }

    private void setupUi() {
        viewPager.setAdapter(sendMessageTargetAdapter);
        tabLayout.setupWithViewPager(viewPager);

        buttonConfirm.setOnClickListener(confirmClickListener);

        viewPager.post(() -> {
            // In order to be able to show the keyboard for search view
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        });
    }

    private View.OnClickListener confirmClickListener = view -> {
        presenter.sendMessage(messageData);
    };

    @Override
    public void dismiss() {
        presenter.release();
        super.dismiss();
    }

    @Override
    public void onTargetUserRemoved(TargetUser targetUser) {
        View targetUserView = targetUserViewCacheMap.get(targetUser.getId());
        linearLayoutTargetUser.removeView(targetUserView);
        sendMessageTargetAdapter.unSelect(targetUser);
        updateConfirmButtonLabel();
    }

    @Override
    public void onTargetUserAdded(TargetUser targetUser) {
        if (targetUserViewCacheMap.get(targetUser.getId()) == null) {
            targetUserViewCacheMap.put(targetUser.getId(), createUserThumbnailView(targetUser));
        }

        View targetUserView = targetUserViewCacheMap.get(targetUser.getId());
        linearLayoutTargetUser.addView(targetUserView, layoutParams);
        // scroll to the right
        horizontalScrollView.post(() -> horizontalScrollView.fullScroll(View.FOCUS_RIGHT));
        updateConfirmButtonLabel();
    }

    @NonNull
    private UserThumbnailView createUserThumbnailView(TargetUser targetUser) {
        UserThumbnailView userThumbnailView = new UserThumbnailView(getContext());
        userThumbnailView.setOnClickListener(view -> presenter.removeTargetUser(targetUser));
        userThumbnailView.setTargetUser(targetUser);
        return userThumbnailView;
    }

    @Override
    public void onExceedMaxTargetUserCount(int count) {
    }

    @Override
    public void onSendMessageSuccess() {
        if (onSendListener != null) {
            onSendListener.onSendSuccess(this);
        }
        dismiss();
    }

    @Override
    public void onSendMessageFailure() {
        if (onSendListener != null) {
            onSendListener.onSendFailure(this);
        }
        dismiss();
    }

    /**
     * Set a listener to be invoked when the {@link #messageData} is sent.
     *
     * @param listener The {@link OnSendListener} to use.
     */
    public void setOnSendListener(@Nullable OnSendListener listener) {
        if (onSendListener != null) {
            throw new IllegalStateException(
                    "OnSendListener is already taken and can not be replaced.");
        }
        onSendListener = listener;
    }

    /**
     * Interface used to allow the creator of a dialog to run some code when the
     * dialog sends a message to friends or groups.
     */
    public interface OnSendListener {
        /**
         * Called by the given <i>dialog</i> when the dialog sends a message to selected friends
         * and groups <i>successfully</i>.
         *
         * @param dialog The dialog that sends the message data will be passed into the method.
         */
        void onSendSuccess(DialogInterface dialog);

        /**
         * Called by the given <i>dialog</i> when the dialog sends a message to selected friends
         * and groups <i>unsuccessfully</i>.
         *
         * @param dialog The dialog that send the message data will be passed into the method.
         */
        void onSendFailure(DialogInterface dialog);
    }

    private void updateConfirmButtonLabel() {
        int targetCount = presenter.getTargetUserListSize();
        if (targetCount == 0) {
            buttonConfirm.setText(android.R.string.ok);
            buttonConfirm.setVisibility(View.GONE);
        } else {
            String text = getContext().getString(android.R.string.ok) + " (" + targetCount + ")";
            buttonConfirm.setText(text);
            buttonConfirm.setVisibility(View.VISIBLE);
        }
    }
}
