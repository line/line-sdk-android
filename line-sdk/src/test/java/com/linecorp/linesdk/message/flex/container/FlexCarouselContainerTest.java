package com.linecorp.linesdk.message.flex.container;

import com.linecorp.linesdk.LineSdkTestRunner;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class FlexCarouselContainerTest extends LineSdkTestRunner {
    private FlexBubbleContainer flexBubbleContainer = FlexBubbleContainer.newBuilder().build();
    private FlexCarouselContainer container = new FlexCarouselContainer(Arrays.asList(flexBubbleContainer));

    @Test
    public void all_fields() throws JSONException {
        messageShouldBe("{\"type\":\"carousel\",\"contents\":[{\"type\":\"bubble\"}]}");
    }

    private void messageShouldBe(String expected) throws JSONException {
        assertEquals(new JSONObject(expected).toString(), container.toJsonObject().toString());
    }
}
