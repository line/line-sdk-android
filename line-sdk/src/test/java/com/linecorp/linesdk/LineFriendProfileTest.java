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
 * Test for {@link LineFriendProfile}.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = TestConfig.TARGET_SDK_VERSION)
public class LineFriendProfileTest {
    @Test
    public void testParcelable() {
        LineFriendProfile expected = new LineFriendProfile(
                "userId", "displayName", Uri.parse("http://line.me"), "statusMessage", "overriddenDisplayName");

        Parcel parcel = Parcel.obtain();
        expected.writeToParcel(parcel, 0);

        parcel.setDataPosition(0);

        LineFriendProfile actual = LineFriendProfile.CREATOR.createFromParcel(parcel);
        assertTrue(expected.equals(actual));
    }

    @Test
    public void testEquals() {
        LineFriendProfile expected = new LineFriendProfile(
                "userId", "displayName", Uri.parse("http://line.me"), "statusMessage", "overriddenDisplayName");

        assertTrue(expected.equals(new LineFriendProfile(
                "userId", "displayName", Uri.parse("http://line.me"), "statusMessage",
                "overriddenDisplayName")));
        assertFalse(expected.equals(new LineFriendProfile(
                "userId2", "displayName", Uri.parse("http://line.me"), "statusMessage",
                "overriddenDisplayName")));
        assertFalse(expected.equals(new LineFriendProfile(
                "userId", "displayName2", Uri.parse("http://line.me"), "statusMessage",
                "overriddenDisplayName")));
        assertFalse(expected.equals(new LineFriendProfile(
                "userId", "displayName", Uri.parse("http://line.me/2"), "statusMessage",
                "overriddenDisplayName")));
        assertFalse(expected.equals(new LineFriendProfile(
                "userId", "displayName", Uri.parse("http://line.me"), "statusMessage2",
                "overriddenDisplayName")));
        assertFalse(expected.equals(new LineFriendProfile(
                "userId", "displayName", Uri.parse("http://line.me"), "statusMessage",
                "overriddenDisplayName2")));
    }
}
