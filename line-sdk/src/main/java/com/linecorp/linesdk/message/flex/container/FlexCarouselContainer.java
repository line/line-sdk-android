package com.linecorp.linesdk.message.flex.container;

import androidx.annotation.NonNull;

import com.linecorp.linesdk.utils.JSONUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Represents a container that contains multiple bubble containers.
 * The bubbles will be shown in order by scrolling horizontally.
 * <p>
 * The maximum size of the carousel container is 50 KB.
 * The total size of the bubble containers in the carousel container must be within 50 KB.
 */
public class FlexCarouselContainer extends FlexMessageContainer {

    /**
     * List of {@link FlexBubbleContainer}s.
     * You could set at most 10 bubble container in this carousel container.
     * Line SDK does not check the elements count in a container.
     * However, it would cause an API response error if more bubbles contained in the container.
     */
    @NonNull
    private List<FlexBubbleContainer> contents;

    private FlexCarouselContainer() {
        super(Type.CAROUSEL);
    }

    /**
     * Creates a carousel container with given information
     *
     * @param contents Bubble containers which consist this carousel container.
     */
    public FlexCarouselContainer(@NonNull List<FlexBubbleContainer> contents) {
        this();
        this.contents = contents;
    }

    @NonNull
    @Override
    public JSONObject toJsonObject() throws JSONException {
        JSONObject jsonObject = super.toJsonObject();
        JSONUtils.putArray(jsonObject, "contents", contents);
        return jsonObject;
    }
}
