package com.example.studybuddy;

import static org.junit.Assert.*;

import android.text.TextUtils;

import com.example.studybuddy.utils.ValidationUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

public class ValidationUtilsTest {

    private MockedStatic<TextUtils> textUtilsMock;

    @Before
    public void setUp() {
        // Mock the TextUtils class
        textUtilsMock = Mockito.mockStatic(TextUtils.class);
        textUtilsMock.when(() -> TextUtils.isEmpty(Mockito.anyString())).thenAnswer(invocation -> {
            String value = invocation.getArgument(0);
            return value == null || value.isEmpty();
        });
    }

    @After
    public void tearDown() {
        // Close the mock
        textUtilsMock.close();
    }

    @Test
    public void testIsValidName_Success() {
        String name = "John Doe";
        boolean result = ValidationUtils.isValidUsername(name);
        assertTrue("Valid name should pass", result);
    }

    @Test
    public void testIsValidName_Failure() {
        assertFalse("Empty name should fail", ValidationUtils.isValidUsername(""));
        assertFalse("Name shorter than 3 characters should fail", ValidationUtils.isValidUsername("Jo"));
        assertFalse("Name longer than 30 characters should fail",
                ValidationUtils.isValidUsername("Johnathan Christopher Doe Longname"));
    }

    @Test
    public void testIsValidEmail_Success() {
        assertTrue("Valid email should pass", ValidationUtils.isValidEmail("johndoe@example.com"));
        assertTrue("Valid email with subdomain should pass", ValidationUtils.isValidEmail("john.doe@sub.example.com"));
    }

    @Test
    public void testIsValidEmail_Failure() {
        assertFalse("Email without @ should fail", ValidationUtils.isValidEmail("johndoeexample.com"));
        assertFalse("Email without domain should fail", ValidationUtils.isValidEmail("johndoe@.com"));
        assertFalse("Empty email should fail", ValidationUtils.isValidEmail(""));
    }

    @Test
    public void testIsValidPassword_Success() {
        assertTrue("Password with uppercase, digit, and special character should pass",
                ValidationUtils.isValidPassword("Password123!"));
        assertTrue("Password with more than 8 characters and mixed types should pass",
                ValidationUtils.isValidPassword("Pass@2023"));
    }

    @Test
    public void testIsValidPassword_Failure() {
        assertFalse("Password shorter than 6 characters should fail", ValidationUtils.isValidPassword("Shor1"));
        assertFalse("Password without uppercase should fail", ValidationUtils.isValidPassword("password123!"));
        assertFalse("Password without digit should fail", ValidationUtils.isValidPassword("Password!"));
        assertFalse("Empty password should fail", ValidationUtils.isValidPassword(""));
    }

    @Test
    public void testArePasswordsMatching_Success() {
        String password = "Password123!";
        String confirmPassword = "Password123!";
        boolean result = ValidationUtils.doPasswordsMatch(password, confirmPassword);
        assertTrue("Matching passwords should pass", result);
    }

    @Test
    public void testArePasswordsMatching_Failure() {
        String password = "Password123!";
        String confirmPassword = "Password456!";
        boolean result = ValidationUtils.doPasswordsMatch(password, confirmPassword);
        assertFalse("Mismatched passwords should fail", result);
    }
}
