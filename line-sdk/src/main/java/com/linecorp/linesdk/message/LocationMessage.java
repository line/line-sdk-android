package com.linecorp.linesdk.message;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents a location message to be sent using the
 * {@link com.linecorp.linesdk.message.MessageSendRequest} object.
 */
public class LocationMessage extends MessageData {

    @NonNull
    private final String title;
    @NonNull
    private final String address;
    @NonNull
    private final Double latitude;
    @NonNull
    private final Double longitude;

    /**
     * Constructs an {@link LocationMessage} object.
     * @param title Required. The title of the location.
     * @param address Required. The address of the location.
     * @param latitude Required. The latitude of the location.
     * @param longitude Required. The longitude of the location.
     */
    public LocationMessage(
            @NonNull String title,
            @NonNull String address,
            @NonNull Double latitude,
            @NonNull Double longitude
    ) {
        this.title = title;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @NonNull
    @Override
    public Type getType() {
        return Type.LOCATION;
    }

    @NonNull
    @Override
    public JSONObject toJsonObject() throws JSONException {
        JSONObject jsonObject = super.toJsonObject();
        jsonObject.put("title", title);
        jsonObject.put("address", address);
        jsonObject.put("latitude", latitude);
        jsonObject.put("longitude", longitude);
        return jsonObject;
    }
}
