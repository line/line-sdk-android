package com.linecorp.linesdk.dialog.internal;

import android.content.Context;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.linecorp.linesdk.R;
import com.linecorp.linesdk.image.LineSdkImageConfig;


public class UserThumbnailView extends ConstraintLayout  {
    private TextView targetUserName;

    private ImageView imageView;

    public UserThumbnailView(Context context) {
        super(context);
        init();
    }

    public void setTargetUser(TargetUser targetUser) {
        targetUserName.setText(targetUser.getDisplayName());

        int thumbnailResId = (targetUser.getType() == TargetUser.Type.FRIEND) ?
                                     R.drawable.friend_thumbnail : R.drawable.group_thumbnail;
        String imageUrl = targetUser.getPictureUri() != null ? 
                targetUser.getPictureUri().toString() : null;
        LineSdkImageConfig.getImageLoader().loadImage(
                imageUrl,
                imageView,
                thumbnailResId
        );
    }

    private void init() {
        inflate(getContext(), R.layout.target_user_thumbnail, this);
        targetUserName = findViewById(R.id.textViewDisplayName);
        imageView = findViewById(R.id.imageViewTargetUser);
    }
}
