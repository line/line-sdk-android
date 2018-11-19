package com.linecorp.linesdk.message.flex.component;

import com.linecorp.linesdk.message.flex.action.UriAction;

import org.json.JSONException;
import org.junit.Test;

public class FlexTextComponentTest extends BaseFlexComponentTest {

    @Test
    public void all_fields() throws JSONException {
        givenTextComponent(FlexTextComponent.newBuilder("Hello, World!")
                .setFlex(2)
                .setAlign(FlexMessageComponent.Alignment.START)
                .setGravity(FlexMessageComponent.Gravity.CENTER)
                .setMargin(FlexMessageComponent.Margin.XL)
                .setMaxLines(3)
                .setWrap(true)
                .setSize(FlexMessageComponent.Size.XL)
                .setWeight(FlexMessageComponent.Weight.BOLD)
                .setColor("#0000ff")
                .setAction(new UriAction("https://d.line-scdn.net/n/line_lp/img/logo160629.png", "View details")));

        messageShouldBe("{" +
                "  \"type\": \"text\"," +
                "  \"text\": \"Hello, World!\"," +
                "  \"margin\": \"xl\"," +
                "  \"size\": \"xl\"," +
                "  \"align\": \"start\"," +
                "  \"gravity\": \"center\"," +
                "  \"wrap\": true," +
                "  \"weight\": \"bold\"," +
                "  \"color\": \"#0000ff\"," +
                "  \"action\": {" +
                "    \"type\": \"uri\"," +
                "    \"label\": \"View details\"," +
                "    \"uri\": \"https://d.line-scdn.net/n/line_lp/img/logo160629.png\"" +
                "  }," +
                "  \"flex\": 2," +
                "  \"maxLines\": 3" +
                "}");
    }

    private void givenTextComponent(FlexTextComponent.Builder builder) {
        component = builder.build();
    }
}
