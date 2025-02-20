package com.example.studybuddy.utils;

import android.text.TextUtils;
import android.util.Patterns;
import java.util.regex.Pattern;

public class ValidationUtils {
    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final int MIN_USERNAME_LENGTH = 3;
    private static final int MAX_USERNAME_LENGTH = 30;

    private static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{6,}$"
    );

    // Email validation
    public static boolean isValidEmail(String email) {
        return email != null && email.matches(EMAIL_REGEX);
    }

    // Password validation
    public static boolean isValidPassword(String password) {
        return !TextUtils.isEmpty(password) &&
                password.length() >= MIN_PASSWORD_LENGTH &&
                PASSWORD_PATTERN.matcher(password).matches();
    }

    public static String getPasswordRequirements() {
        return "Password must:\n" +
                "• Be at least 6 characters long\n" +
                "• Contain at least one digit\n" +
                "• Contain at least one lowercase letter\n" +
                "• Contain at least one uppercase letter\n" +
                "• Not contain spaces";
    }

    public static boolean doPasswordsMatch(String password, String confirmPassword) {
        return password != null && password.equals(confirmPassword);
    }

    // Username validation
    public static boolean isValidUsername(String username) {
        return !TextUtils.isEmpty(username) &&
                username.length() >= MIN_USERNAME_LENGTH &&
                username.length() <= MAX_USERNAME_LENGTH;
    }

    // Course name validation
    public static boolean isValidCourseName(String courseName) {
        return !TextUtils.isEmpty(courseName) && courseName.length() <= 50;
    }

    // Group name validation
    public static boolean isValidGroupName(String groupName) {
        return !TextUtils.isEmpty(groupName) && groupName.length() <= 50;
    }

    // Session validation
    public static boolean isValidSessionTitle(String title) {
        return !TextUtils.isEmpty(title) && title.length() <= 100;
    }

    public static boolean isValidLocation(String location) {
        return !TextUtils.isEmpty(location) && location.length() <= 200;
    }

    // Resource validation
    public static boolean isValidResourceName(String name) {
        return !TextUtils.isEmpty(name) && name.length() <= 100;
    }

    public static boolean isValidFileSize(long size) {
        return size > 0 && size <= 50 * 1024 * 1024; // 50MB limit
    }
}