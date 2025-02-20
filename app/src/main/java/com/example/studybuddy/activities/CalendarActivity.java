package com.example.studybuddy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.studybuddy.R;
import com.example.studybuddy.adapters.SessionsAdapter;
import com.example.studybuddy.models.Session;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class CalendarActivity extends AppCompatActivity {
    private CalendarView calendarView;
    private ListView sessionsListView;
    private TextView noSessionsText;
    private FloatingActionButton addSessionFab;
    private ProgressBar progressBar;

    private FirebaseFirestore db;
    private String groupId;
    private String selectedDate;
    private SessionsAdapter sessionsAdapter;
    private List<Session> sessionsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();

        // Get group ID from intent
        groupId = getIntent().getStringExtra("groupId");

        if (groupId == null) {
            // If no groupId provided, load all sessions for all user's groups
            loadUserGroups();
        }

        // Initialize UI elements
        initializeViews();
        setupCalendarView();
        setupAddButton();

        // Initialize sessions list
        sessionsList = new ArrayList<>();
        sessionsAdapter = new SessionsAdapter(this, sessionsList);
        sessionsListView.setAdapter(sessionsAdapter);

        // Set initial date
        selectedDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                .format(new Date());
        loadSessions();
    }

    private void initializeViews() {
        calendarView = findViewById(R.id.calendarView);
        sessionsListView = findViewById(R.id.sessionsListView);
        noSessionsText = findViewById(R.id.noSessionsText);
        addSessionFab = findViewById(R.id.addSessionFab);
        progressBar = findViewById(R.id.progressBar);

        if (groupId == null) {
            // Hide add button if not in a specific group
            addSessionFab.setVisibility(View.GONE);
        }
    }

    private void setupCalendarView() {
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d",
                    dayOfMonth, month + 1, year);
            loadSessions();
        });
    }

    private void setupAddButton() {
        addSessionFab.setOnClickListener(v -> {
            if (groupId != null) {
                Intent intent = new Intent(CalendarActivity.this, AddSessionActivity.class);
                intent.putExtra("groupId", groupId);
                startActivity(intent);
            }
        });
    }

    private void loadUserGroups() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("groups")
                .whereArrayContains("members", currentUserId)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(this, "Error loading groups", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value != null) {
                        List<String> groupIds = new ArrayList<>();
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            groupIds.add(doc.getId());
                        }
                        loadSessionsForGroups(groupIds);
                    }
                });
    }

    private void loadSessionsForGroups(List<String> groupIds) {
        progressBar.setVisibility(View.VISIBLE);
        sessionsList.clear();

        for (String gId : groupIds) {
            db.collection("groups")
                    .document(gId)
                    .collection("sessions")
                    .whereEqualTo("date", selectedDate)
                    .addSnapshotListener((value, error) -> {
                        progressBar.setVisibility(View.GONE);
                        if (error != null) {
                            Toast.makeText(this, "Error loading sessions", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (value != null) {
                            for (DocumentSnapshot doc : value.getDocuments()) {
                                Session session = doc.toObject(Session.class);
                                if (session != null) {
                                    session.setId(doc.getId());
                                    session.setGroupId(gId);
                                    sessionsList.add(session);
                                }
                            }
                            updateUI();
                        }
                    });
        }
    }

    private void loadSessions() {
        if (groupId == null) {
            loadUserGroups();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        db.collection("groups")
                .document(groupId)
                .collection("sessions")
                .whereEqualTo("date", selectedDate)
                .addSnapshotListener((value, error) -> {
                    progressBar.setVisibility(View.GONE);
                    if (error != null) {
                        Toast.makeText(this, "Error loading sessions", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    sessionsList.clear();
                    if (value != null) {
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            Session session = doc.toObject(Session.class);
                            if (session != null) {
                                session.setId(doc.getId());
                                session.setGroupId(groupId);
                                sessionsList.add(session);
                            }
                        }
                    }

                    updateUI();
                });
    }

    private void updateUI() {
        if (sessionsList.isEmpty()) {
            noSessionsText.setVisibility(View.VISIBLE);
            sessionsListView.setVisibility(View.GONE);
        } else {
            noSessionsText.setVisibility(View.GONE);
            sessionsListView.setVisibility(View.VISIBLE);
            sessionsAdapter.notifyDataSetChanged();
        }
    }
}