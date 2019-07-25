package com.linecorp.linesdk.message.flex.container;

import androidx.annotation.NonNull;

import com.linecorp.linesdk.message.Jsonable;
import com.linecorp.linesdk.message.Stringable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents a flex message container which acts as the content of a {@link com.linecorp.linesdk.message.FlexMessage}.
 * <p>
 * <li> bubble: Represents the type of bubble container. A {@link FlexBubbleContainer} value is associated.</li>
 * <li> carousel: Represents the type of carousel container. A {@link FlexCarouselContainer} value is associated.</li>
 */
public abstract class FlexMessageContainer implements Jsonable {
    public enum Type implements Stringable {
        BUBBLE,
        CAROUSEL
    }

    final protected Type type;

    public FlexMessageContainer(@NonNull Type type) {
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
