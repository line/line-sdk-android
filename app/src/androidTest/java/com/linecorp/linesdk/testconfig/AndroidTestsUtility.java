package com.linecorp.linesdk.testconfig;

import android.content.Context;
import android.content.pm.PackageManager;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;

public class AndroidTestsUtility {
    public static boolean checkTextExists(String text) {
        UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        UiObject result = uiDevice.findObject(new UiSelector().text(text));
        return result.exists();
    }

    public static boolean checkElementExists(String className) {
        UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        uiDevice.wait(Until.hasObject(By.clazz(className)), AndroidTestsConfig.TIMEOUT);
        UiObject2 element = uiDevice.findObject(By.clazz(className));
        return element.isEnabled() && element.isClickable() && element.isFocusable();
    }

    public static boolean checkElementExists(int resourceId) {
        UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        Context context = ApplicationProvider.getApplicationContext();
        return uiDevice.wait(Until.hasObject(By.res(toResourceName(context, resourceId))), AndroidTestsConfig.TIMEOUT);
    }

    public static boolean isLineAppInstalled() {
        Context context = ApplicationProvider.getApplicationContext();
        return isPackageInstalled(AndroidTestsConfig.LINE_PACKAGE_NAME, context.getPackageManager());
    }

    public static boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        try {
            return packageManager.getApplicationInfo(packageName, 0).enabled;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private static String toResourceName(Context context, int resourceId) {
        return context.getResources().getResourceName(resourceId);
    }
}
