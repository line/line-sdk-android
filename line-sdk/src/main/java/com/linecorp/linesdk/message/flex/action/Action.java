package com.linecorp.linesdk.message.flex.action;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.linecorp.linesdk.message.Jsonable;
import com.linecorp.linesdk.message.Stringable;
import com.linecorp.linesdk.utils.JSONUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * These are types of actions for your bot to take when a user taps a button or an image in a message.
 * <p>
 * <li> Postback action</li>
 * <li> Message action</li>
 * <li> URI action</li>
 * <li> Datetime picker action</li>
 * <li> Camera action</li>
 * <li> Camera roll action</li>
 * <li> Location action</li>
 *
 * @see <a href=https://developers.line.biz/en/reference/messaging-api/#action-objects>Action objects</a>
 */
public abstract class Action implements Jsonable {

    public enum Type implements Stringable {
        POSTBACK,
        MESSAGE,
        URI,
        DATETIMEPICKER,
        CAMERA,
        CAMERAROLL,
        LOCATION
    }

    /**
     * Required. indicate {@link Type} of action
     */
    @NonNull
    final protected Type type;

    /**
     * Label for the action
     */
    @Nullable
    protected String label;

    public Action(@NonNull Type type, @Nullable String label) {
        this.type = type;
        this.label = label;
    }

    public Action(@NonNull Type type) {
        this(type, null);
    }

    @NonNull
    @Override
    public JSONObject toJsonObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", type.name().toLowerCase());
        JSONUtils.put(jsonObject, "label", label);
        return jsonObject;
    }
}
