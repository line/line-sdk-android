package com.linecorp.linesdk.dialog.internal;

import android.net.Uri;

import com.linecorp.linesdk.LineFriendProfile;
import com.linecorp.linesdk.LineGroup;

public class TargetUser {
    public enum Type { FRIEND, GROUP }

    private String id;

    private String displayName;

    private Uri pictureUri;

    private Boolean isSelected = false;

    private Type type;

    public TargetUser(Type type, String id, String displayName, Uri pictureUri) {
        this.type = type;
        this.id = id;
        this.displayName = displayName;
        this.pictureUri = pictureUri;
    }

    public Type getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Uri getPictureUri() {
        return pictureUri;
    }

    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }

    public static TargetUser createInstance(final LineFriendProfile friend) {
        return new TargetUser(Type.FRIEND,
                              friend.getUserId(),
                              friend.getAvailableDisplayName(),
                              friend.getPictureUrl());
    }

    public static TargetUser createInstance(final LineGroup group) {
        return new TargetUser(Type.GROUP,
                              group.getGroupId(),
                              group.getGroupName(),
                              group.getPictureUrl());
    }

    public static int getTargetTypeCount() {
        return Type.values().length;
    }
}
