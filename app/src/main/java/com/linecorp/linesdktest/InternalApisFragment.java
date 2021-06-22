package com.linecorp.linesdktest;

import android.R.layout;
import android.R.string;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.Group;

import com.linecorp.linesdk.ActionResult;
import com.linecorp.linesdk.FriendSortField;
import com.linecorp.linesdk.GetFriendsResponse;
import com.linecorp.linesdk.GetGroupsResponse;
import com.linecorp.linesdk.LineAccessToken;
import com.linecorp.linesdk.LineApiError;
import com.linecorp.linesdk.LineApiResponse;
import com.linecorp.linesdk.LineApiResponseCode;
import com.linecorp.linesdk.LineFriendProfile;
import com.linecorp.linesdk.LineGroup;
import com.linecorp.linesdk.dialog.SendMessageDialog;
import com.linecorp.linesdk.message.AudioMessage;
import com.linecorp.linesdk.message.ImageMessage;
import com.linecorp.linesdk.message.LocationMessage;
import com.linecorp.linesdk.message.MessageData;
import com.linecorp.linesdk.message.MessageSender;
import com.linecorp.linesdk.message.TemplateMessage;
import com.linecorp.linesdk.message.TextMessage;
import com.linecorp.linesdk.message.VideoMessage;
import com.linecorp.linesdk.message.template.ButtonsLayoutTemplate;
import com.linecorp.linesdk.message.template.CarouselLayoutTemplate;
import com.linecorp.linesdk.message.template.ClickActionForTemplateMessage;
import com.linecorp.linesdk.message.template.ConfirmLayoutTemplate;
import com.linecorp.linesdk.message.template.ImageCarouselLayoutTemplate;
import com.linecorp.linesdk.message.template.UriAction;
import com.linecorp.linesdk.openchat.OpenChatCategory;
import com.linecorp.linesdk.openchat.OpenChatParameters;
import com.linecorp.linesdk.openchat.OpenChatRoomInfo;
import com.linecorp.linesdk.openchat.ui.CreateOpenChatActivity;
import com.linecorp.linesdktest.apiclient.LineOauthApiClientForTest;
import com.linecorp.linesdktest.settings.TestSetting;
import com.linecorp.linesdktest.util.FlexMessageGenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static java.util.Arrays.asList;

public class InternalApisFragment extends BaseApisFragment implements SendMessageDialog.OnSendListener {
    private static final int REQUEST_CODE_CREATE_OPEN_CHATROOM = 4021;
    private final FlexMessageGenerator flexMessageGenerator = new FlexMessageGenerator();

    private final ReceiverList receivers = new ReceiverList();

    @Nullable
    @BindView(R.id.log)
    TextView logView;

    @Nullable
    @BindView(R.id.openchat_api_group)
    Group openChatApiGroup;

    @Nullable
    @BindView(R.id.graph_message_api_group)
    Group graphMessageApiGroup;

    @Nullable
    @BindView(R.id.flex_message_api_group)
    Group flexMessageApiGroup;

    @Nullable
    @BindView(R.id.internal_api_group)
    Group internalApiGroup;

    @Nullable
    private LineOauthApiClientForTest internalOauthApiClient;

    @NonNull
    static InternalApisFragment newFragment(@NonNull TestSetting setting) {
        InternalApisFragment fragment = new InternalApisFragment();
        fragment.setArguments(buildArguments(setting));
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        internalOauthApiClient = new LineOauthApiClientForTest(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_internal_apis, container, false);
        ButterKnife.bind(this, rootView);

        return rootView;
    }

    @OnClick(R.id.clear_log_btn)
    void clearLog() {
        logView.setText("");
    }

    @OnClick(R.id.clear_receivers_btn)
    void clearReceivers() {
        receivers.clearAll();
    }

