package com.example.studybuddy.utils;

import com.example.studybuddy.models.Group;
import com.example.studybuddy.models.User;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.*;

import java.util.*;

public class GroupManager {
    private static final int MAX_MEMBERS = 100;
    private static final int MIN_MEMBERS = 5;

    private final FirebaseFirestore db;

    public GroupManager() {
        this.db = FirebaseUtils.getFirestore();
    }

    // Callbacks
    public interface GroupCallback {
        void onSuccess();
        void onError(String error);
    }

    public interface GroupsLoadCallback {
        void onGroupsLoaded(List<Group> groups);
        void onError(String error);
    }

    // Group creation
    public void createGroup(String name, String courseName, User creator, GroupCallback callback) {
        // Check if group name exists
        db.collection("groups")
                .whereEqualTo("name", name)
                .whereEqualTo("courseName", courseName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().isEmpty()) {
                        Group group = new Group(name, courseName);
                        group.addMember(creator.getId());
                        group.setCreatedBy(creator.getId());

                        db.collection("groups")
                                .add(group)
                                .addOnSuccessListener(ref -> callback.onSuccess())
                                .addOnFailureListener(e ->
                                        callback.onError("Failed to create group: " + e.getMessage()));
                    } else {
                        callback.onError("A group with this name already exists");
                    }
                });
    }

    // Group membership management
    public void joinGroup(Group group, User user, GroupCallback callback) {
        if (group.isMember(user.getId())) {
            callback.onError("Already a member of this group");
            return;
        }

        if (group.getMembers().size() >= MAX_MEMBERS) {
            callback.onError("Group has reached maximum capacity");
            return;
        }

        db.collection("groups")
                .document(group.getId())
                .update("members", FieldValue.arrayUnion(user.getId()))
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e ->
                        callback.onError("Failed to join group: " + e.getMessage()));
    }

    public void leaveGroup(Group group, User user, GroupCallback callback) {
        if (!group.isMember(user.getId())) {
            callback.onError("Not a member of this group");
            return;
        }

        if (group.getMembers().size() <= MIN_MEMBERS) {
            callback.onError("Group requires minimum " + MIN_MEMBERS + " members");
            return;
        }

        db.collection("groups")
                .document(group.getId())
                .update("members", FieldValue.arrayRemove(user.getId()))
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e ->
                        callback.onError("Failed to leave group: " + e.getMessage()));
    }

    // Group loading
    public void loadUserGroups(String userId, GroupsLoadCallback callback) {
        db.collection("groups")
                .whereArrayContains("members", userId)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        callback.onError("Failed to load groups: " + error.getMessage());
                        return;
                    }

                    List<Group> groups = new ArrayList<>();
                    for (DocumentSnapshot doc : value.getDocuments()) {
                        Group group = doc.toObject(Group.class);
                        if (group != null) {
                            group.setId(doc.getId());
                            groups.add(group);
                        }
                    }
                    callback.onGroupsLoaded(groups);
                });
    }

    // Group search
    public void searchGroups(String courseName, String query, GroupsLoadCallback callback) {
        db.collection("groups")
                .whereEqualTo("courseName", courseName)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Group> groups = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Group group = doc.toObject(Group.class);
                        if (group != null &&
                                (query.isEmpty() ||
                                        group.getName().toLowerCase().contains(query.toLowerCase()))) {
                            group.setId(doc.getId());
                            groups.add(group);
                        }
                    }
                    callback.onGroupsLoaded(groups);
                })
                .addOnFailureListener(e ->
                        callback.onError("Failed to search groups: " + e.getMessage()));
    }

    // Group deletion
    public void deleteGroup(Group group, String userId, GroupCallback callback) {
        if (!group.isAdmin(userId)) {
            callback.onError("Only group admin can delete the group");
            return;
        }

        // Delete all related collections first
        deleteGroupCollections(group.getId())
                .addOnSuccessListener(aVoid -> {
                    // Then delete the group document
                    db.collection("groups")
                            .document(group.getId())
                            .delete()
                            .addOnSuccessListener(v -> callback.onSuccess())
                            .addOnFailureListener(e ->
                                    callback.onError("Failed to delete group: " + e.getMessage()));
                })
                .addOnFailureListener(e ->
                        callback.onError("Failed to delete group data: " + e.getMessage()));
    }

    private Task<Void> deleteGroupCollections(String groupId) {
        List<Task<Void>> tasks = new ArrayList<>();

        // Add tasks for each collection
        tasks.add(deleteCollection("messages", groupId));
        tasks.add(deleteCollection("sessions", groupId));
        tasks.add(deleteCollection("resources", groupId));

        return Tasks.whenAll(tasks);
    }

    private Task<Void> deleteCollection(String collectionName, String groupId) {
        return db.collection("groups")
                .document(groupId)
                .collection(collectionName)
                .get()
                .continueWithTask(task -> {
                    List<Task<Void>> deleteTasks = new ArrayList<>();
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                            deleteTasks.add(doc.getReference().delete());
                        }
                    }
                    return Tasks.whenAll(deleteTasks);
                });
    }

    // Member management
    public void removeMember(Group group, String adminId, String memberId, GroupCallback callback) {
        if (!group.isAdmin(adminId)) {
            callback.onError("Only group admin can remove members");
            return;
        }

        if (adminId.equals(memberId)) {
            callback.onError("Admin cannot be removed from the group");
            return;
        }

        if (group.getMembers().size() <= MIN_MEMBERS) {
            callback.onError("Group requires minimum " + MIN_MEMBERS + " members");
            return;
        }

        db.collection("groups")
                .document(group.getId())
                .update("members", FieldValue.arrayRemove(memberId))
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e ->
                        callback.onError("Failed to remove member: " + e.getMessage()));
    }

    // Admin management
    public void transferAdmin(Group group, String currentAdminId, String newAdminId, GroupCallback callback) {
        if (!group.isAdmin(currentAdminId)) {
            callback.onError("Only current admin can transfer admin rights");
            return;
        }

        if (!group.isMember(newAdminId)) {
            callback.onError("New admin must be a group member");
            return;
        }

        // Remove current admin from first position
        List<String> updatedMembers = new ArrayList<>(group.getMembers());
        updatedMembers.remove(currentAdminId);
        // Add current admin to regular members
        updatedMembers.add(currentAdminId);
        // Add new admin at first position
        updatedMembers.remove(newAdminId);
        updatedMembers.add(0, newAdminId);

        db.collection("groups")
                .document(group.getId())
                .update("members", updatedMembers)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e ->
                        callback.onError("Failed to transfer admin rights: " + e.getMessage()));
    }

    // Group update
    public void updateGroupInfo(Group group, String adminId, String newName, String newCourseName, GroupCallback callback) {
        if (!group.isAdmin(adminId)) {
            callback.onError("Only group admin can update group information");
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        if (newName != null && !newName.isEmpty()) {
            updates.put("name", newName);
        }
        if (newCourseName != null && !newCourseName.isEmpty()) {
            updates.put("courseName", newCourseName);
        }

        if (updates.isEmpty()) {
            callback.onError("No updates provided");
            return;
        }

        db.collection("groups")
                .document(group.getId())
                .update(updates)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e ->
                        callback.onError("Failed to update group: " + e.getMessage()));
    }

    // Group statistics
    public void getGroupStats(String groupId, GroupStatsCallback callback) {
        db.collection("groups").document(groupId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Map<String, Object> stats = new HashMap<>();
                    if (documentSnapshot.exists()) {
                        stats.put("memberCount",
                                ((List<?>) documentSnapshot.get("members")).size());
                    }
                    callback.onStatsLoaded(stats);
                })
                .addOnFailureListener(e ->
                        callback.onError(e.getMessage()));
    }

    public interface GroupStatsCallback {
        void onStatsLoaded(Map<String, Object> stats);
        void onError(String error);
    }
}