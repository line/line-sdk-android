package com.linecorp.linesdk.pageobjects;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import com.linecorp.linesdk.testconfig.AndroidTestsConfig;

public class LineWebLoginPage {
    private UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

    public void tapChromeCloseButton() throws UiObjectNotFoundException {
        uiDevice.findObject(new UiSelector().resourceId(AndroidTestsConfig.CHROME_CLOSE_BUTTON))
                .click();
    }
}
