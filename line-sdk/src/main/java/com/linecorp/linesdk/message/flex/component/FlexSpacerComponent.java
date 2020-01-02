package com.linecorp.linesdk.message.flex.component;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.linecorp.linesdk.utils.JSONUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents some spacing in a box component.
 * This is an invisible component that places a fixed-size space at the beginning or end of the box.
 * The spacing property of the parent box will be ignored for spacers.
 */
public class FlexSpacerComponent extends FlexMessageComponent {

    /**
     * Size of the space. You can specify one of the following values: xs, sm, md, lg, xl, or xxl.
     * The size increases in the order of listing. The default value is md.
     */
    @Nullable
    private Size size;

    public FlexSpacerComponent() {
        super(Type.SPACER);
    }

    public void setSize(@Nullable Size size) {
        this.size = size;
    }

    @NonNull
    @Override
    public JSONObject toJsonObject() throws JSONException {
        JSONObject jsonObject = super.toJsonObject();
        JSONUtils.put(jsonObject, "size", size);
        return jsonObject;
    }
}
