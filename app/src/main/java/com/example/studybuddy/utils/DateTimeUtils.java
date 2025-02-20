package com.example.studybuddy.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateTimeUtils {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
    private static final long HOUR_IN_MILLIS = 3600000;
    private static final long DAY_IN_MILLIS = 86400000;

    // Date formatting methods
    public static String formatDate(Date date) {
        if (date == null) return "";
        return DATE_FORMAT.format(date);
    }

    public static String formatTime(Date date) {
        if (date == null) return "";
        return TIME_FORMAT.format(date);
    }

    public static String formatDateTime(Date date) {
        if (date == null) return "";
        return DATE_TIME_FORMAT.format(date);
    }

    // Date parsing methods
    public static Date parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) return null;
        try {
            DATE_FORMAT.setLenient(false);
            return DATE_FORMAT.parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
    }

    public static Date parseTime(String timeStr) {
        if (timeStr == null || timeStr.trim().isEmpty()) return null;
        try {
            TIME_FORMAT.setLenient(false);
            return TIME_FORMAT.parse(timeStr);
        } catch (ParseException e) {
            return null;
        }
    }

    public static Date parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) return null;
        try {
            DATE_TIME_FORMAT.setLenient(false);
            return DATE_TIME_FORMAT.parse(dateTimeStr);
        } catch (ParseException e) {
            return null;
        }
    }

    // Validation methods
    public static boolean isValidDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) return false;
        try {
            DATE_FORMAT.setLenient(false);
            Date date = DATE_FORMAT.parse(dateStr);
            return date != null && !date.before(new Date());
        } catch (ParseException e) {
            return false;
        }
    }

    public static boolean isValidTime(String timeStr) {
        if (timeStr == null || timeStr.trim().isEmpty()) return false;
        try {
            TIME_FORMAT.setLenient(false);
            TIME_FORMAT.parse(timeStr);
            String[] parts = timeStr.split(":");
            if (parts.length != 2) return false;
            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);
            return hours >= 0 && hours < 24 && minutes >= 0 && minutes < 60;
        } catch (Exception e) {
            return false;
        }
    }

    // Time calculations
    public static long getTimeDifferenceInHours(Date date1, Date date2) {
        if (date1 == null || date2 == null) return 0;
        return (date2.getTime() - date1.getTime()) / HOUR_IN_MILLIS;
    }

    public static long getTimeDifferenceInDays(Date date1, Date date2) {
        if (date1 == null || date2 == null) return 0;
        return (date2.getTime() - date1.getTime()) / DAY_IN_MILLIS;
    }

    public static String getTimeAgo(Date date) {
        if (date == null) return "";

        long diff = new Date().getTime() - date.getTime();
        long hours = diff / HOUR_IN_MILLIS;
        long days = diff / DAY_IN_MILLIS;

        if (hours < 1) return "Just now";
        if (hours < 24) return hours + " hours ago";
        if (days < 7) return days + " days ago";

        return formatDate(date);
    }

    // Utility methods for time comparison
    public static boolean isTimeAfter(String time1, String time2) {
        try {
            Date t1 = TIME_FORMAT.parse(time1);
            Date t2 = TIME_FORMAT.parse(time2);
            return t1.after(t2);
        } catch (ParseException e) {
            return false;
        }
    }

    public static boolean isDateAfterToday(String dateStr) {
        try {
            Date date = DATE_FORMAT.parse(dateStr);
            Date today = new Date();
            return date.after(today);
        } catch (ParseException e) {
            return false;
        }
    }
}