package com.linecorp.linesdk.message.flex.style;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.linecorp.linesdk.message.Jsonable;
import com.linecorp.linesdk.utils.JSONUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents a style for a block in a flex message.
 */
public class FlexBlockStyle implements Jsonable {

    /**
     * Optional. Background color of the block. Use a hexadecimal color code.
     */
    @Nullable
    private String backgroundColor;

    /**
     * Optional. Whether a separator should be placed above the block.
     * `true` to place a separator above the block. `true` will be ignored for the first block in a container because you cannot place a separator above the first block. The default value is `false`.
     */
    @Nullable
    private boolean separator;

    /**
     * Optional. Color of the separator. Use a hexadecimal color code.
     */
    @Nullable
    private String separatorColor;

    public FlexBlockStyle(@Nullable String backgroundColor,
                          @Nullable boolean separator,
                          @Nullable String separatorColor) {
        this.backgroundColor = backgroundColor;
        this.separator = separator;
        this.separatorColor = separatorColor;
    }

    @NonNull
    @Override
    public JSONObject toJsonObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        JSONUtils.put(jsonObject, "backgroundColor", backgroundColor);
        JSONUtils.put(jsonObject, "separator", separator);
        JSONUtils.put(jsonObject, "separatorColor", separatorColor);
        return jsonObject;
    }
}
