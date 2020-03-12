package com.linecorp.linesdk.testsuites;

import androidx.test.filters.MediumTest;
import androidx.test.rule.ActivityTestRule;

import com.linecorp.linesdk.pageobjects.ApisFragmentPage;
import com.linecorp.linesdk.pageobjects.MenuFragmentPage;
import com.linecorp.linesdktest.MainActivity;
import com.linecorp.linesdktest.R;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.core.AllOf.allOf;

@MediumTest
public class LineLoginButtonTests {
    MenuFragmentPage menuFragmentPage;
    ApisFragmentPage apisFragmentPage;

    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule(MainActivity.class);

    @Before
    public void setup() {
        menuFragmentPage = new MenuFragmentPage();
        apisFragmentPage = new ApisFragmentPage();
    }

    @Test
    public void testLineLoginButtonExists() {
        //Given
        onView(allOf(withId(R.id.line_login_btn))).check(matches(isDisplayed()));
        //Then
        onView(allOf(withId(R.id.line_login_btn))).check(matches(isEnabled()));
    }
}
