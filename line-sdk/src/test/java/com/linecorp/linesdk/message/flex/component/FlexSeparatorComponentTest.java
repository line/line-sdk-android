package com.linecorp.linesdk.message.flex.component;

import org.json.JSONException;
import org.junit.Test;

public class FlexSeparatorComponentTest extends BaseFlexComponentTest {

    @Test
    public void only_type() throws JSONException {
        component = new FlexSeparatorComponent();

        messageShouldBe("{\"type\":\"separator\"}");
    }

    @Test
    public void all_fields() throws JSONException {
        component = FlexSeparatorComponent.newBuilder()
                .setMargin(FlexMessageComponent.Margin.XL)
                .setColor("#00ffff")
                .build();

        messageShouldBe("{" +
                "  \"type\": \"separator\"," +
                "  \"margin\": \"xl\"," +
                "  \"color\": \"#00ffff\"" +
                "}");
    }
}
