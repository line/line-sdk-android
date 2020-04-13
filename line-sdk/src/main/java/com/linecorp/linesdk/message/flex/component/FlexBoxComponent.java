package com.linecorp.linesdk.message.flex.component;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.linecorp.linesdk.message.flex.action.Action;
import com.linecorp.linesdk.utils.JSONUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Represents a box component in a flex message.
 * A box component behave as a container of other components. It defines the layout of its child components.
 * You can also include a nested box in a box.
 * <p>
 * For more information, please refer to <a href=https://developers.line.biz/en/reference/messaging-api/#box>box</a>
 */
public class FlexBoxComponent extends FlexMessageComponent {

    /**
     * The placement style of components in this box.
     */
    @NonNull
    private Layout layout;

    /**
     * Components in this box.
     * <p>
     * When the {@link FlexBoxComponent#layout} is {@link Layout#HORIZONTAL} or {@link Layout#VERTICAL}, the following components are supported as nested:
     * <li> {@link FlexBoxComponent}</li>
     * <li> {@link FlexTextComponent}</li>
     * <li> {@link FlexImageComponent}</li>
     * <li> {@link FlexButtonComponent}</li>
     * <li> {@link FlexFillerComponent}</li>
     * <li> {@link FlexSeparatorComponent}</li>
     * <li> {@link FlexSpacerComponent}</li>
     * <p>
     * When the {@link FlexBoxComponent#layout} is {@link Layout#BASELINE}, the following components are supported as nested:
     * <li> {@link FlexTextComponent}</li>
     * <li> {@link FlexFillerComponent}</li>
     * <li> {@link FlexIconComponent}</li>
     * <li> {@link FlexSpacerComponent}</li>
     * <p>
     * LineSDK does not check the validation of contents for a certain layout. However, it might cause a response error
     * if you try to send a message with invalid component {@link FlexBoxComponent#contents}
     */
    @NonNull
    private List<FlexMessageComponent> contents;

    /**
     * The ratio of the width or height of this box within the parent box. The default value for the horizontal parent box is 1, and the default value for the vertical parent box is 0.
     * <p>
     * <a href=https://developers.line.biz/en/docs/messaging-api/flex-message-layout/#component-width-and-height>component-width-and-height</a>
     */
    @Nullable
    private int flex;

    /**
     * Optional. Minimum space between components in this box.
     */
    @Nullable
    private Margin spacing;

    /**
     * Optional. Minimum space between this box and the previous component in the parent box.
     */
    @Nullable
    private Margin margin;

    /**
     * FIXME: add action object instead of ClickActionForTemplateMessage
     * Optional. Action performed when this box is tapped. Specify an action object.
     * This property is supported on the following versions of LINE.
     * <li> LINE for iOS and Android: 8.11.0 and later</li>
     * <li> LINE for Windows and macOS: 5.9.0 and later</li>
     */
    @Nullable
    private Action action;

    private FlexBoxComponent() {
        super(Type.BOX);
    }

    private FlexBoxComponent(@NonNull Builder builder) {
        this();
        layout = builder.layout;
        contents = builder.contents;
        flex = builder.flex;
        spacing = builder.spacing;
        margin = builder.margin;
        action = builder.action;
    }

    public static Builder newBuilder(@NonNull Layout layout,
                                     @NonNull List<FlexMessageComponent> contents) {
        return new Builder(layout, contents);
    }

    @NonNull
    @Override
    public JSONObject toJsonObject() throws JSONException {
        JSONObject jsonObject = super.toJsonObject();
        JSONUtils.put(jsonObject, "layout", layout);
        JSONUtils.putArray(jsonObject, "contents", contents);
        JSONUtils.put(jsonObject, "spacing", spacing);
        JSONUtils.put(jsonObject, "margin", margin);
        JSONUtils.put(jsonObject, "action", action);
        if (flex != FLEX_VALUE_NONE) {
            jsonObject.put("flex", flex);
        }
        return jsonObject;
    }


    /**
     * {@code FlexBoxComponent} builder static inner class.
     */
    public static final class Builder {
        @NonNull
        private Layout layout;

        @NonNull
        private List<FlexMessageComponent> contents;

        private int flex = FLEX_VALUE_NONE;

        @Nullable
        private Margin spacing;

        @Nullable
        private Margin margin;

        @Nullable
        private Action action;

        private Builder(@NonNull Layout layout,
                        @NonNull List<FlexMessageComponent> contents) {
            this.layout = layout;
            this.contents = contents;
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
         * Sets the {@code spacing} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param spacing the {@code spacing} to set
         * @return a reference to this Builder
         */
        public Builder setSpacing(@Nullable Margin spacing) {
            this.spacing = spacing;
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
         * Returns a {@code FlexBoxComponent} built from the parameters previously set.
         *
         * @return a {@code FlexBoxComponent} built with parameters of this {@code FlexBoxComponent.Builder}
         */
        public FlexBoxComponent build() {
            return new FlexBoxComponent(this);
        }
    }
}
