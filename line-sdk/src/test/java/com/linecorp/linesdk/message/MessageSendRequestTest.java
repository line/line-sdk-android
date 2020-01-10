package com.linecorp.linesdk.message;

import androidx.annotation.NonNull;

import com.linecorp.linesdk.BuildConfig;
import com.linecorp.linesdk.TestConfig;

import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = TestConfig.TARGET_SDK_VERSION)
public class MessageSendRequestTest {
    private MessageSendRequest messageSendRequest;
    private List<String> targetUserIds = Arrays.asList("targetUserId1", "targetUserId2");
    private List<MessageData> messages = Collections.singletonList(new MessageData() {
        @NonNull
        @Override
        public Type getType() {
            return Type.FLEX;
        }
    });

    @Test
    public void createSingleUserType() {
        givenMessageSendRequest(MessageSendRequest.createSingleUserType(targetUserIds.get(0), messages));
        payLoadShouldBe("{\"to\":\"targetUserId1\",\"messages\":[{\"type\":\"flex\"}]}");
    }

    @Test
    public void createMultiUsersType() {
        givenMessageSendRequest(MessageSendRequest.createMultiUsersType(targetUserIds, messages));
        payLoadShouldBe("{\"to\":[\"targetUserId1\",\"targetUserId2\"],\"messages\":[{\"type\":\"flex\"}]}");
    }

    @Test
    public void createOttType() {
        String ott = "oneTimeToken";
        givenMessageSendRequest(MessageSendRequest.createOttType(ott, messages));
        payLoadShouldBe("{\"token\":\"oneTimeToken\",\"messages\":[{\"type\":\"flex\"}]}");
    }

    private void givenMessageSendRequest(MessageSendRequest messageSendRequest) {
        this.messageSendRequest = messageSendRequest;
    }

    private void payLoadShouldBe(String singleUserPayLoad) {
        try {
            assertThat(messageSendRequest.toJsonString(), is(singleUserPayLoad));
        } catch (JSONException e) {
            fail("Fail due to JSONException when calling MessageSendRequest.toJsonString()");
            e.printStackTrace();
        }
    }
}
