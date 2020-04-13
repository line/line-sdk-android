package com.linecorp.linesdk.message.flex.component;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.linecorp.linesdk.message.flex.action.Action;
import com.linecorp.linesdk.utils.JSONUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents a button component in a flex message.
 * A button component contains a interactive button. When the user taps the button, a bound action is performed.
 *
 * @see <a href="https://developers.line.biz/en/reference/messaging-api/#button">Button component</a>
 */
public class FlexButtonComponent extends FlexMessageComponent {

    /**
     * Action performed when this button is tapped. Specify an {@link Action} object.
     */
    @NonNull
    private Action action;

    /**
     * Optional. The ratio of the width or height of this component within the parent box.
     * The default value for the horizontal parent box is 1, and the default value for the vertical parent box is 0.
     */
    private int flex;

    /**
     * Optional. Minimum space between this component and the previous component in the parent box.
     * The default value is the value of the <code>spacing</code> property of the parent box.
     * If this component is the first component in the parent box, this <code>margin</code> property will be ignored.
     *
     * @see {@link Margin}
     */
    @Nullable
    private Margin margin;

    /**
     * Optional. Height of the button. You can specify <code>sm</code> or <code>md</code>.
     * The default value is <code>md</code>.
     *
     * @see {@link Height}
     */
    @Nullable
    private Height height;

    /**
     * Optional. Style of the button. Specify one of the following values:
     * <li> <code>link</code>: HTML link style</li>
     * <li> <code>primary</code>: Style for dark color buttons</li>
     * <li> <code>secondary</code>: Style for light color buttons</li>
     * The default value is link.
     *
     * @see {@link Style}
     */
    @Nullable
    private Style style;

    /**
     * Optional. Character color when the style property is <code>link</code>.
     * Background color when the style property is <code>primary</code> or <code>secondary</code>.
     * Use a hexadecimal color code.
     */
    @Nullable
    private String color;

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

    private FlexButtonComponent() {
        super(Type.BUTTON);
    }

    private FlexButtonComponent(@NonNull Builder builder) {
        this();
        action = builder.action;
        flex = builder.flex;
        margin = builder.margin;
        height = builder.height;
        style = builder.style;
        color = builder.color;
        gravity = builder.gravity;
    }

    public static Builder newBuilder(@NonNull Action action) {
        return new Builder(action);
    }

    @NonNull
    @Override
    public JSONObject toJsonObject() throws JSONException {
        JSONObject jsonObject = super.toJsonObject();
        JSONUtils.put(jsonObject, "action", action);
        JSONUtils.put(jsonObject, "margin", margin);
        JSONUtils.put(jsonObject, "height", height);
        JSONUtils.put(jsonObject, "style", style);
        JSONUtils.put(jsonObject, "color", color);
        JSONUtils.put(jsonObject, "gravity", gravity);
        if (flex != FLEX_VALUE_NONE) {
            jsonObject.put("flex", flex);
        }
        return jsonObject;
    }

    /**
     * {@code FlexButtonComponent} builder static inner class.
     */
    public static final class Builder {
        @NonNull
        private Action action;

        private int flex = FLEX_VALUE_NONE;

        @Nullable
        private Margin margin;

        @Nullable
        private Height height;

        @Nullable
        private Style style;

        @Nullable
        private String color;

        @Nullable
        private Gravity gravity;

        private Builder(@NonNull Action action) {
            this.action = action;
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
         * Sets the {@code height} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param height the {@code height} to set
         * @return a reference to this Builder
         */
        public Builder setHeight(@Nullable Height height) {
            this.height = height;
            return this;
        }

        /**
         * Sets the {@code style} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param style the {@code style} to set
         * @return a reference to this Builder
         */
        public Builder setStyle(@Nullable Style style) {
            this.style = style;
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
         * Returns a {@code FlexButtonComponent} built from the parameters previously set.
         *
         * @return a {@code FlexButtonComponent} built with parameters of this {@code FlexButtonComponent.Builder}
         */
        public FlexButtonComponent build() {
            return new FlexButtonComponent(this);
        }
    }
}
