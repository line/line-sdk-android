package com.linecorp.linesdk.message.flex.component;

import org.json.JSONException;
import org.junit.Test;

public class FlexIconComponentTest extends BaseFlexComponentTest {

    @Test
    public void all_fields() throws JSONException {
        component = FlexIconComponent.newBuilder("https://example.com/icon/png/caution.png")
                .setMargin(FlexMessageComponent.Margin.XL)
                .setSize(FlexMessageComponent.Size.XL5)
                .setAspectRatio(FlexMessageComponent.AspectRatio.RATIO_1x1)
                .build();

        messageShouldBe("{" +
                "  \"type\": \"icon\"," +
                "  \"url\": \"https://example.com/icon/png/caution.png\"," +
                "  \"margin\": \"xl\"," +
                "  \"size\": \"xl5\"," +
                "  \"aspectRatio\": \"1:1\"" +
                "}");
    }

    @Test
    public void without_margin_and_ratio() throws JSONException {
        component = FlexIconComponent.newBuilder("https://example.com/icon/png/caution.png")
                .setSize(FlexMessageComponent.Size.XXL)
                .build();

        messageShouldBe("{" +
                "  \"type\": \"icon\"," +
                "  \"url\": \"https://example.com/icon/png/caution.png\"," +
                "  \"size\": \"xxl\"" +
                "}");
    }
}
