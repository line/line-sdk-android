package com.linecorp.linesdk.internal.nwclient.core;

import com.linecorp.linesdk.BuildConfig;
import com.linecorp.linesdk.TestConfig;
import com.linecorp.linesdk.internal.nwclient.core.UserAgentGenerator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertTrue;

/**
 * A test of {@link UserAgentGenerator}.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = TestConfig.TARGET_SDK_VERSION)
public class UserAgentGeneratorTest {
    private static final String SDK_VERSION = "999.999";

    private UserAgentGenerator target;

    @Before
    public void setUp() {
        target = new UserAgentGenerator(RuntimeEnvironment.application, SDK_VERSION);
    }

    @Test
    public void testGetUserAgent() throws Exception {
        assertTrue(target.getUserAgent().contains("ChannelSDK/" + SDK_VERSION));
    }
}
