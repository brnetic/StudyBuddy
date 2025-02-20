package com.example.studybuddy.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.studybuddy.R;
import com.example.studybuddy.adapters.UsersAdapter;
import com.example.studybuddy.models.Group;
import com.example.studybuddy.models.User;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CreateGroupDialogFragment extends DialogFragment {
    private TextInputEditText groupNameInput;
    private AutoCompleteTextView courseSpinner;
    private RecyclerView usersRecyclerView;
    private ChipGroup selectedUsersChipGroup;
    private Button createButton;
    private ProgressBar progressBar;

    private FirebaseFirestore db;
    private String currentUserId;
    private Context context;
    private OnGroupCreatedListener listener;
    private Set<String> selectedUserIds = new HashSet<>();
    private List<User> allUsers = new ArrayList<>();

    public interface OnGroupCreatedListener {
        void onGroupCreated();
    }

    public void setOnGroupCreatedListener(OnGroupCreatedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_create_group_with_members, null);

        initializeViews(view);
        setupCourseSpinner();
        loadUsers();

        builder.setView(view)
                .setTitle("Create Group")
                .setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());

        return builder.create();
    }

    private void initializeViews(View view) {
        groupNameInput = view.findViewById(R.id.groupNameInput);
        courseSpinner = view.findViewById(R.id.courseSpinner);
        usersRecyclerView = view.findViewById(R.id.usersRecyclerView);
        selectedUsersChipGroup = view.findViewById(R.id.selectedUsersChipGroup);
        createButton = view.findViewById(R.id.createButton);
        progressBar = view.findViewById(R.id.progressBar);

        db = FirebaseFirestore.getInstance();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Add current user to selected users
        selectedUserIds.add(currentUserId);

        createButton.setOnClickListener(v -> createGroup());

        usersRecyclerView.setLayoutManager(new LinearLayoutManager(context));
    }

    private void setupCourseSpinner() {
        List<String> courses = Arrays.asList(
                "Computer Science",
                "Mathematics",
                "Physics",
                "Chemistry",
                "Biology",
                "Engineering"
        );

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                courses
        );
        courseSpinner.setAdapter(adapter);
    }

    private void loadUsers() {
        progressBar.setVisibility(View.VISIBLE);
        db.collection("users")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    allUsers.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        User user = document.toObject(User.class);
                        if (user != null) {
                            // Set the ID first before any comparisons
                            user.setId(document.getId());
                            // Only add other users, not the current user
                            if (!document.getId().equals(currentUserId)) {
                                allUsers.add(user);
                            }
                        }
                    }
                    setupUsersAdapter();
                    progressBar.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to load users", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                });
    }

    private void setupUsersAdapter() {
        UsersAdapter adapter = new UsersAdapter(allUsers, user -> {
            String userId = user.getId();
            if (userId != null) {  // Add null check
                if (selectedUserIds.contains(userId)) {
                    selectedUserIds.remove(userId);
                    removeUserChip(user);
                } else {
                    selectedUserIds.add(userId);
                    addUserChip(user);
                }
                updateCreateButtonState();
            }
        });
        usersRecyclerView.setAdapter(adapter);
    }

    private void addUserChip(User user) {
        if (user.getId() == null) return;  // Add null check

        Chip chip = new Chip(requireContext());
        chip.setText(user.getName() != null ? user.getName() : "Unknown User");
        chip.setCloseIconVisible(true);
        chip.setTag(user.getId());
        chip.setOnCloseIconClickListener(v -> {
            selectedUserIds.remove(user.getId());
            selectedUsersChipGroup.removeView(chip);
            updateCreateButtonState();
        });
        selectedUsersChipGroup.addView(chip);
    }

    private void removeUserChip(User user) {
        if (user.getId() == null) return;  // Add null check

        for (int i = 0; i < selectedUsersChipGroup.getChildCount(); i++) {
            Chip chip = (Chip) selectedUsersChipGroup.getChildAt(i);
            if (chip.getTag() != null && chip.getTag().equals(user.getId())) {  // Add null check
                selectedUsersChipGroup.removeView(chip);
                break;
            }
        }
    }

    private void updateCreateButtonState() {
        // Add check to prevent NPE
        if (createButton != null) {
            createButton.setEnabled(selectedUserIds.size() >= 5);
        }
    }

    private void createGroup() {
        String groupName = groupNameInput.getText().toString().trim();
        String courseName = courseSpinner.getText().toString().trim();

        if (groupName.isEmpty() || courseName.isEmpty()) {
            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedUserIds.size() < 5) {
            Toast.makeText(context, "Please select at least 5 members", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        createButton.setEnabled(false);

        // Create group documen
        Group group = new Group();
        group.setName(groupName);
        group.setCourseName(courseName);
        group.setCreatedBy(currentUserId);
        group.setCreatedAt(new Date());
        group.setMembers(new ArrayList<>(selectedUserIds));

        db.collection("groups")
                .add(group)
                .addOnSuccessListener(documentReference -> {
                    String groupId = documentReference.getId();
                    documentReference.update("id", groupId)
                            .addOnSuccessListener(aVoid -> {
                                if (context != null) {
                                    Toast.makeText(context,
                                            "Group created successfully", Toast.LENGTH_SHORT).show();
                                }
                                if (listener != null) {
                                    listener.onGroupCreated();
                                }
                                dismiss();
                            })
                            .addOnFailureListener(e -> {
                                progressBar.setVisibility(View.GONE);
                                createButton.setEnabled(true);
                                if (context != null) {
                                    Toast.makeText(context,
                                            "Failed to update group ID", Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    createButton.setEnabled(true);
                    if (context != null) {
                        Toast.makeText(context,
                                "Failed to create group: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
// working
    @Override
    public void onDetach() {
        super.onDetach();
        context = null;
    }
}