package com.example.studybuddy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.studybuddy.R;
import com.example.studybuddy.adapters.MembersAdapter;
import com.example.studybuddy.models.Group;
import com.example.studybuddy.models.User;
import com.example.studybuddy.utils.FirebaseUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import java.util.*;

public class GroupDetailsActivity extends AppCompatActivity {
    private TextView groupNameTextView;
    private TextView courseNameTextView;
    private ListView membersListView;
    private Button chatButton, calendarButton, addSessionButton;
    private ProgressBar progressBar;

    private FirebaseFirestore db;
    private String groupId;
    private String currentUserId;
    private Group currentGroup;
    private List<User> membersList;
    private MembersAdapter membersAdapter;
    private ListenerRegistration groupListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_details);

        // Get group ID from intent
        groupId = getIntent().getStringExtra("groupId");
        if (groupId == null) {
            Toast.makeText(this, "Error: Group ID not provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        initializeViews();
        setupButtons();
        setupGroupListener();
    }

    private void initializeViews() {
        groupNameTextView = findViewById(R.id.groupNameTextView);
        courseNameTextView = findViewById(R.id.courseNameTextView);
        membersListView = findViewById(R.id.membersListView);
        chatButton = findViewById(R.id.chatButton);
        calendarButton = findViewById(R.id.calendarButton);
        addSessionButton = findViewById(R.id.addSessionButton);
        progressBar = findViewById(R.id.progressBar);

        membersList = new ArrayList<>();
        membersAdapter = new MembersAdapter(
                this,
                membersList,
                false, // Will be updated when group loads
                currentUserId,
                this::removeGroupMember
        );
        membersListView.setAdapter(membersAdapter);
    }

    private void setupButtons() {
        chatButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChatActivity.class);
            intent.putExtra("groupId", groupId);
            startActivity(intent);
        });

        calendarButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, CalendarActivity.class);
            intent.putExtra("groupId", groupId);
            startActivity(intent);
        });

        addSessionButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddSessionActivity.class);
            intent.putExtra("groupId", groupId);
            startActivity(intent);
        });
    }

    private void setupGroupListener() {
        progressBar.setVisibility(View.VISIBLE);
        groupListener = db.collection("groups")
                .document(groupId)
                .addSnapshotListener((documentSnapshot, error) -> {
                    progressBar.setVisibility(View.GONE);

                    if (error != null) {
                        Toast.makeText(this, "Error loading group details", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        currentGroup = documentSnapshot.toObject(Group.class);
                        if (currentGroup != null) {
                            currentGroup.setId(documentSnapshot.getId());
                            updateUI();
                            loadMembers();
                        }
                    }
                });
    }

    private void loadMembers() {
        if (currentGroup == null || currentGroup.getMembers() == null) return;

        membersList.clear();
        for (String memberId : currentGroup.getMembers()) {
            db.collection("users")
                    .document(memberId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        User member = documentSnapshot.toObject(User.class);
                        if (member != null) {
                            member.setId(documentSnapshot.getId());
                            membersList.add(member);
                            membersAdapter.notifyDataSetChanged();
                        }
                        if (membersList.size() == currentGroup.getMembers().size()) {
                            progressBar.setVisibility(View.GONE);
                        }
                    })
                    .addOnFailureListener(e -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(this, "Error loading members", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void updateUI() {
        if (currentGroup != null) {
            groupNameTextView.setText(currentGroup.getName());
            courseNameTextView.setText(currentGroup.getCourseName());

            boolean isMember = currentGroup.getMembers().contains(currentUserId);
            boolean isAdmin = currentGroup.isAdmin(currentUserId);

            // Update adapter with admin status
            membersAdapter = new MembersAdapter(
                    this,
                    membersList,
                    isAdmin,
                    currentUserId,
                    this::removeGroupMember
            );
            membersListView.setAdapter(membersAdapter);

            // Show/hide action buttons based on membership
            if (isMember) {
                addSessionButton.setVisibility(View.VISIBLE);
                chatButton.setVisibility(View.VISIBLE);
                calendarButton.setVisibility(View.VISIBLE);
            } else {
                addSessionButton.setVisibility(View.GONE);
                chatButton.setVisibility(View.GONE);
                calendarButton.setVisibility(View.GONE);
            }
        }
    }

    private void removeGroupMember(User user) {
        if (currentGroup != null && currentGroup.isAdmin(currentUserId)) {
            if (currentGroup.getMembers().size() <= 2) {
                Toast.makeText(this, "Group must have at least 2 members", Toast.LENGTH_SHORT).show();
                return;
            }

            db.collection("groups")
                    .document(groupId)
                    .update("members", FieldValue.arrayRemove(user.getId()))
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Member removed successfully", Toast.LENGTH_SHORT).show();
                        membersList.remove(user);
                        membersAdapter.notifyDataSetChanged();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this,
                                "Failed to remove member: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (groupListener != null) {
            groupListener.remove();
        }
    }
}