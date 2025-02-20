package com.example.studybuddy;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.example.studybuddy.activities.RegisterActivity;

import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import java.util.UUID;


@RunWith(AndroidJUnit4.class)
public class RegisterActivityTest {

    @Test
    public void testValidRegistration() throws InterruptedException {
        ActivityScenario.launch(RegisterActivity.class);

        String uniqueEmail = "testuser" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
        // Fill out the registration form with valid data
        onView(withId(R.id.nameInput)).perform(typeText("Test User"), closeSoftKeyboard());
        onView(withId(R.id.emailInput)).perform(typeText(uniqueEmail), closeSoftKeyboard());
        onView(withId(R.id.passwordInput)).perform(typeText("Password123"), closeSoftKeyboard());
        onView(withId(R.id.confirmPasswordInput)).perform(typeText("Password123"), closeSoftKeyboard());

        onView(withId(R.id.courseSpinner)).perform(click());
        onView(withText("Computer Science")).inRoot(RootMatchers.isPlatformPopup()).perform(click());

        onView(withId(R.id.registerButton)).perform(click());
        Thread.sleep(4000);

        // Verify that the user is redirected to the DashboardActivity
        onView(withId(R.id.fragmentContainer))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testMismatchedPasswords() {
        ActivityScenario.launch(RegisterActivity.class);
        // Fill out the registration form with mismatched passwords
        onView(withId(R.id.nameInput)).perform(typeText("Test User"), closeSoftKeyboard());
        onView(withId(R.id.emailInput)).perform(typeText("test3@example.com"), closeSoftKeyboard());
        onView(withId(R.id.passwordInput)).perform(typeText("Password123"), closeSoftKeyboard());
        onView(withId(R.id.confirmPasswordInput)).perform(typeText("Password321"), closeSoftKeyboard());

        onView(withId(R.id.registerButton)).perform(click());

        // Verify that the "Passwords do not match" error message is displayed
        onView(withId(R.id.confirmPasswordInput))
                .check(matches(hasErrorText("Passwords do not match")));
    }

    @Test
    public void testEmptyFields() {
        ActivityScenario.launch(RegisterActivity.class);
        onView(withId(R.id.registerButton)).perform(click());


        // Verify that errors are shown for required fields
        onView(withId(R.id.nameInput)).check(matches(hasErrorText("Please enter a valid name (3-30 characters)")));
        onView(withId(R.id.nameInput)).perform(typeText("Test User"), closeSoftKeyboard());
        onView(withId(R.id.registerButton)).perform(click());
        onView(withId(R.id.emailInput)).check(matches(hasErrorText("Please enter a valid email")));
        onView(withId(R.id.emailInput)).perform(typeText("test@example.com"), closeSoftKeyboard());
        onView(withId(R.id.registerButton)).perform(click());
        onView(withId(R.id.passwordInput)).check(matches(hasErrorText(
                "Password must:\n" +
                        "• Be at least 6 characters long\n" +
                        "• Contain at least one digit\n" +
                        "• Contain at least one lowercase letter\n" +
                        "• Contain at least one uppercase letter\n" +
                        "• Not contain spaces"
        )));
        onView(withId(R.id.passwordInput)).perform(typeText("Password123"), closeSoftKeyboard());
        onView(withId(R.id.registerButton)).perform(click());
        onView(withId(R.id.confirmPasswordInput)).check(matches(hasErrorText("Passwords do not match"))); // Assuming a confirm password error
    }

    @Test
    public void testInvalidEmail() {
        ActivityScenario.launch(RegisterActivity.class);
        // Fill out the registration form with an invalid email
        onView(withId(R.id.nameInput)).perform(typeText("Test User"), closeSoftKeyboard());
        onView(withId(R.id.emailInput)).perform(typeText("invalidemail"), closeSoftKeyboard());
        onView(withId(R.id.passwordInput)).perform(typeText("password123"), closeSoftKeyboard());
        onView(withId(R.id.confirmPasswordInput)).perform(typeText("password123"), closeSoftKeyboard());

        // Click on the register button
        onView(withId(R.id.registerButton)).perform(click());

        // Verify that the "Please enter a valid email" error message is displayed
        onView(withId(R.id.emailInput)).check(matches(hasErrorText("Please enter a valid email")));
    }

    @Test
    public void testMultipleCourseSelection() throws InterruptedException{
        ActivityScenario.launch(RegisterActivity.class);

        String uniqueEmail = "testuser" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";

        // Fill out the registration form with valid data
        onView(withId(R.id.nameInput)).perform(typeText("Test User2"), closeSoftKeyboard());
        onView(withId(R.id.emailInput)).perform(typeText(uniqueEmail), closeSoftKeyboard());
        onView(withId(R.id.passwordInput)).perform(typeText("Password123"), closeSoftKeyboard());
        onView(withId(R.id.confirmPasswordInput)).perform(typeText("Password123"), closeSoftKeyboard());

        // Select multiple courses from the spinner
        onView(withId(R.id.courseSpinner)).perform(click());
        onView(withText("Computer Science")).inRoot(RootMatchers.isPlatformPopup()).perform(click());
        onView(withId(R.id.courseSpinner)).perform(click());
        onView(withText("Mathematics")).inRoot(RootMatchers.isPlatformPopup()).perform(click());
        onView(withId(R.id.courseSpinner)).perform(click());
        onView(withText("Biology")).inRoot(RootMatchers.isPlatformPopup()).perform(click());

        onView(withId(R.id.registerButton)).perform(click());
        Thread.sleep(4000);
        // Verify that the user is redirected to the DashboardActivity
        onView(withId(R.id.fragmentContainer))
                .check(matches(isDisplayed()));
    }

}