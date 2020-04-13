package com.linecorp.linesdk.pageobjects;

import com.linecorp.linesdktest.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.core.AllOf.allOf;

public class ApisFragmentPage {
    public void tapLogoutButton() {
        onView(allOf(withId(R.id.logout_btn))).perform(scrollTo()).perform(click());
    }

    public void tapRefreshTokenButton() {
        onView(allOf(withId(R.id.refresh_token_btn))).perform(scrollTo()).perform(click());
    }

    public void tapVerifyTokenButton() {
        onView(allOf(withId(R.id.verify_token_btn))).perform(scrollTo()).perform(click());
    }

    public void tapGetCurrentTokenButton() {
        onView(allOf(withId(R.id.get_current_token_btn))).perform(scrollTo()).perform(click());
    }

    public void tapGetProfileButton() {
        onView(allOf(withId(R.id.get_profile_btn))).perform(scrollTo()).perform(click());
    }

    public void tapGetFriendshipStatusButton() {
        onView(allOf(withId(R.id.get_friendship_status_btn))).perform(scrollTo()).perform(click());
    }
}
