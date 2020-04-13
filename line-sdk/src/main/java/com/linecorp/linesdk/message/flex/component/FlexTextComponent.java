package com.linecorp.linesdk.message.flex.component;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.linecorp.linesdk.message.flex.action.Action;
import com.linecorp.linesdk.utils.JSONUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents a text component in a flex message.
 * A text component contains some formatted text. LINE clients will render the text in a flex message.
 * <p>
 * For more information, please refer to <a herf=https://developers.line.biz/en/reference/messaging-api/#f-text>Text component</a>
 */
public class FlexTextComponent extends FlexMessageComponent {
    protected final static int MAXLINES_VALUE_NONE = -1;  // indicates maxLines value is not set

    /**
     * Required. Content text of this component.
     */
    @NonNull
    private String text;

    /**
     * Optional. The ratio of the width or height of this box within the parent box. The default value for
     * the horizontal parent box is 1, and the default value for the vertical parent box is 0.
     */
    @Nullable
    private int flex;

    /**
     * Optional. Minimum space between this component and the previous component in the parent box.
     *
     * @see {@link Margin}
     * The default value is the value of the <code>spacing</code> property of the parent box.
     * If this component is the first component in the parent box, this <code>margin</code> property will be ignored.
     */
    @Nullable
    private Margin margin;

    /**
     * Optional. Font size. @see {@link Size}
     * The size increases in the order of listing. The default value is <code>md</code>.
     */
    @Nullable
    private Size size;

    /**
     * Optional. Horizontal alignment style. Specify one of the following values:
     * <li> start: Left-aligned</li>
     * <li> end: Right-aligned</li>
     * <li> center: Center-aligned</li>
     * The default value is <code>start</code>.
     * Specify an {@link Alignment}
     */
    @Nullable
    private Alignment align;

    /**
     * Optional. Vertical alignment style. Specify one of the following values:
     * <li> top: Top-aligned</li>
     * <li> bottom: Bottom-aligned</li>
     * <li> center: Center-aligned</li>
     * The default value is top.
     * If the layout property of the parent box is <code>baseline</code>, the gravity property will be ignored.
     * Specify a {@link Gravity}
     */
    @Nullable
    private Gravity gravity;

    /**
     * Optional. true to wrap text. The default value is false.
     * If set to true, you can use a new line character (\n) to begin on a new line.
     */
    private Boolean wrap;

    /**
     * Optional. Max number of lines. If the text does not fit in the specified number of lines,
     * an ellipsis (…) is displayed at the end of the last line.
     * If set to 0, all the text is displayed. The default value is 0.
     * This property is supported on the following versions of LINE.
     * <li> LINE for iOS and Android: 8.11.0 and later</li>
     * <li> LINE for Windows and macOS: 5.9.0 and later</li>
     */
    private int maxLines;

    /**
     * Optional. Font weight. You can specify one of the following values: regular, or bold.
     * Specifying bold makes the font bold. The default value is regular.
     */
    @Nullable
    private Weight weight;

    /**
     * Font color. Use a hexadecimal color code.
     */
    @Nullable
    private String color;

    /**
     * Optional	Action performed when this text is tapped.
     * Specify an <a href="https://developers.line.biz/en/reference/messaging-api/#action-objects">action object</a>.
     */
    @Nullable
    private Action action;

    private FlexTextComponent() {
        super(Type.TEXT);
    }


    private FlexTextComponent(@NonNull Builder builder) {
        this();
        text = builder.text;
        flex = builder.flex;
        margin = builder.margin;
        size = builder.size;
        align = builder.align;
        gravity = builder.gravity;
        wrap = builder.wrap;
        maxLines = builder.maxLines;
        weight = builder.weight;
        color = builder.color;
        action = builder.action;
    }

    public static Builder newBuilder(@NonNull String text) {
        return new Builder(text);
    }

