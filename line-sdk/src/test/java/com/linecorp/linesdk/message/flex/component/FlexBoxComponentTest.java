package com.linecorp.linesdk.message.flex.component;

import com.linecorp.linesdk.message.flex.action.UriAction;

import org.json.JSONException;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class FlexBoxComponentTest extends BaseFlexComponentTest {
    private List<FlexMessageComponent> contents = Arrays.asList(
            FlexTextComponent.newBuilder("Hello, World!").build(),
            FlexImageComponent
                    .newBuilder("https://example.com/flex/images/image.jpg").build()
    );

    @Test
    public void only_required_fields() throws JSONException {
        component = FlexBoxComponent.newBuilder(FlexMessageComponent.Layout.VERTICAL, contents).build();

        messageShouldBe("{" +
                "  \"type\": \"box\"," +
                "  \"layout\": \"vertical\"," +
                "  \"contents\": [" +
                "    {" +
                "      \"type\": \"text\"," +
                "      \"text\": \"Hello, World!\"" +
                "    }," +
                "    {" +
                "      \"type\": \"image\"," +
                "      \"url\": \"https://example.com/flex/images/image.jpg\"" +
                "    }" +
                "  ]" +
                "}");
    }

    @Test
    public void all_fields() throws JSONException {
        component = FlexBoxComponent.newBuilder(FlexMessageComponent.Layout.HORIZONTAL, contents)
                .setFlex(1)
                .setMargin(FlexMessageComponent.Margin.MD)
                .setSpacing(FlexMessageComponent.Margin.MD)
                .setAction(new UriAction("https://d.line-scdn.net/n/line_lp/img/logo160629.png", "View details"))
                .build();

        messageShouldBe("{" +
                "  \"type\": \"box\"," +
                "  \"layout\": \"horizontal\"," +
                "  \"contents\": [" +
                "    {" +
                "      \"type\": \"text\"," +
                "      \"text\": \"Hello, World!\"" +
                "    }," +
                "    {" +
                "      \"type\": \"image\"," +
                "      \"url\": \"https://example.com/flex/images/image.jpg\"" +
                "    }" +
                "  ]," +
                "  \"spacing\": \"md\"," +
                "  \"margin\": \"md\"," +
                "  \"action\": {" +
                "    \"type\": \"uri\"," +
                "    \"label\": \"View details\"," +
                "    \"uri\": \"https://d.line-scdn.net/n/line_lp/img/logo160629.png\"" +
                "  }," +
                "  \"flex\": 1" +
                "}");
    }
}
