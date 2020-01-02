package com.linecorp.linesdk.message.template;

import android.graphics.Color;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.linecorp.linesdk.utils.JSONUtils;
import com.linecorp.linesdk.message.MessageSender;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * ButtonsLayoutTemplate is a template type used in TemplateMessage.
 * It can have multiple actions.
 */
public class ButtonsLayoutTemplate extends LayoutTemplate {

    @Nullable
    private String thumbnailImageUrl;
    @NonNull
    private ImageAspectRatio imageAspectRatio = ImageAspectRatio.RECTANGLE;
    @NonNull
    private ImageScaleType imageScaleType = ImageScaleType.COVER;
    @ColorInt
    private int imageBackgroundColor = Color.WHITE;
    @Nullable
    private String title; // max 40
    @NonNull
    private String text; // 160 without image and title, 60 with image or title
    @Nullable
    private ClickActionForTemplateMessage defaultAction;
    @NonNull
    private List<ClickActionForTemplateMessage> actions;
    @Nullable
    private MessageSender messageSender;

    /**
     * Constructor to create ButtonsLayoutTemplate
     * @param text description for the button
     * @param actions a list of actions for the button
     */
    public ButtonsLayoutTemplate(
            @NonNull String text,
            @NonNull List<ClickActionForTemplateMessage> actions
    ) {
        super(Type.BUTTONS);
        this.text = text;
        this.actions = actions;
    }

    /**
     * Set thumbnail image url.
     * @param thumbnailImageUrl
     */
    public void setThumbnailImageUrl(@Nullable String thumbnailImageUrl) {
        this.thumbnailImageUrl = thumbnailImageUrl;
    }

    /**
     * Set image aspect ratio.
     * @param imageAspectRatio available values are rectangle (1.51:1) and square (1:1)
     */
    public void setImageAspectRatio(@NonNull ImageAspectRatio imageAspectRatio) {
        this.imageAspectRatio = imageAspectRatio;
    }

    /**
     * Set image scale type.
     * @param imageScaleType
     */
    public void setImageScaleType(@NonNull ImageScaleType imageScaleType) {
        this.imageScaleType = imageScaleType;
    }

    /**
     * Set image background color.
     * @param imageBackgroundColor
     */
    public void setImageBackgroundColor(@ColorInt int imageBackgroundColor) {
        this.imageBackgroundColor = imageBackgroundColor;
    }

    /**
     * Set button title.
     * @param title
     */
    public void setTitle(@Nullable String title) {
        this.title = title;
    }

    /**
     * Set default action for button
     * @param defaultAction
     */
    public void setDefaultAction(@Nullable ClickActionForTemplateMessage defaultAction) {
        this.defaultAction = defaultAction;
    }

    /**
     * Set message sender
     * @param messageSender
     */
    public void setMessageSender(@Nullable MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    @NonNull
    @Override
    public JSONObject toJsonObject() throws JSONException {
        JSONObject jsonObject = super.toJsonObject();
        JSONUtils.put(jsonObject, "text", text);
        JSONUtils.put(jsonObject, "thumbnailImageUrl", thumbnailImageUrl);
        JSONUtils.put(jsonObject, "imageAspectRatio", imageAspectRatio.getServerKey());
        JSONUtils.put(jsonObject, "imageSize", imageScaleType.getServerKey());
        JSONUtils.put(jsonObject, "imageBackgroundColor", getColorString(imageBackgroundColor));
        JSONUtils.put(jsonObject, "title", title);
        JSONUtils.put(jsonObject, "defaultAction", defaultAction);
        JSONUtils.put(jsonObject, "sentBy", messageSender);
        JSONUtils.putArray(jsonObject, "actions", actions);
        return jsonObject;
    }

    /**
     * remove color alpha value
     */
    @NonNull
    private String getColorString(@ColorInt int color) {
        return String.format("#%06X", 0xFFFFFF & color);
    }
}
