package com.linecorp.linesdk.image;

/**
 * Configuration class for image loading in LINE SDK.
 * This class allows apps to provide their own image loading implementation
 * or use the default implementation provided by the LINE SDK.
 */
public final class LineSdkImageConfig {
    private LineSdkImageConfig() {
        // This class cannot be instantiated.
    }

    private static volatile ImageLoader imageLoader = DefaultImageLoader.INSTANCE;

    /**
     * Set a custom image loader implementation.
     * Call this method in your Application.onCreate() before using LINE SDK
     * features that display user images (e.g., friend picker dialog).
     *
     * @param loader Custom ImageLoader implementation, or null to use default
     */
    public static void setImageLoader(ImageLoader loader) {
        imageLoader = loader != null ? loader : DefaultImageLoader.INSTANCE;
    }

    /**
     * Get the currently configured image loader.
     * @return Current ImageLoader instance (never null)
     */
    public static ImageLoader getImageLoader() {
        return imageLoader;
    }

    /**
     * Reset to default image loader.
     * This is mainly useful for testing or if you want to switch back
     * to the default implementation.
     */
    public static void resetToDefault() {
        imageLoader = DefaultImageLoader.INSTANCE;
    }
}
