package com.linecorp.linesdk.internal;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.linecorp.linesdk.LoginDelegate;

public class LoginDelegateImpl implements LoginDelegate {
    @Nullable
    private LoginHandler loginHandler;

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        return loginHandler != null && loginHandler.onActivityResult(requestCode, resultCode, data);
    }

    public void setLoginHandler(@NonNull LoginHandler loginHandler) {
        this.loginHandler = loginHandler;
    }
}
