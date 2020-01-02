package com.linecorp.linesdk.pageobjects;

import com.linecorp.linesdktest.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.core.AllOf.allOf;

public class MenuFragmentPage {

    private static final String testChannelId  = "123456789";

    public void inputChannelId() {
        onView(allOf(withId(R.id.channel_id_edittext), isDisplayed())).perform(typeText(testChannelId));
    }

    public void navigateToApisPage() {
        onView(allOf(withId(R.id.apis_btn), isDisplayed())).perform(click());
    }
}