    @OnClick(R.id.revoke_access_token_btn)
    void revokeToken() {
        startApiAsyncTask("revokeAccessToken", () -> {
            LineApiResponse<LineAccessToken> currentAccessTokenResponse = lineApiClient.getCurrentAccessToken();
            if (!currentAccessTokenResponse.isSuccess()) {
                return currentAccessTokenResponse;
            }
            try {
                String accessToken = currentAccessTokenResponse.getResponseData().getTokenString();
                String response = internalOauthApiClient.revokeAccessToken(channelId, accessToken);
                return LineApiResponse.createAsSuccess(response);
            } catch (IOException e) {
                return LineApiResponse.createAsError(
                        LineApiResponseCode.SERVER_ERROR,
                        new LineApiError(e));
            }
        });
    }

    @OnClick(R.id.get_friends_btn)
    void getAllFriends(View view) {
        receivers.clearFriends();
        getFriends(view, null);
    }

    private void getFriends(View view, String pageRequestToken) {
        startApiAsyncTask("getFriends", () -> {
            LineApiResponse<GetFriendsResponse> response =
                    lineApiClient.getFriends(FriendSortField.NAME, pageRequestToken);

            if (response != null && response.isSuccess() && response.getResponseData() != null) {
                GetFriendsResponse getFriendsResponse = response.getResponseData();
                receivers.addFriends(getFriendsResponse.getFriends());

                // get next page if exists
                // postpone 2 seconds to make sure the first response data is logged. ONLY FOR TESTING purpose
                String nextPageRequestToken = getFriendsResponse.getNextPageRequestToken();
                if (nextPageRequestToken != null) {
                    view.postDelayed(
                            () -> getFriends(view, nextPageRequestToken),
                            2000
                    );
                }
            }

            return response;
        });
    }

    @OnClick(R.id.get_groups_btn)
    void getGroups(View view) {
        receivers.clearGroups();
        getGroups(view, null);
    }

    private void getGroups(View view, String pageRequestToken) {
        startApiAsyncTask("getGroups", () -> {
            LineApiResponse<GetGroupsResponse> response = lineApiClient.getGroups(pageRequestToken);

            if (response != null && response.isSuccess() && response.getResponseData() != null) {
                GetGroupsResponse getGroupsResponse = response.getResponseData();
                receivers.addGroups(getGroupsResponse.getGroups());

                // get next page if exists
                // postpone 2 seconds to make sure the first response data is logged. ONLY FOR TESTING purpose
                String nextPageRequestToken = getGroupsResponse.getNextPageRequestToken();
                if (nextPageRequestToken != null) {
                    view.postDelayed(
                            () -> getGroups(view, nextPageRequestToken),
                            2000
                    );
                }
            }

            return response;
        });
    }

    @OnClick(R.id.get_friends_approvers_btn)
    void getFriendsApprovers() {
        startApiAsyncTask("getFriendApprovers",
                          () -> lineApiClient.getFriendsApprovers(FriendSortField.NAME, null));
    }

    @OnClick(R.id.get_groups_approvers_btn)
    void getGroupsApprovers() {
        selectReceivers(
                receiverIDs ->
                        startApiAsyncTask("getGroupsApprovers", () -> {
                                    String groupId = receiverIDs.get(0);
                                    LineApiResponse<GetFriendsResponse> response =
                                            lineApiClient.getGroupApprovers(
                                                    groupId, null);
                                    return response;
                                }
                                         )
                       );
    }

    @OnClick(R.id.send_all_message_btn)
    void sendAllMessages() {
        sendMessages(createSampleMessageList());
    }

    @OnClick(R.id.send_text_message)
    void sendTextMessage() {
        sendMessages(createTextMessage());
    }

    @OnClick(R.id.send_audio_message)
    void sendAudioMessage() {
        sendMessages(createAudioMessage());
    }

    @OnClick(R.id.send_image_message)
    void sendImageMessage() {
        sendMessages(createImageMessage());
    }

    @OnClick(R.id.send_location_message)
    void sendLocationMessage() {
        sendMessages(createLocationMessage());
    }

    @OnClick(R.id.send_video_message)
    void sendVideoMessage() {
        sendMessages(createVideoMessage());
    }

    @OnClick(R.id.send_all_template_message_btn)
    void sendAllTemplateMessage() {
        sendMessages(createTemplateMessageList());
    }

