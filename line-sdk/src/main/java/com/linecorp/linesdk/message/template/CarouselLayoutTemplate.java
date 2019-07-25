package com.linecorp.linesdk.message.template;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.linecorp.linesdk.utils.JSONUtils;
import com.linecorp.linesdk.message.Jsonable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * CarouselLayoutTemplate is a class to represent Carousel layout in TemplateMessage.
 */
public class CarouselLayoutTemplate extends LayoutTemplate {
    @NonNull
    private List<CarouselColumn> columns;
    @Nullable
    private ImageAspectRatio imageAspectRatio = ImageAspectRatio.RECTANGLE;
    @Nullable
    private ImageScaleType imageScaleType = ImageScaleType.COVER;

    /**
     * constructor to create CarouselLayoutTemplate
     * @param columns a list of CarouselColumn
     */
    public CarouselLayoutTemplate(@NonNull List<CarouselColumn> columns) {
        super(Type.CAROUSEL);
        this.columns = columns;
    }


    /**
     * Set image aspect ratio.
     * @param imageAspectRatio available values are rectangle (1.51:1) and square (1:1)
     */
    public void setImageAspectRatio(@Nullable ImageAspectRatio imageAspectRatio) {
        this.imageAspectRatio = imageAspectRatio;
    }

    /**
     * Set image scale type.
     * @param imageScaleType
     */
    public void setImageScaleType(@Nullable ImageScaleType imageScaleType) {
        this.imageScaleType = imageScaleType;
    }

    @NonNull
    @Override
    public JSONObject toJsonObject() throws JSONException {
        JSONObject jsonObject = super.toJsonObject();
        JSONUtils.putArray(jsonObject, "columns", columns);
        JSONUtils.putArray(jsonObject, "columns", columns);
        JSONUtils.put(jsonObject, "imageAspectRatio", imageAspectRatio.getServerKey());
        JSONUtils.put(jsonObject, "imageSize", imageScaleType.getServerKey());
        return jsonObject;
    }

    /**
     * This is a class to present every column in a CarouselLayout
     */
    public static class CarouselColumn implements Jsonable {
        @Nullable
        private String thumbnailImageUrl;
        @Nullable
        private String imageBackgroundColor; // default: #FFFFFF
        @Nullable
        private String title; // max 40
        @NonNull
        private String text; // 160 without image and title, 60 with image or title
        @Nullable
        private ClickActionForTemplateMessage defaultAction;
        @NonNull
        private List<ClickActionForTemplateMessage> actions; // max 3

        /**
         * constructor to create CarouselColumn
         * @param text description for the column
         * @param actions action for the column
         */
        public CarouselColumn(
                @NonNull String text,
                @NonNull List<ClickActionForTemplateMessage> actions
        ) {
            this.text = text;
            this.actions = actions;
        }

        /**
         * Set thumbnail image url if exists.
         * @param thumbnailImageUrl
         */
        public void setThumbnailImageUrl(@Nullable String thumbnailImageUrl) {
            this.thumbnailImageUrl = thumbnailImageUrl;
        }

        /**
         * Set image background color.
         * @param imageBackgroundColor
         */
        public void setImageBackgroundColor(@Nullable String imageBackgroundColor) {
            this.imageBackgroundColor = imageBackgroundColor;
        }

        /**
         * Set column title.
         * @param title
         */
        public void setTitle(@Nullable String title) {
            this.title = title;
        }

        /**
         * Set default column action.
         * @param defaultAction
         */
        public void setDefaultAction(@Nullable ClickActionForTemplateMessage defaultAction) {
            this.defaultAction = defaultAction;
        }

        @NonNull
        @Override
        public JSONObject toJsonObject() throws JSONException {
            JSONObject jsonObject = new JSONObject();
            JSONUtils.put(jsonObject, "text", text);
            JSONUtils.putArray(jsonObject, "actions", actions);
            JSONUtils.put(jsonObject, "thumbnailImageUrl", thumbnailImageUrl);
            JSONUtils.put(jsonObject, "imageBackgroundColor", imageBackgroundColor);
            JSONUtils.put(jsonObject, "title", title);
            JSONUtils.put(jsonObject, "defaultAction", defaultAction);
            return jsonObject;
        }
    }
}
