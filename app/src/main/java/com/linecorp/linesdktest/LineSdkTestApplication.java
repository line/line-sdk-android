package com.linecorp.linesdktest;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.multidex.MultiDex;

import android.util.Log;

/**
 * {@link Application} for the LINE SDK test application.
 */
public class LineSdkTestApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        registerActivityLifecycleCallbacks(new ActivityLifecycleLoggingCallback("Activity.lifecycle"));
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private static class ActivityLifecycleLoggingCallback implements ActivityLifecycleCallbacks {
        @NonNull
        private final String tag;

        ActivityLifecycleLoggingCallback(@NonNull String tag) {
            this.tag = tag;
        }

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            Log.d(tag, activity.getClass().getSimpleName() + " : onCreate");
        }

        @Override
        public void onActivityStarted(Activity activity) {
            Log.d(tag, activity.getClass().getSimpleName() + " : onStart");
        }

        @Override
        public void onActivityResumed(Activity activity) {
            Log.d(tag, activity.getClass().getSimpleName() + " : onResume");
        }

        @Override
        public void onActivityPaused(Activity activity) {
            Log.d(tag, activity.getClass().getSimpleName() + " : onPause");
        }

        @Override
        public void onActivityStopped(Activity activity) {
            Log.d(tag, activity.getClass().getSimpleName() + " : onStop");
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            Log.d(tag, activity.getClass().getSimpleName() + " : onRestore");
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            Log.d(tag, activity.getClass().getSimpleName() + " : onDestroy");
        }
    }
}
