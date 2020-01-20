package com.linecorp.linesdk.internal.nwclient;

import com.linecorp.linesdk.SendMessageResponse;
import com.linecorp.linesdk.TestConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = TestConfig.TARGET_SDK_VERSION)
public class ApiParserTest {

    @Test
    public void stringParser() throws JSONException {
        JsonToObjectBaseResponseParser<String> parser = new TalkApiClient.StringParser("status");
        JSONObject jsonObject = new JSONObject().put("status", "mockStatus");

        assertThat(parser.parseJsonToObject(jsonObject), is("mockStatus"));
    }

    @Test
    public void multiSendResponseParser() throws JSONException {
        JsonToObjectBaseResponseParser<List<SendMessageResponse>> parser = new TalkApiClient.MultiSendResponseParser();
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(new JSONObject().put("to", "targetUserId1").put("status", "ok"));
        jsonArray.put(new JSONObject().put("to", "targetUserId2").put("status", "discarded"));
        JSONObject jsonObject = new JSONObject().put("results", jsonArray);

        List<SendMessageResponse> sendMessageResponses = parser.parseJsonToObject(jsonObject);
        assertThat(sendMessageResponses.size(), is(2));
        assertThat(sendMessageResponses.get(0).getTargetUserId(), is("targetUserId1"));
        assertThat(sendMessageResponses.get(0).getStatus(), is(SendMessageResponse.Status.OK));
        assertThat(sendMessageResponses.get(1).getTargetUserId(), is("targetUserId2"));
        assertThat(sendMessageResponses.get(1).getStatus(), is(SendMessageResponse.Status.DISCARDED));
    }
}
