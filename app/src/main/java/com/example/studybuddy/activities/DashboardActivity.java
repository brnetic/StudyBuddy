package com.example.studybuddy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import com.example.studybuddy.R;
import com.example.studybuddy.fragments.HomeFragment;
import com.example.studybuddy.fragments.ResourcesFragment;
import com.example.studybuddy.models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class DashboardActivity extends AppCompatActivity {
    private static final String TAG = "DashboardActivity";

    private BottomNavigationView bottomNav;
    private Toolbar toolbar;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Initialize views
        toolbar = findViewById(R.id.toolbar);
        bottomNav = findViewById(R.id.bottomNav);
        setSupportActionBar(toolbar);

        setupBottomNavigation();
        loadCurrentUser();

        // Set default fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, new HomeFragment())
                    .commit();
        }
    }

    private void setupBottomNavigation() {
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_home) {
                selectedFragment = new HomeFragment();
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle("Home");
                }
            } else if (itemId == R.id.navigation_groups) {  // Assuming you have this menu item
                selectedFragment = new HomeFragment();  // Re-use HomeFragment for groups
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle("Groups");
                }
            } else if (itemId == R.id.navigation_calendar) {
                Intent intent = new Intent(this, CalendarActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.navigation_resources) {
                selectedFragment = new ResourcesFragment();
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle("Resources");
                }
            } else if (itemId == R.id.navigation_logout) {
                logout();
                return true;
            }

            if (selectedFragment != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, selectedFragment)
                        .commit();
                return true;
            }
            return false;
        });
    }

    private void loadCurrentUser() {
        String userId = auth.getCurrentUser().getUid();
        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    currentUser = documentSnapshot.toObject(User.class);
                    if (currentUser != null) {
                        if (getSupportActionBar() != null) {
                            getSupportActionBar().setTitle("Welcome, " + currentUser.getName());
                        }
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load user data", Toast.LENGTH_SHORT).show());
    }

    private void logout() {
        auth.signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        // Handle back navigation
        Fragment currentFragment = getSupportFragmentManager()
                .findFragmentById(R.id.fragmentContainer);
        if (currentFragment instanceof HomeFragment) {
            super.onBackPressed();
        } else {
            bottomNav.setSelectedItemId(R.id.navigation_home);
        }
    }
}