    @NonNull
    @Override
    public JSONObject toJsonObject() throws JSONException {
        JSONObject jsonObject = super.toJsonObject();
        jsonObject.put("text", text);
        JSONUtils.put(jsonObject, "margin", margin);
        JSONUtils.put(jsonObject, "size", size != null ? size.getValue() : null);
        JSONUtils.put(jsonObject, "align", align);
        JSONUtils.put(jsonObject, "gravity", gravity);
        JSONUtils.put(jsonObject, "wrap", wrap);
        JSONUtils.put(jsonObject, "weight", weight);
        JSONUtils.put(jsonObject, "color", color);
        JSONUtils.put(jsonObject, "action", action);
        if (flex != FLEX_VALUE_NONE) {
            jsonObject.put("flex", flex);
        }
        if (maxLines != MAXLINES_VALUE_NONE) {
            jsonObject.put("maxLines", maxLines);
        }
        return jsonObject;
    }


    /**
     * {@code FlexTextComponent} builder static inner class.
     */
    public static final class Builder {
        @NonNull
        private String text;

        private int flex = FLEX_VALUE_NONE;

        @Nullable
        private Margin margin;

        @Nullable
        private Size size;

        @Nullable
        private Alignment align;

        @Nullable
        private Gravity gravity;

        @Nullable
        private Boolean wrap;

        private int maxLines = MAXLINES_VALUE_NONE;

        @Nullable
        private Weight weight;

        @Nullable
        private String color;

        @Nullable
        private Action action;

        private Builder() {
        }

        /**
         * to construct a {@code FlexTextComponent} builder with text field.
         *
         * @param text content text of this component.
         */
        public Builder(@NonNull String text) {
            this();
            this.text = text;
        }

        /**
         * Sets the {@code flex} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param flex the {@code flex} to set
         * @return a reference to this Builder
         */
        public Builder setFlex(int flex) {
            this.flex = flex;
            return this;
        }

        /**
         * Sets the {@code margin} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param margin the {@code margin} to set
         * @return a reference to this Builder
         */
        public Builder setMargin(@Nullable Margin margin) {
            this.margin = margin;
            return this;
        }

        /**
         * Sets the {@code size} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param size the {@code size} to set
         * @return a reference to this Builder
         */
        public Builder setSize(@Nullable Size size) {
            this.size = size;
            return this;
        }

        /**
         * Sets the {@code align} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param align the {@code align} to set
         * @return a reference to this Builder
         */
        public Builder setAlign(@Nullable Alignment align) {
            this.align = align;
            return this;
        }

        /**
         * Sets the {@code gravity} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param gravity the {@code gravity} to set
         * @return a reference to this Builder
         */
        public Builder setGravity(@Nullable Gravity gravity) {
            this.gravity = gravity;
            return this;
        }

        /**
         * Sets the {@code wrap} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param wrap the {@code wrap} to set
         * @return a reference to this Builder
         */
        public Builder setWrap(@Nullable Boolean wrap) {
            this.wrap = wrap;
            return this;
        }

        /**
         * Sets the {@code maxLines} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param maxLines the {@code maxLines} to set
         * @return a reference to this Builder
         */
        public Builder setMaxLines(int maxLines) {
            this.maxLines = maxLines;
            return this;
        }

        /**
         * Sets the {@code weight} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param weight the {@code weight} to set
         * @return a reference to this Builder
         */
        public Builder setWeight(@Nullable Weight weight) {
            this.weight = weight;
            return this;
        }

        /**
         * Sets the {@code color} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param color the {@code color} to set
         * @return a reference to this Builder
         */
        public Builder setColor(@Nullable String color) {
            this.color = color;
            return this;
        }

        /**
         * Sets the {@code action} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param action the {@code action} to set
         * @return a reference to this Builder
         */
        public Builder setAction(@Nullable Action action) {
            this.action = action;
            return this;
        }

        /**
         * Returns a {@code FlexTextComponent} built from the parameters previously set.
         *
         * @return a {@code FlexTextComponent} built with parameters of this {@code FlexTextComponent.Builder}
         */
        public FlexTextComponent build() {
            return new FlexTextComponent(this);
        }
    }
}
