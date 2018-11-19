package com.linecorp.linesdk.message.flex.container;

import com.linecorp.linesdk.LineSdkTestRunner;
import com.linecorp.linesdk.message.flex.component.FlexBoxComponent;
import com.linecorp.linesdk.message.flex.component.FlexImageComponent;
import com.linecorp.linesdk.message.flex.component.FlexMessageComponent;
import com.linecorp.linesdk.message.flex.component.FlexTextComponent;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class FlexBubbleContainerTest extends LineSdkTestRunner {

    private FlexImageComponent imageComponent = FlexImageComponent.newBuilder("https://d.line-scdn.net/n/line_lp/img/logo160629.png").build();

    private FlexTextComponent textComponent = FlexTextComponent.newBuilder("Hello !").build();

    private FlexBubbleContainer flexBubbleContainer;

    @Test
    public void body_with_only_texts() throws JSONException {
        givenFlexBubbleContainer(FlexBubbleContainer.newBuilder().setBody(FlexBoxComponent.newBuilder(
                FlexMessageComponent.Layout.HORIZONTAL,
                Arrays.asList(textComponent, textComponent)
        ).build()));

        messageShouldBe("{" +
                "  \"type\": \"bubble\"," +
                "  \"body\": {" +
                "    \"type\": \"box\"," +
                "    \"layout\": \"horizontal\"," +
                "    \"contents\": [" +
                "      {" +
                "        \"type\": \"text\"," +
                "        \"text\": \"Hello !\"" +
                "      }," +
                "      {" +
                "        \"type\": \"text\"," +
                "        \"text\": \"Hello !\"" +
                "      }" +
                "    ]" +
                "  }" +
                "}");
    }

    @Test
    public void hero_with_image() throws JSONException {
        givenFlexBubbleContainer(FlexBubbleContainer.newBuilder().setHero(imageComponent));

        messageShouldBe("{" +
                "  \"type\": \"bubble\"," +
                "  \"hero\": {" +
                "    \"type\": \"image\"," +
                "    \"url\": \"https://d.line-scdn.net/n/line_lp/img/logo160629.png\"" +
                "  }" +
                "}");
    }

    @Test
    public void all_fields() throws JSONException {
        FlexBoxComponent boxComponent = FlexBoxComponent.newBuilder(
                FlexMessageComponent.Layout.HORIZONTAL,
                Arrays.asList(new FlexMessageComponent[]{textComponent, imageComponent})
        ).build();
        givenFlexBubbleContainer(
                FlexBubbleContainer.newBuilder()
                        .setStyles(new FlexBubbleContainer.Style())
                        .setDirection(FlexBubbleContainer.Direction.LEFT_TO_RIGHT)
                        .setHeader(boxComponent)
                        .setBody(boxComponent)
                        .setHero(imageComponent)
                        .setFooter(boxComponent)
        );

        messageShouldBe("{" +
                "  \"type\": \"bubble\"," +
                "  \"direction\": \"ltr\"," +
                "  \"header\": {" +
                "    \"type\": \"box\"," +
                "    \"layout\": \"horizontal\"," +
                "    \"contents\": [" +
                "      {" +
                "        \"type\": \"text\"," +
                "        \"text\": \"Hello !\"" +
                "      }," +
                "      {" +
                "        \"type\": \"image\"," +
                "        \"url\": \"https://d.line-scdn.net/n/line_lp/img/logo160629.png\"" +
                "      }" +
                "    ]" +
                "  }," +
                "  \"hero\": {" +
                "    \"type\": \"image\"," +
                "    \"url\": \"https://d.line-scdn.net/n/line_lp/img/logo160629.png\"" +
                "  }," +
                "  \"body\": {" +
                "    \"type\": \"box\"," +
                "    \"layout\": \"horizontal\"," +
                "    \"contents\": [" +
                "      {" +
                "        \"type\": \"text\"," +
                "        \"text\": \"Hello !\"" +
                "      }," +
                "      {" +
                "        \"type\": \"image\"," +
                "        \"url\": \"https://d.line-scdn.net/n/line_lp/img/logo160629.png\"" +
                "      }" +
                "    ]" +
                "  }," +
                "  \"footer\": {" +
                "    \"type\": \"box\"," +
                "    \"layout\": \"horizontal\"," +
                "    \"contents\": [" +
                "      {" +
                "        \"type\": \"text\"," +
                "        \"text\": \"Hello !\"" +
                "      }," +
                "      {" +
                "        \"type\": \"image\"," +
                "        \"url\": \"https://d.line-scdn.net/n/line_lp/img/logo160629.png\"" +
                "      }" +
                "    ]" +
                "  }," +
                "  \"styles\": {}" +
                "}");
    }

    private void messageShouldBe(String expected) throws JSONException {
        assertEquals(new JSONObject(expected).toString(), flexBubbleContainer.toJsonObject().toString());
    }

    private void givenFlexBubbleContainer(FlexBubbleContainer.Builder builder) {
        flexBubbleContainer = builder.build();
    }
}
