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
public class LineGroupTest {
    @Test
    public void testParcelable() {
        LineGroup expected = new LineGroup(
                "groupId", "groupName", Uri.parse("http://line.me"));

        Parcel parcel = Parcel.obtain();
        expected.writeToParcel(parcel, 0);

        parcel.setDataPosition(0);

        LineGroup actual = LineGroup.CREATOR.createFromParcel(parcel);
        assertTrue(expected.equals(actual));
    }

    @Test
    public void testEquals() {
        LineGroup expected = new LineGroup(
                "groupId", "groupName", Uri.parse("http://line.me"));

        assertTrue(expected.equals(new LineGroup(
                "groupId", "groupName", Uri.parse("http://line.me"))));
        assertFalse(expected.equals(new LineGroup(
                "groupId2", "groupName", Uri.parse("http://line.me"))));
        assertFalse(expected.equals(new LineGroup(
                "groupId", "groupName2", Uri.parse("http://line.me"))));
        assertFalse(expected.equals(new LineGroup(
                "groupId", "groupName", Uri.parse("http://line.me/2"))));
    }
}
