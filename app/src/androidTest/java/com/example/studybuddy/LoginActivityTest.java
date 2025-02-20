package com.example.studybuddy;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.studybuddy.activities.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static java.util.function.Predicate.not;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {

    private FirebaseAuth mockAuth;

    @Before
    public void setUp() {
        // Mock FirebaseAuth
        mockAuth = FirebaseAuth.getInstance();
    }

    @After
    public void tearDown() {
        // Clear any persisted state
        mockAuth.signOut();
    }

    @Test
    public void testValidLogin() throws InterruptedException {
        ActivityScenario.launch(LoginActivity.class);

        // Enter valid email and password
        onView(withId(R.id.emailInput)).perform(typeText("luka.brnetic@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.passwordInput)).perform(typeText("Lukaerik1"), closeSoftKeyboard());

        // Click login button
        onView(withId(R.id.loginButton)).perform(click());

        Thread.sleep(4000);


        // Verify that the fragmentContainer is displayed in DashboardActivity
        onView(withId(R.id.fragmentContainer)).check(matches(isDisplayed()));
    }

    @Test
    public void testInvalidEmailLogin() {
        ActivityScenario.launch(LoginActivity.class);

        // Enter invalid email and valid password
        onView(withId(R.id.emailInput)).perform(typeText("invalidemail"), closeSoftKeyboard());
        onView(withId(R.id.passwordInput)).perform(typeText("Password123"), closeSoftKeyboard());

        // Click login button
        onView(withId(R.id.loginButton)).perform(click());

        // Verify that the email input field shows an error
        onView(withId(R.id.emailInput)).check(matches(hasErrorText("Please enter a valid email")));
    }

    @Test
    public void testEmptyPasswordLogin() {
        ActivityScenario.launch(LoginActivity.class);

        // Enter valid email and leave password blank
        onView(withId(R.id.emailInput)).perform(typeText("test@example.com"), closeSoftKeyboard());
        onView(withId(R.id.passwordInput)).perform(typeText(""), closeSoftKeyboard());

        // Click login button
        onView(withId(R.id.loginButton)).perform(click());

        // Verify that the password input field shows an error
        onView(withId(R.id.passwordInput)).check(matches(hasErrorText("Password cannot be empty")));
    }

    @Test
    public void testLongEmailInput() throws InterruptedException {
        ActivityScenario.launch(LoginActivity.class);

        // Enter a long email address
        onView(withId(R.id.emailInput)).perform(typeText("thisisaverylongemailaddress@example.com"), closeSoftKeyboard());
        onView(withId(R.id.passwordInput)).perform(typeText("Password123"), closeSoftKeyboard());

        // Click login button
        onView(withId(R.id.loginButton)).perform(click());

        // Verify that login proceeds without errors (assuming your app can handle long emails)
        Thread.sleep(4000);
        onView(withId(R.id.fragmentContainer)).check(matches(isDisplayed()));
    }

    @Test
    public void testEmptyFields() {
        ActivityScenario.launch(LoginActivity.class);

        // Leave email and password blank
        onView(withId(R.id.loginButton)).perform(click());

        // Verify that both fields show error messages
        onView(withId(R.id.emailInput)).check(matches(hasErrorText("Please enter a valid email")));

    }
}

