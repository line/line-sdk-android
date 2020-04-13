package com.linecorp.linesdk.pageobjects;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.Until;

import com.linecorp.linesdk.testconfig.AndroidTestsConfig;

import static com.linecorp.linesdk.testconfig.AndroidTestsUtility.checkElementExists;
import static junit.framework.TestCase.assertTrue;

public class LineAppLoginPage {
    private UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

    public void tapAllowButton() {
        uiDevice.wait(Until.hasObject(By.pkg(AndroidTestsConfig.LINE_PACKAGE_NAME).depth(0)),
                AndroidTestsConfig.TIMEOUT);
        assertTrue(checkElementExists(AndroidTestsConfig.ANDROID_WIDGET_BUTTON));
        uiDevice.findObjects(By.clazz(AndroidTestsConfig.ANDROID_WIDGET_BUTTON)).get(0).click();
    }
}
