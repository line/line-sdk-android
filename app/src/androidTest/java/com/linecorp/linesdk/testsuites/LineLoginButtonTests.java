package com.linecorp.linesdk.testsuites;

import androidx.test.filters.MediumTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.UiObjectNotFoundException;

import com.linecorp.linesdk.pageobjects.ApisFragmentPage;
import com.linecorp.linesdk.pageobjects.LineAppLoginPage;
import com.linecorp.linesdk.pageobjects.LineWebLoginPage;
import com.linecorp.linesdk.pageobjects.MenuFragmentPage;
import com.linecorp.linesdk.testconfig.AndroidTestsConfig;
import com.linecorp.linesdktest.MainActivity;
import com.linecorp.linesdktest.R;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.linecorp.linesdk.testconfig.AndroidTestsUtility.checkElementExists;
import static com.linecorp.linesdk.testconfig.AndroidTestsUtility.checkTextExists;
import static com.linecorp.linesdk.testconfig.AndroidTestsUtility.isLineAppInstalled;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.StringContains.containsString;

@MediumTest
public class LineLoginButtonTests {
    private LineAppLoginPage lineAppLoginPage;
    private LineWebLoginPage lineWebLoginPage;
    private MenuFragmentPage menuFragmentPage;
    private ApisFragmentPage apisFragmentPage;

    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule(MainActivity.class);

    @Before
    public void setup() {
        lineAppLoginPage = new LineAppLoginPage();
        lineWebLoginPage = new LineWebLoginPage();
        menuFragmentPage = new MenuFragmentPage();
        apisFragmentPage = new ApisFragmentPage();
    }

    @Test
    public void testLineLoginButtonExists() {
        // Then
        onView(allOf(withId(R.id.line_login_btn))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.line_login_btn))).check(matches(isEnabled()));
    }

    @Test
    public void testApp2AppLineLoginSuccess() throws UiObjectNotFoundException {
        // Given
        tapLineLoginButton();

        if (isLineAppInstalled()) {
            // When
            lineAppLoginPage.tapAllowButton();
            menuFragmentPage.tapApisButton();
            apisFragmentPage.tapGetProfileButton();

            // Then
            checkGetProfile();
        } else {
            // Then
            assertTrue(checkTextExists(AndroidTestsConfig.LINE_WEB_URL));
            checkReturnSdkSampleApp();
        }
    }

    private void checkReturnSdkSampleApp() throws UiObjectNotFoundException {
        lineWebLoginPage.tapChromeCloseButton();
        assertTrue(checkElementExists(R.id.line_login_btn));
    }

    private void checkGetProfile() {
        assertTrue(checkElementExists(R.id.log));
        onView(allOf(withId(R.id.log)))
                .check(matches(withText(containsString(AndroidTestsConfig.SUCCESS_GET_PROFILE))));
    }

    private void tapLineLoginButton() {
        checkElementExists(R.id.line_login_btn);
        onView(allOf(withId(R.id.line_login_btn))).perform(click());
    }
}
