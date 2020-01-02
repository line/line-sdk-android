package com.linecorp.linesdk.internal;

import android.app.Fragment;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * A wrapper to wrap the {@link Fragment} or {@link androidx.fragment.app.Fragment}. With this
 * wrapper, you don't need to know what fragment is used when calling
 * {@link #startActivityForResult(Intent, int)}.
 */
public class FragmentWrapper {
    @Nullable
    private Fragment fragment;

    @Nullable
    private androidx.fragment.app.Fragment supportFragment;

    public FragmentWrapper(@NonNull Fragment fragment) {
        this.fragment = fragment;
    }

    public FragmentWrapper(@NonNull androidx.fragment.app.Fragment fragment) {
        supportFragment = fragment;
    }

    public void startActivityForResult(Intent intent, int requestCode) {
        if (fragment != null) {
            fragment.startActivityForResult(intent, requestCode);
            return;
        }
        if (supportFragment != null) {
            supportFragment.startActivityForResult(intent, requestCode);
        }
    }
}