    @OnClick(R.id.send_buttons_template_message)
    void sendButtonsTemplateMessage() {
        sendMessages(createButtonsTemplateMessage(createActionList()));
    }

    @OnClick(R.id.send_confirm_template_message)
    void sendConfirmTemplateMessage() {
        sendMessages(createConfirmTemplateMessage(createActionList()));
    }

    @OnClick(R.id.send_carousel_template_message)
    void sendCarouselTemplateMessage() {
        sendMessages(
                createCarouselTemplateMessage("carousel altText", createCarouselLayoutTemplate())
        );
    }

    @OnClick(R.id.send_carousel_image_template_message)
    void sendCarouselImageTemplateMessage() {
        sendMessages(
                createCarouselTemplateMessage("carousel with images altText",
                                              createCarouselLayoutTemplate(createActionList())
                )
        );
    }

    @OnClick(R.id.send_image_carousel_template_message)
    void sendImageCarouselTemplateMessage() {
        sendMessages(createImageCarouselTemplateMessage());
    }

    @OnClick(R.id.send_flex_hello_world_message)
    void sendFlexTextMessage() {
        sendMessages(flexMessageGenerator.createFlexMessageWithTextComponent());
    }

    @OnClick(R.id.send_flex_bubble_container_message)
    void sendFlexBubbleContainerMessage() {
        sendMessages(flexMessageGenerator.createFlexBubbleContainerMessage());
    }

    @OnClick(R.id.send_flex_carousel_container_message)
    void sendFlexCarouselContainerMessage() {
        sendMessages(flexMessageGenerator.createFlexCarouselContainerMessage());
    }

    @OnClick(R.id.send_message_dialog)
    void sendMessageDialog() {
        SendMessageDialog sendMessageDialog = new SendMessageDialog(getContext(), lineApiClient);
        sendMessageDialog.setMessageData(flexMessageGenerator.createFlexCarouselContainerMessage());
        sendMessageDialog.setOnSendListener(this);
        sendMessageDialog.setOnCancelListener(
                dialog -> Toast.makeText(getContext(), "Sending message is canceled.", Toast.LENGTH_LONG).show());
        sendMessageDialog.show();
    }

    @OnClick(R.id.graph_message_api_text)
    void toggleGraphMessageApiButtons() {
        toggleGroupVisibility(graphMessageApiGroup);
    }

    @OnClick(R.id.flex_message_api_text)
    void toggleFlexMessageApiButtons() {
        toggleGroupVisibility(flexMessageApiGroup);
    }

    @OnClick(R.id.internal_api_text)
    void toggleInternalApiButtons() {
        toggleGroupVisibility(internalApiGroup);
    }

    @OnClick(R.id.openchat_api_text)
    void toggleOpenChatApiButtons() {
        toggleGroupVisibility(openChatApiGroup);
    }

    @OnClick(R.id.openchat_agreement_get_status_btn)
    void getAgreementStatus() {
        startApiAsyncTask("getOpenChatAgreementStatus", () -> lineApiClient.getOpenChatAgreementStatus());
    }

    @OnClick(R.id.openchat_create_chat_btn)
    void createChatroom() {
        OpenChatParameters parameters = new OpenChatParameters(
                "Demo openchat room",
                "This is a demo chatroom description",
                "Demo app owner",
                OpenChatCategory.Game,
                true);
        startApiAsyncTask("createChatroom", () -> lineApiClient.createOpenChatRoom(parameters));
    }

    @OnClick(R.id.openchat_create_chat_ui_btn)
    void createChatroomWithUi() {
        Intent intent = CreateOpenChatActivity.createIntent(
            getActivity(),
            channelId);

        startActivityForResult(intent, REQUEST_CODE_CREATE_OPEN_CHATROOM);
    }

    @OnClick(R.id.openchat_join_btn)
    void joinOpenChatroom() {
        final EditText input = new EditText(getContext());
        new AlertDialog.Builder(getContext())
                .setTitle("Input Room Id")
                .setView(input)
                .setPositiveButton(string.ok, (dialog, whichButton) -> {
                    String roomId = input.getText().toString();
                    if (roomId.isEmpty()) return;

                    startApiAsyncTask("joinOpenChatRoom", () -> lineApiClient.joinOpenChatRoom(roomId, "demo displayname"));
                }).show();

    }

