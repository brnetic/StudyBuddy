package com.example.studybuddy.activities;

import android.annotation.SuppressLint; // Add this import
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.studybuddy.R;
import com.example.studybuddy.utils.ValidationUtils;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private EditText emailInput;
    public EditText passwordInput;
    public Button loginButton;
    public Button signupButton;
    public ProgressBar progressBar;
    public FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Check if user is already logged in
        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(this, DashboardActivity.class));
            finish();
            return;
        }

        // Initialize views
        initializeViews();
        setupButtons();
    }

    private void initializeViews() {
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        signupButton = findViewById(R.id.signupButton);
        progressBar = findViewById(R.id.progressBar);
    }

    public void setupButtons() {
        loginButton.setOnClickListener(v -> loginUser());
        signupButton.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    public void loginUser() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        // Validate inputs
        if (!ValidationUtils.isValidEmail(email)) {
            emailInput.setError("Please enter a valid email");
            return;
        }

        if (password.isEmpty()) {
            passwordInput.setError("Password cannot be empty");
            return;
        }

        // Show progress and disable buttons
        progressBar.setVisibility(View.VISIBLE);
        loginButton.setEnabled(false);
        signupButton.setEnabled(false);

        // Attempt login
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    progressBar.setVisibility(View.GONE);
                    startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    loginButton.setEnabled(true);
                    signupButton.setEnabled(true);
                    Toast.makeText(LoginActivity.this,
                            "Login failed: The password is invalid or the user does not exist." , Toast.LENGTH_SHORT).show();
                });
    }

    @SuppressLint("MissingSuperCall") // Suppress the lint warning
    @Override
    public void onBackPressed() {
        // Exit app if on login screen
        finishAffinity();
    }
}
