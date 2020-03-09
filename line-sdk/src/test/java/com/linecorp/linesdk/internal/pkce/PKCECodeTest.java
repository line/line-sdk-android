package com.linecorp.linesdk.internal.pkce;

import android.os.Parcel;

import com.linecorp.linesdk.TestConfig;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test for {@link PKCECode}.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = TestConfig.TARGET_SDK_VERSION)
public class PKCECodeTest {
    @Test
    public void testNewCode() {
        final PKCECode code = PKCECode.newCode();

        assertEquals(64, code.getVerifier().length());
        assertTrue(!code.getChallenge().isEmpty());
    }

    @Test
    public void testParcelable() {
        Parcel parcel = Parcel.obtain();

        final PKCECode original = PKCECode.newCode();
        original.writeToParcel(parcel, 0);

        parcel.setDataPosition(0);
        final PKCECode actual = PKCECode.CREATOR.createFromParcel(parcel);

        assertEquals(original, actual);
    }
}
