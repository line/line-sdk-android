package com.linecorp.linesdk.pageobjects;

import com.linecorp.linesdktest.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.core.AllOf.allOf;

public class MenuFragmentPage {
    public void navigateToApisPage() {
        onView(allOf(withId(R.id.apis_btn), isDisplayed())).perform(click());
    }
}
