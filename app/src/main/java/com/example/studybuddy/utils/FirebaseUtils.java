package com.example.studybuddy.utils;

import com.example.studybuddy.models.User;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirebaseUtils {
    private static FirebaseAuth auth;
    private static FirebaseFirestore db;
    private static FirebaseStorage storage;

    // Initialize Firebase components
    public static void initialize() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    // Auth methods
    public static FirebaseAuth getAuth() {
        if (auth == null) auth = FirebaseAuth.getInstance();
        return auth;
    }

    public static FirebaseUser getCurrentFirebaseUser() {
        return getAuth().getCurrentUser();
    }

    public static String getCurrentUserId() {
        FirebaseUser user = getCurrentFirebaseUser();
        return user != null ? user.getUid() : null;
    }

    // Firestore methods
    public static FirebaseFirestore getFirestore() {
        if (db == null) db = FirebaseFirestore.getInstance();
        return db;
    }

    public static Task<User> getCurrentUser() {
        String userId = getCurrentUserId();
        if (userId == null) {
            return Tasks.forException(new Exception("No user logged in"));
        }

        return getFirestore()
                .collection("users")
                .document(userId)
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            User user = document.toObject(User.class);
                            if (user != null) user.setId(document.getId());
                            return user;
                        }
                    }
                    throw new Exception("User data not found");
                });
    }

    // Storage methods
    public static FirebaseStorage getStorage() {
        if (storage == null) storage = FirebaseStorage.getInstance();
        return storage;
    }

    public static StorageReference getStorageReference() {
        return getStorage().getReference();
    }

    // User presence
    public static void updateUserPresence(boolean online) {
        String userId = getCurrentUserId();
        if (userId != null) {
            getFirestore()
                    .collection("users")
                    .document(userId)
                    .update(
                            "online", online,
                            "lastSeen", System.currentTimeMillis()
                    );
        }
    }

    // Clean up
    public static void cleanup() {
        auth = null;
        db = null;
        storage = null;
    }
}