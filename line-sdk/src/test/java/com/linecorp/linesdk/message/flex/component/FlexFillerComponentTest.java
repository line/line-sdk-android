package com.linecorp.linesdk.message.flex.component;

import org.json.JSONException;
import org.junit.Test;

public class FlexFillerComponentTest extends BaseFlexComponentTest {

    @Test
    public void field_type() throws JSONException {
        component = new FlexFillerComponent();

        messageShouldBe("{" +
                "  \"type\": \"filler\"" +
                "}");
    }
}
