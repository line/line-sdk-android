package com.linecorp.linesdk;

import android.os.Parcel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test for {@link LineAccessToken}.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = TestConfig.TARGET_SDK_VERSION)
public class LineAccessTokenTest {
    @Test
    public void testParcelable() {
        LineAccessToken expected = new LineAccessToken("accessToken", 1L, 2L);

        Parcel parcel = Parcel.obtain();
        expected.writeToParcel(parcel, 0);

        parcel.setDataPosition(0);

        LineAccessToken actual = (LineAccessToken) LineAccessToken.CREATOR.createFromParcel(parcel);
        assertTrue(expected.equals(actual));
    }

    @Test
    public void testEquals() {
        LineAccessToken expected = new LineAccessToken("accessToken", 1L, 2L);

        assertTrue(expected.equals(new LineAccessToken("accessToken", 1L, 2L)));
        assertFalse(expected.equals(new LineAccessToken("accessToken1", 1L, 2L)));
        assertFalse(expected.equals(new LineAccessToken("accessToken", 2L, 2L)));
        assertFalse(expected.equals(new LineAccessToken("accessToken", 1L, 1L)));
    }

    @Test
    public void testGetEstimatedExpirationTimeMillis() {
        LineAccessToken token = new LineAccessToken("test", 1L, 2L);
        assertEquals(3, token.getEstimatedExpirationTimeMillis());
    }
}
