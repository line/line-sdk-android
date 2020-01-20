package com.linecorp.linesdk.auth.internal;

import android.os.Parcel;

import com.linecorp.linesdk.TestConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertTrue;

/**
 * Test for {@link LineAppVersion}.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = TestConfig.TARGET_SDK_VERSION)
public class LineAuthenticationStatusTest {

    private LineAuthenticationStatus lineAuthenticationStatus;

    @Before
    public void setUp() {
        lineAuthenticationStatus = new LineAuthenticationStatus();
    }

    @Test
    public void testParcelableStatusStarted() {
        lineAuthenticationStatus.authenticationStarted();

        Parcel parcel = Parcel.obtain();
        lineAuthenticationStatus.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        LineAuthenticationStatus actual = (LineAuthenticationStatus) LineAuthenticationStatus.CREATOR.createFromParcel(parcel);

        assertTrue(actual.getStatus() == LineAuthenticationStatus.Status.STARTED);
    }

    @Test
    public void testParcelableStatusIntentReceived() {
        lineAuthenticationStatus.authenticationIntentReceived();

        Parcel parcel = Parcel.obtain();
        lineAuthenticationStatus.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        LineAuthenticationStatus actual = (LineAuthenticationStatus) LineAuthenticationStatus.CREATOR.createFromParcel(parcel);

        assertTrue(actual.getStatus() == LineAuthenticationStatus.Status.INTENT_RECEIVED);
    }

}
