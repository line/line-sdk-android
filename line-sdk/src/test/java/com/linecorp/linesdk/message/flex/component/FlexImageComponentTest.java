package com.linecorp.linesdk.message.flex.component;

import com.linecorp.linesdk.message.flex.action.UriAction;

import org.json.JSONException;
import org.junit.Test;

public class FlexImageComponentTest extends BaseFlexComponentTest {

    @Test
    public void only_image_url() throws JSONException {
        givenImageComponent(FlexImageComponent.newBuilder("https://d.line-scdn.net/n/line_lp/img/logo160629.png"));

        messageShouldBe("{" +
                "  \"type\": \"image\"," +
                "  \"url\": \"https://d.line-scdn.net/n/line_lp/img/logo160629.png\"" +
                "}");
    }

    @Test
    public void with_all_fields() throws JSONException {
        givenImageComponent(FlexImageComponent.newBuilder("https://d.line-scdn.net/n/line_lp/img/logo160629.png")
                .setFlex(0)
                .setMargin(FlexMessageComponent.Margin.XXL)
                .setAlign(FlexMessageComponent.Alignment.START)
                .setGravity(FlexMessageComponent.Gravity.BOTTOM)
                .setSize(FlexMessageComponent.Size.XL3)
                .setAspectRatio(FlexMessageComponent.AspectRatio.RATIO_16x9)
                .setAspectMode(FlexMessageComponent.AspectMode.COVER)
                .setBackgroundColor("#FFFFFF")
                .setAction(new UriAction("http://example.com/page/222", "View details")));

        messageShouldBe("{" +
                "  \"type\": \"image\"," +
                "  \"url\": \"https://d.line-scdn.net/n/line_lp/img/logo160629.png\"," +
                "  \"flex\": 0," +
                "  \"margin\": \"xxl\"," +
                "  \"align\": \"start\"," +
                "  \"gravity\": \"bottom\"," +
                "  \"size\": \"3xl\"," +
                "  \"aspectRatio\": \"16:9\"," +
                "  \"aspectMode\": \"cover\"," +
                "  \"backgroundColor\": \"#FFFFFF\"," +
                "  \"action\": {" +
                "    \"type\": \"uri\"," +
                "    \"label\": \"View details\"," +
                "    \"uri\": \"http://example.com/page/222\"" +
                "  }" +
                "}");
    }

    private void givenImageComponent(FlexImageComponent.Builder builder) {
        component = builder.build();
    }
}
