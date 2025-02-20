package com.example.studybuddy.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.studybuddy.R;
import com.example.studybuddy.models.Session;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class AddSessionActivity extends AppCompatActivity {
    private EditText titleInput, locationInput;
    private Button dateButton, startTimeButton, endTimeButton, createSessionButton;
    private TextView selectedDateTextView, selectedStartTimeTextView, selectedEndTimeTextView;
    private ProgressBar progressBar;

    private String selectedDate, selectedStartTime, selectedEndTime;
    private FirebaseFirestore db;
    private String groupId;
    private String currentUserId;

    private SimpleDateFormat dateTimeFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_session);

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

        // Initialize date/time format
        dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        titleInput = findViewById(R.id.titleInput);
        locationInput = findViewById(R.id.locationInput);
        dateButton = findViewById(R.id.dateButton);
        startTimeButton = findViewById(R.id.startTimeButton);
        endTimeButton = findViewById(R.id.endTimeButton);
        createSessionButton = findViewById(R.id.createSessionButton);
        selectedDateTextView = findViewById(R.id.selectedDateTextView);
        selectedStartTimeTextView = findViewById(R.id.selectedStartTimeTextView);
        selectedEndTimeTextView = findViewById(R.id.selectedEndTimeTextView);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupListeners() {
        dateButton.setOnClickListener(v -> showDatePicker());
        startTimeButton.setOnClickListener(v -> showTimePicker(true));
        endTimeButton.setOnClickListener(v -> showTimePicker(false));
        createSessionButton.setOnClickListener(v -> createSession());
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    selectedDate = String.format("%02d/%02d/%04d",
                            dayOfMonth, monthOfYear + 1, year1);
                    selectedDateTextView.setText("Selected Date: " + selectedDate);
                }, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void showTimePicker(boolean isStartTime) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute1) -> {
                    String time = String.format("%02d:%02d", hourOfDay, minute1);
                    if (isStartTime) {
                        selectedStartTime = time;
                        selectedStartTimeTextView.setText("Start Time: " + time);
                    } else {
                        selectedEndTime = time;
                        selectedEndTimeTextView.setText("End Time: " + time);
                    }
                }, hour, minute, true);
        timePickerDialog.show();
    }

    private void createSession() {
        String title = titleInput.getText().toString().trim();
        String location = locationInput.getText().toString().trim();

        if (title.isEmpty() || location.isEmpty() || selectedDate == null ||
                selectedStartTime == null || selectedEndTime == null) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        createSessionButton.setEnabled(false);

        // Parse and validate dates and times
        try {
            Date startDateTime = dateTimeFormat.parse(selectedDate + " " + selectedStartTime);
            Date endDateTime = dateTimeFormat.parse(selectedDate + " " + selectedEndTime);

            if (startDateTime == null || endDateTime == null) {
                Toast.makeText(this, "Invalid date or time format", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                createSessionButton.setEnabled(true);
                return;
            }

            // Check if start time is before end time
            if (!startDateTime.before(endDateTime)) {
                Toast.makeText(this, "Start time must be before end time", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                createSessionButton.setEnabled(true);
                return;
            }

            // Check if the start time is not in the past
            Date currentDateTime = new Date();
            if (startDateTime.before(currentDateTime)) {
                Toast.makeText(this, "Start time cannot be in the past", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                createSessionButton.setEnabled(true);
                return;
            }

            // Proceed to create the session
            Session session = new Session();
            session.setTitle(title);
            session.setLocation(location);
            session.setDate(selectedDate);
            session.setStartTime(selectedStartTime);
            session.setEndTime(selectedEndTime);
            session.setParticipants(new ArrayList<>());
            session.setGroupId(groupId);
            session.setCreatedBy(currentUserId);
            session.setCreatedAt(new Date());

            db.collection("groups")
                    .document(groupId)
                    .collection("sessions")
                    .add(session)
                    .addOnSuccessListener(documentReference -> {
                        String sessionId = documentReference.getId();
                        documentReference.update("id", sessionId)
                                .addOnSuccessListener(aVoid -> {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(AddSessionActivity.this,
                                            "Session created successfully", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    progressBar.setVisibility(View.GONE);
                                    createSessionButton.setEnabled(true);
                                    Toast.makeText(AddSessionActivity.this,
                                            "Failed to update session ID", Toast.LENGTH_SHORT).show();
                                });
                    })
                    .addOnFailureListener(e -> {
                        progressBar.setVisibility(View.GONE);
                        createSessionButton.setEnabled(true);
                        Toast.makeText(AddSessionActivity.this,
                                "Failed to create session: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    });

        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error parsing date/time", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            createSessionButton.setEnabled(true);
        }
    }
}
