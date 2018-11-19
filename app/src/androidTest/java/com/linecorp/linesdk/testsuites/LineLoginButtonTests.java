package com.linecorp.linesdk.testsuites;

import android.support.test.filters.MediumTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.linecorp.linesdk.pageobjects.ApisFragmentPage;
import com.linecorp.linesdk.pageobjects.MenuFragmentPage;
import com.linecorp.linesdktest.MainActivity;
import com.linecorp.linesdktest.R;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.core.AllOf.allOf;

@RunWith(AndroidJUnit4.class)
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
        //When
        menuFragmentPage.inputChannelId();
        //Then
        onView(allOf(withId(R.id.line_login_btn))).check(matches(isEnabled()));
    }
}
