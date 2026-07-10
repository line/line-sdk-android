package com.linecorp.linesdk.image;

import android.widget.ImageView;

/**
 * Interface for loading images into ImageViews.
 * This allows apps to provide their own image loading implementation
 * (e.g., using Glide, Picasso, Coil) or use the default implementation
 * provided by the LINE SDK.
 *
 * @see com.linecorp.linesdk.image.LineSdkImageConfig#setImageLoader(ImageLoader)
 */
public interface ImageLoader {

    /**
     * Load an image from URL into ImageView with placeholder.
     *
     * @param url Image URL to load (may be null or empty)
     * @param imageView Target ImageView to load the image into
     * @param placeholderResId Resource ID for placeholder image while loading
     */
    void loadImage(String url, ImageView imageView, int placeholderResId);
}