    @OnClick(R.id.openchat_chatroom_status_btn)
    void getChatroomStatus() {
        final EditText input = new EditText(getContext());
        new AlertDialog.Builder(getContext())
                .setTitle("Input Room Id")
                .setView(input)
                .setPositiveButton(string.ok, (dialog, whichButton) -> {
                    String roomId = input.getText().toString();
                    if (roomId.isEmpty()) return;

                    startApiAsyncTask("getChatroomStatus", () -> lineApiClient.getOpenChatRoomStatus(roomId));
                }).show();

    }

    @OnClick(R.id.openchat_chatroom_join_type_btn)
    void getChatroomJoinType() {
        final EditText input = new EditText(getContext());
        new AlertDialog.Builder(getContext())
                .setTitle("Input Room Id")
                .setView(input)
                .setPositiveButton(string.ok, (dialog, whichButton) -> {
                    String roomId = input.getText().toString();
                    if (roomId.isEmpty()) return;

                    startApiAsyncTask("getChatroomJoinType", () -> lineApiClient.getOpenChatRoomJoinType(roomId));
                }).show();

    }

    @OnClick(R.id.openchat_membership_status_btn)
    void getOpenChatMembershipStatus() {
        final EditText input = new EditText(getContext());
        new AlertDialog.Builder(getContext())
                .setTitle("Input Room Id")
                .setView(input)
                .setPositiveButton(string.ok, (dialog, whichButton) -> {
                    String roomId = input.getText().toString();
                    if (roomId.isEmpty()) return;

                    startApiAsyncTask("getOpenChatMembershipStatus", () -> lineApiClient.getOpenChatMembershipStatus(roomId));
                }).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == REQUEST_CODE_CREATE_OPEN_CHATROOM && resultCode == Activity.RESULT_OK) {
            ActionResult result = CreateOpenChatActivity.getChatRoomCreationResult(intent);
            if (result instanceof ActionResult.Success) {
                OpenChatRoomInfo openChatRoomInfo = (OpenChatRoomInfo)((ActionResult.Success) result).getValue();
                // post operations to openChatRoomInfo
            } else {
                LineApiError lineApiError = (LineApiError) ((ActionResult.Error) result).getValue();
                // post operations to lineApiError
            }
            addLog("== create chatroom with UI ==\n" + result.toString());
        }
    }


    @Override
    public void onSendSuccess(DialogInterface dialog) {
        Toast.makeText(getContext(), "Message sent successfully.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSendFailure(DialogInterface dialog) {
        Toast.makeText(getContext(), "Message sent failure.", Toast.LENGTH_LONG).show();
    }

    @NonNull
    private List<ClickActionForTemplateMessage> createActionList() {
        return asList(
                new UriAction("LINE Developers", "https://developers.line.biz/en/"),
                new UriAction("LINE Blog", "https://official-blog.line.me/en/"));
    }

    private void sendMessages(@NonNull MessageData messageData) {
        sendMessages(asList(messageData));
    }

    private void sendMessages(@NonNull List<MessageData> messageDataList) {
        selectReceivers(
                receiverIDs -> sendMessageInternal(receiverIDs, messageDataList)
        );
    }

    private void selectReceivers(NextAction<List<String>> nextAction) {
        if (receivers.isEmpty()) {
            Toast.makeText(getContext(), "run GetGroups or GetFriends first", Toast.LENGTH_LONG).show();
            return;
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select Friend/Group to send message");

        final ListView listView = new ListView(getContext());
        listView.setItemsCanFocus(false);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        final ArrayAdapter<Receiver> adapter = new ArrayAdapter<>(getContext(),
                                                                  layout.select_dialog_multichoice);
        adapter.addAll(receivers.getAll());
        listView.setAdapter(adapter);

        builder.setNegativeButton(string.cancel, (dialog, which) -> dialog.dismiss());
        builder.setPositiveButton(
                string.ok,
                (dialog, which) -> {
                    final List<String> receiverIDs = getSelectedReceiverIDs(listView);

                    if (receiverIDs.isEmpty()) {
                        Toast.makeText(getContext(), "No Friend/Group selected", Toast.LENGTH_LONG).show();
                    } else {
                        nextAction.run(receiverIDs);
                    }
                }
        );
        builder.setView(listView);
        builder.show();
    }

    private final List<String> getSelectedReceiverIDs(final ListView listView) {
        final List<String> receiverIDs = new ArrayList<>();

        final Adapter adapter = listView.getAdapter();
        final SparseBooleanArray checkedItems = listView.getCheckedItemPositions();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (checkedItems.get(i)) {
                receiverIDs.add(((Receiver) adapter.getItem(i)).id);
            }
        }

        return receiverIDs;
    }

    private void sendMessageInternal(List<String> toList, List<MessageData> messageList) {
        startApiAsyncTask("sendMessage", () -> {
            if (toList.size() == 1) {
                return lineApiClient.sendMessage(toList.get(0), messageList);
            } else {
                return lineApiClient.sendMessageToMultipleUsers(toList, messageList);
            }
        });
    }

    private List<MessageData> createSampleMessageList() {
        return asList(
                createTextMessage(),
                createImageMessage(),
                createVideoMessage(),
                createAudioMessage(),
                createLocationMessage());
    }

    @NonNull
    private MessageData createLocationMessage() {
        return new LocationMessage("LINE",
                                   "No. 610, Ruiguang Road, Neihu District",
                                   25.080330d,
                                   121.565789d);
    }

    @NonNull
    private MessageData createAudioMessage() {
        return new AudioMessage(
                "https://archive.org/download/IHaveNoIdeaEnM4a/IHaveNoIdea.m4a",
                115000L);
    }

    @NonNull
    private MessageData createVideoMessage() {
        return new VideoMessage("https://archive.org/download/Popeye_Nearlyweds/Popeye_Nearlyweds_512kb.mp4",
                                "https://archive.org/download/Popeye_Nearlyweds/Popeye_Nearlyweds.thumbs/Popeye_Nearlyweds_000001.jpg");
    }

    @NonNull
    private MessageData createImageMessage() {
        return new ImageMessage("https://c2.staticflickr.com/2/1846/29669547557_dde8b3816e_c.jpg",
                                "https://c2.staticflickr.com/2/1846/29669547557_dde8b3816e_n.jpg");
    }

    @NonNull
    private MessageData createTextMessage() {
        return new TextMessage("Default Text Message",
                               new MessageSender("demoLabel",
                                                 "https://raw.githubusercontent.com/google/material-design-icons/master/social/2x_web/ic_cake_black_36dp.png",
                                                 "https://developers.line.biz/en/"));
    }

    private List<MessageData> createTemplateMessageList() {
        List<ClickActionForTemplateMessage> actionList = createActionList();

        return asList(
                createButtonsTemplateMessage(actionList),
                createConfirmTemplateMessage(actionList),
                createCarouselTemplateMessage("carousel with images altText",
                                              createCarouselLayoutTemplate(actionList)),
                createCarouselTemplateMessage("carousel altText", createCarouselLayoutTemplate()),
                createImageCarouselTemplateMessage());
    }

    @NonNull
    private TemplateMessage createImageCarouselTemplateMessage() {
        return new TemplateMessage("image carousel altText", createImageCarouselLayoutTemplate());
    }

    @NonNull
    private TemplateMessage createCarouselTemplateMessage(String s,
                                                          CarouselLayoutTemplate carouselLayoutTemplate) {
        return new TemplateMessage(s, carouselLayoutTemplate);
    }

    @NonNull
    private TemplateMessage createConfirmTemplateMessage(List<ClickActionForTemplateMessage> actionList) {
        return new TemplateMessage("confirm altText",
                                   new ConfirmLayoutTemplate("Confirm Template", actionList));
    }

    @NonNull
    private TemplateMessage createButtonsTemplateMessage(List<ClickActionForTemplateMessage> actionList) {
        return new TemplateMessage("buttons altText",
                                   new ButtonsLayoutTemplate("ButtonsLayoutTemplate LayoutTemplate",
                                           actionList) {
                                       {
                                           setTitle("button title");
                                           setThumbnailImageUrl("https://picsum.photos/600/390.jpg");
                                       }
                                   }
        );
    }

    @NonNull
    private ImageCarouselLayoutTemplate createImageCarouselLayoutTemplate() {
        List<ImageCarouselLayoutTemplate.ImageCarouselColumn> imageCarouselColumnList = asList(
                new ImageCarouselLayoutTemplate.ImageCarouselColumn(
                        "https://picsum.photos/600/390.jpg",
                        new UriAction("LINE Dev", "https://developers.line.biz/en/")),
                new ImageCarouselLayoutTemplate.ImageCarouselColumn(
                        "https://picsum.photos/600/390.jpg",
                        new UriAction("LINE Blog", "https://official-blog.line.me/en/")));
        return new ImageCarouselLayoutTemplate(imageCarouselColumnList);
    }

    @NonNull
    private CarouselLayoutTemplate createCarouselLayoutTemplate(
            List<ClickActionForTemplateMessage> actionList) {
        List<CarouselLayoutTemplate.CarouselColumn> carouselWithImageColumnList = asList(
                new CarouselLayoutTemplate.CarouselColumn("carousel image 1", actionList),
                new CarouselLayoutTemplate.CarouselColumn("carousel image 2", actionList));
        carouselWithImageColumnList.get(0).setThumbnailImageUrl("https://picsum.photos/600/390.jpg");
        carouselWithImageColumnList.get(1).setThumbnailImageUrl("https://picsum.photos/600/390.jpg");
        return new CarouselLayoutTemplate(carouselWithImageColumnList);
    }

    @NonNull
    private CarouselLayoutTemplate createCarouselLayoutTemplate() {
        List<ClickActionForTemplateMessage> actionList = createActionList();
        List<CarouselLayoutTemplate.CarouselColumn> carouselColumnList = asList(
                new CarouselLayoutTemplate.CarouselColumn("carousel item 1", actionList),
                new CarouselLayoutTemplate.CarouselColumn("carousel item 2", actionList));
        return new CarouselLayoutTemplate(carouselColumnList);
    }

    private void toggleGroupVisibility(Group group) {
        if (group.getVisibility() == View.VISIBLE) {
            group.setVisibility(View.GONE);
        } else {
            group.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void addLog(@NonNull String logText) {
        Log.d("LineSdkTest", logText);
        if (logView != null) {
            logView.setText(logView.getText() + LOG_SEPARATOR + logText);
        }
    }

    private static class ReceiverList {
        private final List<Receiver> friends = new ArrayList<>();
        private final List<Receiver> groups = new ArrayList<>();

        private void addFriends(final List<LineFriendProfile> friendList) {
            for (final LineFriendProfile friend : friendList) {
                friends.add(new Receiver(friend));
            }
        }

        private void addGroups(final List<LineGroup> groupList) {
            for (final LineGroup group : groupList) {
                groups.add(new Receiver(group));
            }
        }

        private void clearFriends() {
            friends.clear();
        }

        private void clearGroups() {
            groups.clear();
        }

        private void clearAll() {
            clearFriends();
            clearGroups();
        }

        private boolean isEmpty() {
            return friends.isEmpty() && groups.isEmpty();
        }

        private List<Receiver> getAll() {
            List<Receiver> list = new ArrayList<>();

            list.addAll(friends);
            list.addAll(groups);

            return list;
        }
    }

    private static class Receiver {
        final Type type;
        final String id;
        final String displayName;

        private Receiver(final LineFriendProfile friend) {
            type = Type.Friend;
            id = friend.getUserId();
            displayName = friend.getAvailableDisplayName();
        }

        private Receiver(final LineGroup group) {
            type = Type.Group;
            id = group.getGroupId();
            displayName = group.getGroupName();
        }

        @Override
        public String toString() {
            return type + ": " + displayName;
        }

        private enum Type {
            Friend,
            Group
        }
    }
}
