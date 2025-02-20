
package com.example.studybuddy;

import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;


import com.example.studybuddy.activities.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.endsWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class StudyBuddyUITests {
    private static final String TEST_EMAIL = "test2@test.com";
    private static final String TEST_PASSWORD = "Today@123";

    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    @Before
    public void setup() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.getIdlingResource());
        FirebaseAuth.getInstance().signOut();

        // Login
        onView(allOf(
                withId(R.id.emailInput),
                isDescendantOfA(withClassName(endsWith("TextInputLayout")))
        ))
                .perform(replaceText(TEST_EMAIL), closeSoftKeyboard());

        onView(allOf(
                withId(R.id.passwordInput),
                isDescendantOfA(withClassName(endsWith("TextInputLayout")))
        ))
                .perform(replaceText(TEST_PASSWORD), closeSoftKeyboard());

        onView(withId(R.id.loginButton))
                .perform(click());

        // Wait until the main activity is displayed
        onView(withId(R.id.addGroupButton))
                .check(matches(isDisplayed()));
    }

    @After
    public void cleanup() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.getIdlingResource());
        FirebaseAuth.getInstance().signOut();
    }

    @Test
    public void testCreateGroup() {

        onView(withId(R.id.addGroupButton))
                .perform(click());

        onView(withId(R.id.groupNameInput))
                .check(matches(isDisplayed()));

        onView(withId(R.id.groupNameInput))
                .perform(replaceText("Chemistry Study Group"), closeSoftKeyboard());


        onView(withId(R.id.courseSpinner))
                .perform(click());


        onData(anything())
                .atPosition(0)
                .inRoot(isPlatformPopup())
                .perform(click());


        onView(withId(R.id.usersRecyclerView))
                .check(matches(isDisplayed()));


        for (int i = 0; i < 5; i++) {
            onView(withId(R.id.usersRecyclerView))
                    .perform(RecyclerViewActions.scrollToPosition(i));
            onView(withId(R.id.usersRecyclerView))
                    .perform(actionOnItemAtPosition(i, click()));
        }


        onView(withId(R.id.createButton))
                .perform(click());


        onView(withText("Create Group")).check(doesNotExist());
    }

    @Test
    public void testGroupDetails() {

        onView(withId(R.id.navigation_home)).perform(click());

        onView(withId(R.id.groupsRecyclerView))
                .perform(actionOnItemAtPosition(0, click()));

        onView(withId(R.id.groupNameTextView)).check(matches(isDisplayed()));
        onView(withId(R.id.membersListView)).check(matches(isDisplayed()));
    }

    @Test
    public void testGroupFeatures() {

        onView(withId(R.id.navigation_home)).perform(click());

        onView(withId(R.id.addGroupButton)).check(matches(isDisplayed()));
        onView(withId(R.id.groupsRecyclerView)).check(matches(isDisplayed()));
        onView(withId(R.id.welcomeTextView)).check(matches(isDisplayed()));
    }

    @Test
    public void testLogout() {

        onView(withId(R.id.navigation_logout)).perform(click());

        onView(withId(R.id.loginButton)).check(matches(isDisplayed()));
        onView(withId(R.id.emailInput)).check(matches(isDisplayed()));
        onView(withId(R.id.passwordInput)).check(matches(isDisplayed()));
    }

    @Test
    public void testResourceFeatures() {

        onView(withId(R.id.navigation_resources)).perform(click());

        onView(withId(R.id.resourcesRecyclerView)).check(matches(isDisplayed()));
        onView(withId(R.id.uploadButton)).check(matches(isDisplayed()));
        onView(withId(R.id.searchView)).check(matches(isDisplayed()));
    }
}
