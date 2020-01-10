package com.linecorp.linesdk.message;

import com.linecorp.linesdk.BuildConfig;
import com.linecorp.linesdk.TestConfig;

import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = TestConfig.TARGET_SDK_VERSION)
public class OttRequestTest {
    private OttRequest ottRequest;
    private List<String> targetUserIds = Arrays.asList("targetUserId1", "targetUserId2");

    @Test
    public void createOttRequestPayLoad() {
        givenOttRequest();
        payLoadShouldBe();
    }

    private void givenOttRequest() {
        this.ottRequest = new OttRequest(targetUserIds);
    }

    private void payLoadShouldBe() {
        try {
            assertThat(ottRequest.toJsonString(), is("{\"userIds\":[\"targetUserId1\",\"targetUserId2\"]}"));
        } catch (JSONException e) {
            fail("Fail due to JSONException when calling OttRequest.toJsonString()");
            e.printStackTrace();
        }
    }
}
