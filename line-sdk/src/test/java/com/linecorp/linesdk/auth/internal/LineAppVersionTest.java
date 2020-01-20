package com.linecorp.linesdk.auth.internal;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.linecorp.linesdk.TestConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;

/**
 * Test for {@link LineAppVersion}.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = TestConfig.TARGET_SDK_VERSION)
public class LineAppVersionTest {

    @Mock
    private Context context;
    @Mock
    private PackageManager packageManager;
    @Mock
    private PackageInfo packageInfo;

    @Before
    public void setUp() throws PackageManager.NameNotFoundException {
        MockitoAnnotations.initMocks(this);
        doReturn(packageManager).when(context).getPackageManager();
        doReturn(packageInfo).when(packageManager)
                .getPackageInfo("jp.naver.line.android", PackageManager.GET_META_DATA);
    }

    @Test
    public void testGetLineAppVersion() throws PackageManager.NameNotFoundException, NoSuchFieldException, IllegalAccessException {
        LineAppVersion lineAppVersion;

        setVersionName(packageInfo, "1.2.3");
        lineAppVersion = LineAppVersion.getLineAppVersion(context);
        assertEquals(1, lineAppVersion.getMajor());
        assertEquals(2, lineAppVersion.getMinor());
        assertEquals(3, lineAppVersion.getRevision());

        setVersionName(packageInfo, "illegalVersion");
        assertNull(LineAppVersion.getLineAppVersion(context));

        setVersionName(packageInfo, null);
        assertNull(LineAppVersion.getLineAppVersion(context));
    }

    @Test
    public void testIsEqualOrGreaterThan() {
        assertFalse(new LineAppVersion(1, 1, 1).isEqualOrGreaterThan(null));
        assertTrue(new LineAppVersion(1, 1, 1).isEqualOrGreaterThan(new LineAppVersion(0, 1, 1)));
        assertTrue(new LineAppVersion(1, 1, 1).isEqualOrGreaterThan(new LineAppVersion(1, 0, 1)));
        assertTrue(new LineAppVersion(1, 1, 1).isEqualOrGreaterThan(new LineAppVersion(1, 1, 0)));
        assertTrue(new LineAppVersion(1, 1, 1).isEqualOrGreaterThan(new LineAppVersion(1, 1, 1)));
        assertFalse(new LineAppVersion(1, 1, 1).isEqualOrGreaterThan(new LineAppVersion(2, 1, 1)));
        assertFalse(new LineAppVersion(1, 1, 1).isEqualOrGreaterThan(new LineAppVersion(1, 2, 1)));
        assertFalse(new LineAppVersion(1, 1, 1).isEqualOrGreaterThan(new LineAppVersion(1, 1, 2)));
    }

    private static void setVersionName(@NonNull PackageInfo target, @Nullable String versionName)
            throws NoSuchFieldException, IllegalAccessException {
        Field field = PackageInfo.class.getDeclaredField("versionName");
        field.setAccessible(true);
        field.set(target, versionName);
    }
}
