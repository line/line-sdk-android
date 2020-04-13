package com.linecorp.linesdk.message.flex.component;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.linecorp.linesdk.message.flex.action.Action;
import com.linecorp.linesdk.utils.JSONUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents an image component in a flex message.
 *
 * @see <a href=https://developers.line.biz/en/reference/messaging-api/#f-image>FlexImageComponent</a>
 */

public class FlexImageComponent extends FlexMessageComponent {

    /**
     * Required. Image URL.
     * Protocol: HTTPS
     * Image format: JPEG or PNG
     * Maximum image size: 1024x1024 pixels
     * Maximum data size: 1 MB
     */
    @NonNull
    private String url;

    /**
     * Optional. The ratio of the width or height of this box within the parent box. The default
     * value for the horizontal parent box is 1, and the default value for the vertical parent box is 0.
     * @see <a href="https://developers.line.biz/en/docs/messaging-api/flex-message-layout/#component-width-and-height">component-width-and-height</a>
     */
    @Nullable
    private int flex;

    /**
     * Optional. Minimum space between this component and the previous component in the parent box.
     * Specify a {@link Margin}
     */
    @Nullable
    private Margin margin;

    /**
     * Optional. Horizontal alignment style. Specify one of the following values:
     * <li> start: Left-aligned</li>
     * <li> end: Right-aligned</li>
     * <li> center: Center-aligned</li>
     * The default value is center.
     * Specify an {@link Alignment}
     */
    @Nullable
    private Alignment align = Alignment.CENTER;

    /**
     * Optional. Vertical alignment style. Specify one of the following values:
     * <li> top: Top-aligned</li>
     * <li> bottom: Bottom-aligned</li>
     * <li> center: Center-aligned</li>
     * The default value is top.
     * If the layout property of the parent box is baseline, the gravity property will be ignored.
     * Specify a {@link Gravity}
     */
    @Nullable
    private Gravity gravity = Gravity.TOP;

    /**
     * Optional. Maximum size of the image width. If not specified, `.md` will be used. Specify a {@link Size}
     */
    @Nullable
    private Size size;

    /**
     * Optional. Aspect ratio for the image. Width versus height. If not specified, `.ratio_1x1` will be used.
     * Specify an {@link AspectRatio}
     */
    @Nullable
    private AspectRatio aspectRatio;

    /**
     * Optional. Aspect scaling mode for the image. If not specified, `.fit` will be used.
     * Specify an {@link AspectMode}
     */
    @Nullable
    private AspectMode aspectMode;

    /**
     * Optional. Background color of the image.
     */
    @Nullable
    private String backgroundColor;

    /**
     * Optional. An action to perform when the box tapped. Specify an {@link Action}
     */
    @Nullable
    private Action action;

    private FlexImageComponent() {
        super(Type.IMAGE);
    }

    private FlexImageComponent(@NonNull Builder builder) {
        this();
        url = builder.url;
        flex = builder.flex;
        margin = builder.margin;
        align = builder.align;
        gravity = builder.gravity;
        size = builder.size;
        aspectRatio = builder.aspectRatio;
        aspectMode = builder.aspectMode;
        backgroundColor = builder.backgroundColor;
        action = builder.action;
    }

    public static Builder newBuilder(@NonNull String url) {
        return new Builder(url);
    }

    @NonNull
    @Override
    public JSONObject toJsonObject() throws JSONException {
        JSONObject jsonObject = super.toJsonObject();
        jsonObject.put("url", url);
        if (flex != FLEX_VALUE_NONE) {
            jsonObject.put("flex", flex);
        }
        JSONUtils.put(jsonObject, "margin", margin);
        JSONUtils.put(jsonObject, "align", align);
        JSONUtils.put(jsonObject, "gravity", gravity);
        JSONUtils.put(jsonObject, "size", size != null ? size.getValue() : null);
        JSONUtils.put(jsonObject, "aspectRatio", aspectRatio != null ? aspectRatio.getValue() : null);
        JSONUtils.put(jsonObject, "aspectMode", aspectMode);
        JSONUtils.put(jsonObject, "backgroundColor", backgroundColor);
        JSONUtils.put(jsonObject, "action", action);
        return jsonObject;
    }


    /**
     * {@code FlexImageComponent} builder static inner class.
     */
    public static final class Builder {
        @NonNull
        private String url;

        private int flex = FLEX_VALUE_NONE;

        @Nullable
        private Margin margin;

        @Nullable
        private Alignment align;

        @Nullable
        private Gravity gravity;

        @Nullable
        private Size size;

        @Nullable
        private AspectRatio aspectRatio;

        @Nullable
        private AspectMode aspectMode;

        @Nullable
        private String backgroundColor;

        @Nullable
        private Action action;

        private Builder(@NonNull String url) {
            this.url = url;
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
         * Sets the {@code aspectRatio} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param aspectRatio the {@code aspectRatio} to set
         * @return a reference to this Builder
         */
        public Builder setAspectRatio(@Nullable AspectRatio aspectRatio) {
            this.aspectRatio = aspectRatio;
            return this;
        }

        /**
         * Sets the {@code aspectMode} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param aspectMode the {@code aspectMode} to set
         * @return a reference to this Builder
         */
        public Builder setAspectMode(@Nullable AspectMode aspectMode) {
            this.aspectMode = aspectMode;
            return this;
        }

        /**
         * Sets the {@code backgroundColor} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param backgroundColor the {@code backgroundColor} to set
         * @return a reference to this Builder
         */
        public Builder setBackgroundColor(@Nullable String backgroundColor) {
            this.backgroundColor = backgroundColor;
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
         * Returns a {@code FlexImageComponent} built from the parameters previously set.
         *
         * @return a {@code FlexImageComponent} built with parameters of this {@code FlexImageComponent.Builder}
         */
        public FlexImageComponent build() {
            return new FlexImageComponent(this);
        }
    }
}
