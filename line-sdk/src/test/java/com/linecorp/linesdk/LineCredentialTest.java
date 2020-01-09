package com.linecorp.linesdk;

import android.os.Parcel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test for {@link LineCredential}.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = TestConfig.TARGET_SDK_VERSION)
public class LineCredentialTest {
    @Test
    public void testParcelable() {
        LineCredential expected = new LineCredential(
                new LineAccessToken("accessToken", 1L, 2L),
                Arrays.asList(Scope.FRIEND, Scope.GROUP));

        Parcel parcel = Parcel.obtain();
        expected.writeToParcel(parcel, 0);

        parcel.setDataPosition(0);

        LineCredential actual = LineCredential.CREATOR.createFromParcel(parcel);
        assertTrue(expected.equals(actual));
    }

    @Test
    public void testEquals() {
        LineCredential expected = new LineCredential(
                new LineAccessToken("accessToken", 1L, 2L),
                Arrays.asList(Scope.FRIEND, Scope.GROUP));

        assertTrue(expected.equals(new LineCredential(
                new LineAccessToken("accessToken", 1L, 2L),
                Arrays.asList(Scope.FRIEND, Scope.GROUP))));
        assertFalse(expected.equals(new LineCredential(
                new LineAccessToken("accessToken2", 1L, 2L),
                Arrays.asList(Scope.FRIEND, Scope.GROUP))));
        assertFalse(expected.equals(new LineCredential(
                new LineAccessToken("accessToken", 1L, 2L),
                Arrays.asList(Scope.FRIEND))));
    }
}
