package com.example.studybuddy.utils;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import com.example.studybuddy.models.*;
import java.util.*;

public class FirestoreManager {
    private static FirestoreManager instance;
    private final FirebaseFirestore db;
    private final FirebaseAuth auth;

    private FirestoreManager() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    public static synchronized FirestoreManager getInstance() {
        if (instance == null) {
            instance = new FirestoreManager();
        }
        return instance;
    }

    // Group Operations
    public Task<DocumentReference> createGroup(Group group) {
        return db.collection("groups").add(group);
    }

    public Task<Void> updateGroup(String groupId, Map<String, Object> updates) {
        return db.collection("groups").document(groupId).update(updates);
    }

    public Task<Void> deleteGroup(String groupId) {
        return db.collection("groups").document(groupId).delete();
    }

    public ListenerRegistration addGroupListener(String groupId,
                                                 com.google.firebase.firestore.EventListener<DocumentSnapshot> listener) {
        return db.collection("groups")
                .document(groupId)
                .addSnapshotListener(listener);
    }

    // Group Messages
    public Task<DocumentReference> sendGroupMessage(String groupId, Message message) {
        return db.collection("groups")
                .document(groupId)
                .collection("messages")
                .add(message);
    }

    public ListenerRegistration addGroupMessagesListener(String groupId,
                                                         com.google.firebase.firestore.EventListener<QuerySnapshot> listener) {
        return db.collection("groups")
                .document(groupId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener(listener);
    }

    // Direct Messages
    public String createChatRoomId(String userId1, String userId2) {
        String[] sortedIds = {userId1, userId2};
        Arrays.sort(sortedIds);
        return sortedIds[0] + "_" + sortedIds[1];
    }

    public Task<DocumentReference> sendDirectMessage(String chatRoomId, Message message) {
        Map<String, Object> chatMetadata = new HashMap<>();
        chatMetadata.put("lastMessage", message.getContent());
        chatMetadata.put("lastMessageTime", message.getTimestamp());
        chatMetadata.put("participants", Arrays.asList(
                message.getSenderId(),
                chatRoomId.replace(message.getSenderId() + "_", "")
                        .replace("_" + message.getSenderId(), "")
        ));

        db.collection("direct_messages")
                .document(chatRoomId)
                .set(chatMetadata, SetOptions.merge());

        return db.collection("direct_messages")
                .document(chatRoomId)
                .collection("messages")
                .add(message);
    }

    public ListenerRegistration addDirectMessagesListener(String chatRoomId,
                                                          com.google.firebase.firestore.EventListener<QuerySnapshot> listener) {
        return db.collection("direct_messages")
                .document(chatRoomId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener(listener);
    }

    // User Chats
    public Task<QuerySnapshot> getUserChats(String userId) {
        return db.collection("direct_messages")
                .whereArrayContains("participants", userId)
                .orderBy("lastMessageTime", Query.Direction.DESCENDING)
                .get();
    }

    public ListenerRegistration addUserChatsListener(String userId,
                                                     com.google.firebase.firestore.EventListener<QuerySnapshot> listener) {
        return db.collection("direct_messages")
                .whereArrayContains("participants", userId)
                .orderBy("lastMessageTime", Query.Direction.DESCENDING)
                .addSnapshotListener(listener);
    }

    // User Profile Operations
    public Task<DocumentSnapshot> getUserProfile(String userId) {
        return db.collection("users").document(userId).get();
    }

    public Task<Void> updateUserProfile(String userId, Map<String, Object> updates) {
        return db.collection("users").document(userId).update(updates);
    }

    public void updateUserOnlineStatus(String userId, boolean online) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("online", online);
        updates.put("lastSeen", new Date());

        db.collection("users").document(userId).update(updates);
    }

    // Cleanup
    public void removeListeners(ListenerRegistration... listeners) {
        for (ListenerRegistration listener : listeners) {
            if (listener != null) {
                listener.remove();
            }
        }
    }

    // Helper method to get current user ID
    public String getCurrentUserId() {
        return auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;
    }
}