package com.linecorp.linesdk;

import android.content.Intent;

import com.linecorp.linesdk.internal.LoginDelegateImpl;

/**
 * Delegates the login result to the internal login handler. Use the
 * provided {@link Factory#create()} method to create an object that implements the LoginDelegate
 * interface.
 */
public interface LoginDelegate {
    /**
     * Delegates the login result to the internal login handler. Call this method in the
     * onActivityResult() of an <code>Activity</code> instance or a <code>Fragment</code> instance.
     *
     * @param requestCode The integer request code originally supplied to the
     *                    startActivityForResult() method. This can be used to identify the source of
     *                    the login result.
     * @param resultCode  The integer result code returned by the login activity through its
     *                    setResult() method.
     * @param data        The login result's intent.
     */
    boolean onActivityResult(int requestCode, int resultCode, Intent data);

    /**
     * Represents a factory that creates objects that implement the LoginDelegate interface.
     */
    class Factory {
        /**
         * Creates an object that implements the LoginDelegate interface.
         * @return
         */
        public static LoginDelegate create() {
            return new LoginDelegateImpl();
        }
    }
}
