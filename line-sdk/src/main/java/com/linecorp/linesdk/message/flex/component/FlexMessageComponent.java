package com.linecorp.linesdk.message.flex.component;

import androidx.annotation.NonNull;

import com.linecorp.linesdk.message.Jsonable;
import com.linecorp.linesdk.message.Stringable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents a flex message component which acts as a part of a {@link com.linecorp.linesdk.message.flex.container.FlexMessageContainer}.
 * <p>
 * <li> box: Represents the type of box component. A {@link FlexBoxComponent} value is associated.</li>
 * <li> text: Represents the type of text component. A {@link FlexTextComponent} value is associated.</li>
 * <li> button: Represents the type of button component. A {@link FlexButtonComponent} value is associated.</li>
 * <li> image: Represents the type of image component. A {@link FlexImageComponent} value is associated.</li>
 * <li> filler: Represents the type of filler component. A {@link FlexFillerComponent} value is associated.</li>
 * <li> icon: Represents the type of icon component. A {@link FlexIconComponent} value is associated.</li>
 * <li> separator: Represents the type of separator component. A {@link FlexSeparatorComponent} value is associated.</li>
 * <li> spacer: Represents the type of spacer component. A {@link FlexSpacerComponent} value is associated.</li>
 * <p>
 * For more information, @see <a herf=https://developers.line.biz/en/reference/messaging-api/#component>component</a>
 */
public abstract class FlexMessageComponent implements Jsonable {
    protected final static int FLEX_VALUE_NONE = -1;  // indicates flex value is not set

    public enum Type implements Stringable {
        BOX,
        BUTTON,
        FILLER,
        ICON,
        IMAGE,
        SEPARATOR,
        SPACER,
        TEXT
    }

    public enum Layout implements Stringable {
        HORIZONTAL,
        VERTICAL,
        BASELINE
    }

    /**
     * Represents a spacing between a component and the previous component in the parent box.
     * <p>
     * <li> none: No spacing between.</li>
     * <li> xs: Extra small size.</li>
     * <li> sm: Small size.</li>
     * <li> md: Middle size.</li>
     * <li> lg: Large size.</li>
     * <li> xl: Extra large size.</li>
     * <li> xxl: Double extra large size.</li>
     */
    public enum Margin implements Stringable {
        NONE,
        XS,
        SM,
        MD,
        LG,
        XL,
        XXL
    }

    /**
     * Represents the horizontal alignment of texts or images in component.
     * <p>
     * <li> start: Leading aligned.</li>
     * <li> end: Trailing aligned.</li>
     * <li> center: Center aligned</li>
     */
    public enum Alignment implements Stringable {
        START,
        END,
        CENTER
    }

    /**
     * Represents the vertical alignment of texts or images in component.
     * <p>
     * <li> top: Top aligned.</li>
     * <li> bottom: Bottom aligned.</li>
     * <li> center: Center aligned.</li>
     */
    public enum Gravity implements Stringable {
        TOP,
        BOTTOM,
        CENTER
    }

    /**
     * Represents a size for some components.
     *
     * <li> xxs: Double extra small size.</li>
     * <li> xs: Extra small size.</li>
     * <li> sm: Small size.</li>
     * <li> md: Middle size.</li>
     * <li> lg: Large size.</li>
     * <li> xl: Extra large size.</li>
     * <li> xxl: Double extra large size.</li>
     * <li> xl3: 3xl size.</li>
     * <li> xl4: 4xl size.</li>
     * <li> xl5: 5xl size.</li>
     * <li> full: The full size.</li>
     */
    public enum Size implements Stringable {
        XXS("xxs"),
        XS("xs"),
        SM("sm"),
        MD("md"),
        LG("lg"),
        XL("xl"),
        XXL("xxl"),
        XL3("3xl"),
        XL4("4xl"),
        XL5("5xl"),
        FULL("full");

        private String value;

        Size(@NonNull String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * Represents aspect ratio for an image in a component. Width versus height.
     *
     * <li> ratio_1x1: Ratio 1:1.</li>
     * <li> ratio_1_51x1: Ratio 1.51:1.</li>
     * <li> ratio_1_91x1: Ratio 1.91:1.</li>
     * <li> ratio_4x3: Ratio 4:3.</li>
     * <li> ratio_16x9: Ratio 16:9.</li>
     * <li> ratio_20x13: Ratio 20:13.</li>
     * <li> ratio_2x1: Ratio 2:1.</li>
     * <li> ratio_3x1: Ratio 3:1.</li>
     * <li> ratio_3x4: Ratio 3:4.</li>
     * <li> ratio_9x16: Ratio 9:16.</li>
     * <li> ratio_1x2: Ratio 1:2.</li>
     * <li> ratio_1x3: Ratio 1:3.</li>
     */
    public enum AspectRatio {
        RATIO_1x1("1:1"),
        RATIO_1_51x1("1.51:1"),
        RATIO_1_91x1("1.91:1"),
        RATIO_4x3("4:3"),
        RATIO_16x9("16:9"),
        RATIO_20x13("20:13"),
        RATIO_2x1("2:1"),
        RATIO_3x1("3:1"),
        RATIO_3x4("3:4"),
        RATIO_9x16("9:16"),
        RATIO_1x2("1:2"),
        RATIO_1x3("1:3");

        private String value;

        AspectRatio(@NonNull String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * Represents aspect scale mode for an image in a component.
     *
     * <li> fill: With "cover" as its raw value. Aspect scales the image to completely fill the image container. </li>
     * <li> fit: With "fit" as its raw value. Aspect scales the image to fit inside the image container.</li>
     */
    public enum AspectMode implements Stringable {
        COVER,
        FIT
    }

    public enum Weight implements Stringable {
        BOLD,
        REGULAR
    }

    /**
     * Represents height of a component.
     *  <li> sm: Small size.</li>
     *  <li> md: Middle size.</li>
     */
    public enum Height implements Stringable {
        SM,
        MD
    }

    /**
     * Represents style of a component.
     *  <li> <code>link</code>: HTML link style</li>
     *  <li> <code>primary</code>: Style for dark color buttons</li>
     *  <li> <code>secondary</code>: Style for light color buttons</li>
     */
    public enum Style implements Stringable {
        LINK,
        PRIMARY,
        SECONDARY
    }

    @NonNull
    final protected Type type;

    public FlexMessageComponent(@NonNull Type type) {
        this.type = type;
    }

    @NonNull
    @Override
    public JSONObject toJsonObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", type.name().toLowerCase());
        return jsonObject;
    }
}
