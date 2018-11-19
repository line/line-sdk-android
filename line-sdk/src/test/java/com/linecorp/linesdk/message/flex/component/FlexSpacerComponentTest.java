package com.linecorp.linesdk.message.flex.component;

import org.json.JSONException;
import org.junit.Test;

public class FlexSpacerComponentTest extends BaseFlexComponentTest {

    @Test
    public void only_type() throws JSONException {
        component = new FlexSpacerComponent();
        messageShouldBe("{\"type\":\"spacer\"}");
    }

    @Test
    public void with_size() throws JSONException {
        component = new FlexSpacerComponent();
        ((FlexSpacerComponent) component).setSize(FlexMessageComponent.Size.FULL);

        messageShouldBe("{" +
                "  \"type\": \"spacer\"," +
                "  \"size\": \"full\"" +
                "}");
    }
}
