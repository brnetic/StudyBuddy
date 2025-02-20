package com.example.studybuddy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.studybuddy.R;
import com.example.studybuddy.models.User;
import com.example.studybuddy.utils.ValidationUtils;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {
    private TextInputEditText nameInput, emailInput, passwordInput, confirmPasswordInput;
    private AutoCompleteTextView courseSpinner;
    private ChipGroup selectedCoursesChipGroup;
    private Button registerButton;
    private ProgressBar progressBar;

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    public List<String> selectedCourses;
    private List<String> availableCourses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize lists
        selectedCourses = new ArrayList<>();
        availableCourses = Arrays.asList(
                "Computer Science", "Mathematics", "Physics",
                "Chemistry", "Biology", "Engineering"
        ); // Add more courses as needed

        initializeViews();
        setupSpinner();
        setupButtons();
    }

    private void initializeViews() {
        nameInput = findViewById(R.id.nameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput); // Added confirm password input
        courseSpinner = findViewById(R.id.courseSpinner);
        selectedCoursesChipGroup = findViewById(R.id.selectedCoursesChipGroup);
        registerButton = findViewById(R.id.registerButton);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                availableCourses
        );
        courseSpinner.setAdapter(adapter);
        courseSpinner.setOnItemClickListener((parent, view, position, id) -> {
            String selectedCourse = availableCourses.get(position);
            addCourse(selectedCourse);
            courseSpinner.setText(""); // Clear the spinner text after selection
        });
    }

    private void setupButtons() {
        registerButton.setOnClickListener(v -> registerUser());
    }

    public void addCourse(String course) {
        if (!selectedCourses.contains(course)) {
            selectedCourses.add(course);
            addChip(course);
        } else {
            Toast.makeText(this, "Course already selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void addChip(String course) {
        Chip chip = new Chip(this);
        chip.setText(course);
        chip.setCloseIconVisible(true);
        chip.setOnCloseIconClickListener(v -> {
            selectedCoursesChipGroup.removeView(chip);
            selectedCourses.remove(course);
        });
        selectedCoursesChipGroup.addView(chip);
    }

    public boolean validateInputs(String name, String email, String password, String confirmPassword) {
        if (!ValidationUtils.isValidUsername(name)) {
            nameInput.setError("Please enter a valid name (3-30 characters)");
            return false;
        }
        if (!ValidationUtils.isValidEmail(email)) {
            emailInput.setError("Please enter a valid email");
            return false;
        }
        if (!ValidationUtils.isValidPassword(password)) {
            passwordInput.setError(ValidationUtils.getPasswordRequirements());
            return false;
        }
        if (!password.equals(confirmPassword)) {
            confirmPasswordInput.setError("Passwords do not match");
            return false;
        }
        if (selectedCourses.isEmpty()) {
            Toast.makeText(this, "Please select at least one course", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void registerUser() {
        String name = nameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString();
        String confirmPassword = confirmPasswordInput.getText().toString();

        if (!validateInputs(name, email, password, confirmPassword)) {
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        registerButton.setEnabled(false);

        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String userId = authResult.getUser().getUid();
                    createUserInFirestore(userId, name, email);
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    registerButton.setEnabled(true);
                    Toast.makeText(RegisterActivity.this,
                            "Registration failed: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void createUserInFirestore(String userId, String name, String email) {
        User user = new User(userId, name, email, selectedCourses);
        db.collection("users")
                .document(userId)
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(RegisterActivity.this,
                            "Registration successful",
                            Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, DashboardActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    registerButton.setEnabled(true);
                    Toast.makeText(RegisterActivity.this,
                            "Failed to create user profile: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }
}
