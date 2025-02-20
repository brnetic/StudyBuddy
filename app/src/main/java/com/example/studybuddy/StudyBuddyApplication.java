package com.example.studybuddy;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.example.studybuddy.utils.FirebaseUtils;

public class StudyBuddyApplication extends Application {
    public static final String NOTIFICATION_CHANNEL_ID = "study_buddy_channel";

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        FirebaseUtils.initialize();

        // Initialize Firebase App Check
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();

        // Create notification channel for Android O and above
        createNotificationChannel();

        // Set up user presence monitoring
        setupUserPresenceMonitoring();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    "StudyBuddy Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Notifications for new messages and study sessions");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void setupUserPresenceMonitoring() {
        FirebaseUtils.getAuth().addAuthStateListener(firebaseAuth -> {
            if (firebaseAuth.getCurrentUser() != null) {
                FirebaseUtils.updateUserPresence(true);
            }
        });
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        FirebaseUtils.updateUserPresence(false);
        FirebaseUtils.cleanup();
    }
}