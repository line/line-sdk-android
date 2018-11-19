package com.linecorp.linesdk.message.flex.component;

import com.linecorp.linesdk.message.flex.action.UriAction;

import org.json.JSONException;
import org.junit.Test;

public class FlexButtonComponentTest extends BaseFlexComponentTest {

    @Test
    public void only_action() throws JSONException {
        givenButtonComponent(FlexButtonComponent
                .newBuilder(new UriAction("https://d.line-scdn.net/n/line_lp/img/logo160629.png",
                        "View details")));

        messageShouldBe("{" +
                "  \"type\": \"button\"," +
                "  \"action\": {" +
                "    \"type\": \"uri\"," +
                "    \"label\": \"View details\"," +
                "    \"uri\": \"https://d.line-scdn.net/n/line_lp/img/logo160629.png\"" +
                "  }" +
                "}");
    }

    @Test
    public void all_fields() throws JSONException {
        givenButtonComponent(FlexButtonComponent
                .newBuilder(new UriAction("https://d.line-scdn.net/n/line_lp/img/logo160629.png", "View details"))
                .setFlex(3)
                .setGravity(FlexMessageComponent.Gravity.BOTTOM)
                .setColor("#ff0000")
                .setHeight(FlexMessageComponent.Height.MD)
                .setMargin(FlexMessageComponent.Margin.XL)
                .setStyle(FlexMessageComponent.Style.LINK));

        messageShouldBe("{" +
                "  \"type\": \"button\"," +
                "  \"action\": {" +
                "    \"type\": \"uri\"," +
                "    \"label\": \"View details\"," +
                "    \"uri\": \"https://d.line-scdn.net/n/line_lp/img/logo160629.png\"" +
                "  }," +
                "  \"margin\": \"xl\"," +
                "  \"height\": \"md\"," +
                "  \"style\": \"link\"," +
                "  \"color\": \"#ff0000\"," +
                "  \"gravity\": \"bottom\"," +
                "  \"flex\": 3" +
                "}");
    }

    private void givenButtonComponent(FlexButtonComponent.Builder builder) {
        component = builder.build();
    }
}
