package com.linecorp.linesdk.message.flex.container;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.linecorp.linesdk.message.Jsonable;
import com.linecorp.linesdk.message.flex.component.FlexBoxComponent;
import com.linecorp.linesdk.message.flex.component.FlexImageComponent;
import com.linecorp.linesdk.message.flex.style.FlexBlockStyle;
import com.linecorp.linesdk.utils.JSONUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents a container that contains one message bubble. It can contain four blocks:
 * header, hero, body, and footer. These blocks, which could contain nested components,
 * will follow some given {@link FlexBubbleContainer#styles} to construct the flexible layout.
 *
 * @see <a href="https://developers.line.biz/en/reference/messaging-api/#bubble">Bubble Container</a>
 */
public class FlexBubbleContainer extends FlexMessageContainer {

    /**
     * Represents the text direction inside a bubble.
     * <p>
     * <li> leftToRight: The text should be from left to right.</li>
     * <li> rightToLeft: The text should be from right to left.</li>
     */
    public enum Direction {
        LEFT_TO_RIGHT("ltr"),
        RIGHT_TO_LEFT("rtl");

        private String value;

        Direction(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * The style used for a bubble container.
     */
    public static class Style implements Jsonable {
        /**
         * Optional. Style of the header block.
         */
        private FlexBlockStyle header;

        /**
         * Optional. Style of the hero block.
         */
        private FlexBlockStyle hero;

        /**
         * Optional. Style of the body block.
         */
        private FlexBlockStyle body;

        /**
         * Optional. Style of the footer block.
         */
        private FlexBlockStyle footer;

        @NonNull
        @Override
        public JSONObject toJsonObject() throws JSONException {
            JSONObject jsonObject = new JSONObject();
            JSONUtils.put(jsonObject, "header", header);
            JSONUtils.put(jsonObject, "hero", hero);
            JSONUtils.put(jsonObject, "body", body);
            JSONUtils.put(jsonObject, "footer", footer);
            return jsonObject;
        }
    }

    /**
     * Optional. Text directionality and the order of components in horizontal boxes in the container.
     * If not specified, {@link Direction#LEFT_TO_RIGHT} will be used.
     */
    @Nullable
    private Direction direction = Direction.LEFT_TO_RIGHT;

    /**
     * Optional. The header block. Header section of the bubble.
     * This block is a @see {@link FlexBoxComponent} and could contain arbitrary nested components.
     */
    @Nullable
    private FlexBoxComponent header;

    /**
     * Optional. The hero block. Hero block is a {@link FlexImageComponent} which show an image inside the bubble.
     */
    @Nullable
    private FlexImageComponent hero;

    /**
     * Optional. The body block. Main content of the bubble.
     * This block is a {@link FlexBoxComponent} and could contain arbitrary nested components.
     */
    @Nullable
    private FlexBoxComponent body;

    /**
     * Optional. The footer block. Footer section of the bubble.
     * This block is a {@link FlexBoxComponent} and and could contain arbitrary nested components.
     */
    @Nullable
    private FlexBoxComponent footer;

    /**
     * Optional. TThe styles used for this bubble container.
     */
    @Nullable
    private Style styles;

    private FlexBubbleContainer() {
        super(Type.BUBBLE);
    }

    private FlexBubbleContainer(Builder builder) {
        this();
        direction = builder.direction;
        header = builder.header;
        hero = builder.hero;
        body = builder.body;
        footer = builder.footer;
        styles = builder.styles;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @NonNull
    @Override
    public JSONObject toJsonObject() throws JSONException {
        JSONObject jsonObject = super.toJsonObject();
        JSONUtils.put(jsonObject, "direction", (direction != null ? direction.getValue() : direction));
        JSONUtils.put(jsonObject, "header", header);
        JSONUtils.put(jsonObject, "hero", hero);
        JSONUtils.put(jsonObject, "body", body);
        JSONUtils.put(jsonObject, "footer", footer);
        JSONUtils.put(jsonObject, "styles", styles);
        return jsonObject;
    }


    /**
     * {@code FlexBubbleContainer} builder static inner class.
     */
    public static final class Builder {
        private Direction direction;
        private FlexBoxComponent header;
        private FlexImageComponent hero;
        private FlexBoxComponent body;
        private FlexBoxComponent footer;
        private Style styles;

        private Builder() {
        }

        /**
         * Sets the {@code direction} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param direction the {@code direction} to set
         * @return a reference to this Builder
         */
        public Builder setDirection(Direction direction) {
            this.direction = direction;
            return this;
        }

        /**
         * Sets the {@code header} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param header the {@code header} to set
         * @return a reference to this Builder
         */
        public Builder setHeader(FlexBoxComponent header) {
            this.header = header;
            return this;
        }

        /**
         * Sets the {@code hero} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param hero the {@code hero} to set
         * @return a reference to this Builder
         */
        public Builder setHero(FlexImageComponent hero) {
            this.hero = hero;
            return this;
        }

        /**
         * Sets the {@code body} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param body the {@code body} to set
         * @return a reference to this Builder
         */
        public Builder setBody(FlexBoxComponent body) {
            this.body = body;
            return this;
        }

        /**
         * Sets the {@code footer} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param footer the {@code footer} to set
         * @return a reference to this Builder
         */
        public Builder setFooter(FlexBoxComponent footer) {
            this.footer = footer;
            return this;
        }

        /**
         * Sets the {@code styles} and returns a reference to this Builder so that the methods can be chained together.
         *
         * @param styles the {@code styles} to set
         * @return a reference to this Builder
         */
        public Builder setStyles(Style styles) {
            this.styles = styles;
            return this;
        }

        /**
         * Returns a {@code FlexBubbleContainer} built from the parameters previously set.
         *
         * @return a {@code FlexBubbleContainer} built with parameters of this {@code FlexBubbleContainer.Builder}
         */
        public FlexBubbleContainer build() {
            return new FlexBubbleContainer(this);
        }
    }
}
