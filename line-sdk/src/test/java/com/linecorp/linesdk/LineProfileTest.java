package com.linecorp.linesdk;

import android.net.Uri;
import android.os.Parcel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test for {@link LineProfile}.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = TestConfig.TARGET_SDK_VERSION)
public class LineProfileTest {
    @Test
    public void testParcelable() {
        LineProfile expected = new LineProfile(
                "userId", "displayName", Uri.parse("http://line.me"), "statusMessage");

        Parcel parcel = Parcel.obtain();
        expected.writeToParcel(parcel, 0);

        parcel.setDataPosition(0);

        LineProfile actual = LineProfile.CREATOR.createFromParcel(parcel);
        assertTrue(expected.equals(actual));
    }

    @Test
    public void testEquals() {
        LineProfile expected = new LineProfile(
                "userId", "displayName", Uri.parse("http://line.me"), "statusMessage");

        assertTrue(expected.equals(new LineProfile(
                "userId", "displayName", Uri.parse("http://line.me"), "statusMessage")));
        assertFalse(expected.equals(new LineProfile(
                "userId2", "displayName", Uri.parse("http://line.me"), "statusMessage")));
        assertFalse(expected.equals(new LineProfile(
                "userId", "displayName2", Uri.parse("http://line.me"), "statusMessage")));
        assertFalse(expected.equals(new LineProfile(
                "userId", "displayName", Uri.parse("http://line.me/2"), "statusMessage")));
        assertFalse(expected.equals(new LineProfile(
                "userId", "displayName", Uri.parse("http://line.me"), "statusMessage2")));
    }
}
