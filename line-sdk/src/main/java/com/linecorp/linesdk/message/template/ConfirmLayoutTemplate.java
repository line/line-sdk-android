package com.linecorp.linesdk.message.template;

import androidx.annotation.NonNull;
import androidx.annotation.Size;

import com.linecorp.linesdk.utils.JSONUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * ConfirmLayoutTemplate is a class to represent Confirm layout template in TemplateMessage.
 * It needs to include two actions when constructing the object
 */
public class ConfirmLayoutTemplate extends LayoutTemplate {
    @NonNull
    private String text; // max 240
    @NonNull
    private List<ClickActionForTemplateMessage> actions;

    /**
     * constructor to create a new ConfirmLayoutTemplate.
     * @param text confirmation description
     * @param actions what actions to take for the two buttons
     */
    public ConfirmLayoutTemplate(
            @NonNull String text,
            @NonNull @Size(2) List<ClickActionForTemplateMessage> actions
    ) {
        super(Type.CONFIRM);
        this.text = text;
        this.actions = actions;
    }

    @NonNull
    @Override
    public JSONObject toJsonObject() throws JSONException {
        JSONObject jsonObject = super.toJsonObject();
        JSONUtils.put(jsonObject, "text", text);
        JSONUtils.putArray(jsonObject, "actions", actions);
        return jsonObject;
    }
}
