package com.linecorp.linesdk.testsuites;

import androidx.test.filters.MediumTest;
import androidx.test.rule.ActivityTestRule;

import com.linecorp.linesdk.pageobjects.ApisFragmentPage;
import com.linecorp.linesdk.pageobjects.MenuFragmentPage;
import com.linecorp.linesdk.testconfig.AndroidTestsConfig;
import com.linecorp.linesdktest.MainActivity;
import com.linecorp.linesdktest.R;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.StringContains.containsString;

@MediumTest
public class LoginApiTests {
    MenuFragmentPage menuFragmentPage;
    ApisFragmentPage apisFragmentPage;

    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule(MainActivity.class);

    @Before
    public void setup() {
        menuFragmentPage = new MenuFragmentPage();
        apisFragmentPage = new ApisFragmentPage();

        menuFragmentPage.tapApisButton();
        apisFragmentPage.tapLogoutButton();
    }

    @Test
    public void testNotLoginLogout() {
        // When
        apisFragmentPage.tapLogoutButton();
        // Then
        onView(allOf(withId(R.id.log)))
                .check(matches(withText(containsString(AndroidTestsConfig.ERROR_LOGOUT))));
    }

    @Test
    public void testNotLoginRefreshToken() {
        // When
        apisFragmentPage.tapRefreshTokenButton();
        // Then
        onView(allOf(withId(R.id.log)))
                .check(matches(withText(containsString(AndroidTestsConfig.ERROR_REFRESH_TOKEN))));
    }

    @Test
    public void testNotLoginVerifyToken() {
        // When
        apisFragmentPage.tapVerifyTokenButton();
        // Then
        onView(allOf(withId(R.id.log)))
                .check(matches(withText(containsString(AndroidTestsConfig.ERROR_VERIFY_TOKEN))));
    }

    @Test
    public void testNotLoginGetCurrentToken() {
        // When
        apisFragmentPage.tapGetCurrentTokenButton();
        // Then
        onView(allOf(withId(R.id.log)))
                .check(matches(withText(containsString(AndroidTestsConfig.ERROR_GET_CURRENT_TOKEN))));
    }

    @Test
    public void testNotLoginGetProfile() {
        // When
        apisFragmentPage.tapGetProfileButton();
        // Then
        onView(allOf(withId(R.id.log)))
                .check(matches(withText(containsString(AndroidTestsConfig.ERROR_GET_PROFILE))));
    }

    @Test
    public void testNotLoginGetFriendshipStatus() {
        // When
        apisFragmentPage.tapGetFriendshipStatusButton();
        // Then
        onView(allOf(withId(R.id.log)))
                .check(matches(withText(containsString(AndroidTestsConfig.ERROR_GET_FRIENDSHIP_STATUS))));
    }
}
