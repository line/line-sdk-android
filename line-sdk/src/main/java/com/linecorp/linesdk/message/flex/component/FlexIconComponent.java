package com.linecorp.linesdk.message.flex.component;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.linecorp.linesdk.utils.JSONUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents an icon component. It is used to embed into a baseline layout and its flex is fixed to 0.
 */
public class FlexIconComponent extends FlexMessageComponent {

    /**
     * Image URL
     * Protocol: HTTPS
     * Image format: JPEG or PNG
     * Maximum image size: 240x240 pixels
     * Maximum data size: 1 MB
     */
    private String url;

    /**
     * Minimum space between this component and the previous component in the parent box.
     * You can specify one of the following values: none, xs, sm, md, lg, xl, or xxl in {@link Margin}.
     * none does not set a space while the other values set a space whose size increases
     * in the order of listing. The default value is the value of the spacing property of the parent box.
     * If this component is the first component in the parent box, the margin property will be ignored.
     */
    private Margin margin;

    /**
     * Maximum size of the icon width. You can specify one of the following values:
     * xxs, xs, sm, md, lg, xl, xxl, 3xl, 4xl, or 5xl in {@link Size}.
     * The size increases in the order of listing.
     * The default value is md.
     */
    private Size size;

    /**
     * Aspect ratio of the icon.
     * You can specify one of the following values: 1:1, 2:1, or 3:1 in {@link AspectRatio}.
     * The default value is 1:1.
     */
    private AspectRatio aspectRatio;

    private FlexIconComponent() {
        super(Type.ICON);
    }

    private FlexIconComponent(@NonNull Builder builder) {
        this();
        url = builder.url;
        margin = builder.margin;
        size = builder.size;
        aspectRatio = builder.aspectRatio;
    }

    public static Builder newBuilder(@NonNull String url) {
        return new Builder(url);
    }

    @NonNull
    @Override
    public JSONObject toJsonObject() throws JSONException {
        JSONObject jsonObject = super.toJsonObject();
        jsonObject.put("url", url);
        JSONUtils.put(jsonObject, "margin", margin);
        JSONUtils.put(jsonObject, "size", size);
        JSONUtils.put(jsonObject, "aspectRatio", aspectRatio != null ? aspectRatio.getValue() : null);
        return jsonObject;
    }

    /**
     * {@code FlexIconComponent} builder static inner class.
     */
    public static final class Builder {
        @NonNull
        private String url;

        @Nullable
        private Margin margin;

        @Nullable
        private Size size;

        @Nullable
        private AspectRatio aspectRatio;

        private Builder(@NonNull String url) {
            this.url = url;
        }

        /**
         * Sets the {@code margin} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param margin the {@code margin} to set
         * @return a reference to this Builder
         */
        public Builder setMargin(@Nullable  Margin margin) {
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
         * Returns a {@code FlexIconComponent} built from the parameters previously set.
         *
         * @return a {@code FlexIconComponent} built with parameters of this {@code FlexIconComponent.Builder}
         */
        public FlexIconComponent build() {
            return new FlexIconComponent(this);
        }
    }
}
