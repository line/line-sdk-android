package com.linecorp.linesdk.message.template;

import androidx.annotation.NonNull;

import com.linecorp.linesdk.utils.JSONUtils;
import com.linecorp.linesdk.message.Jsonable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * ImageCarouselLayoutTemplate is a class to represent Image carousels layout for TemplateMessage.
 * It can show carousels with images.
 */
public class ImageCarouselLayoutTemplate extends LayoutTemplate {
    @NonNull
    private List<ImageCarouselColumn> columns;

    public ImageCarouselLayoutTemplate(@NonNull List<ImageCarouselColumn> columns) {
        super(Type.IMAGE_CAROUSEL);
        this.columns = columns;
    }

    @Override
    public JSONObject toJsonObject() throws JSONException {
        JSONObject jsonObject = super.toJsonObject();
        JSONUtils.putArray(jsonObject, "columns", columns);
        return jsonObject;
    }

    public static class ImageCarouselColumn implements Jsonable {
        @NonNull
        private String imageUrl;
        @NonNull
        private ClickActionForTemplateMessage action;

        public ImageCarouselColumn(@NonNull String imageUrl, @NonNull ClickActionForTemplateMessage action) {
            this.imageUrl = imageUrl;
            this.action = action;
        }

        @NonNull
        @Override
        public JSONObject toJsonObject() throws JSONException {
            JSONObject jsonObject = new JSONObject();
            JSONUtils.put(jsonObject, "imageUrl", imageUrl);
            JSONUtils.put(jsonObject, "action", action);
            return jsonObject;
        }
    }
}
