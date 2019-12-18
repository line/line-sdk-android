package com.linecorp.linesdk.message.flex.component;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.linecorp.linesdk.message.flex.style.FlexBlockStyle;
import com.linecorp.linesdk.utils.JSONUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represent a separator component.
 * This component draws a separator between components in the parent box.
 * Different from the `separator` property of {@link FlexBlockStyle}, the {@link FlexSeparatorComponent} allows you to add a separator between components instead of container block, as well as full control on separator {@link FlexSeparatorComponent#margin}.
 */
public class FlexSeparatorComponent extends FlexMessageComponent {

    /**
     * Minimum space between this component and the previous component in the parent box.
     * You can specify one of the following values: none, xs, sm, md, lg, xl, or xxl.
     * none does not set a space while the other values set a space whose size increases in the order of listing.
     * The default value is the value of the spacing property of the parent box.
     * If this component is the first component in the parent box, the margin property will be ignored.
     */
    @Nullable
    private Margin margin;

    /**
     * Color of the separator. Use a hexadecimal color code.
     */
    @Nullable
    private String color;

    public FlexSeparatorComponent() {
        super(Type.SEPARATOR);
    }

    private FlexSeparatorComponent(@NonNull Builder builder) {
        this();
        margin = builder.margin;
        color = builder.color;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @NonNull
    @Override
    public JSONObject toJsonObject() throws JSONException {
        JSONObject jsonObject = super.toJsonObject();
        JSONUtils.put(jsonObject, "margin", margin);
        JSONUtils.put(jsonObject, "color", color);
        return jsonObject;
    }

    /**
     * {@code FlexSeparatorComponent} builder static inner class.
     */
    public static final class Builder {
        @Nullable
        private Margin margin;

        @Nullable
        private String color;

        private Builder() {
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
         * Returns a {@code FlexSeparatorComponent} built from the parameters previously set.
         *
         * @return a {@code FlexSeparatorComponent} built with parameters of this {@code FlexSeparatorComponent.Builder}
         */
        public FlexSeparatorComponent build() {
            return new FlexSeparatorComponent(this);
        }
    }
}
