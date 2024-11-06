package com.example.studybuddy;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;


public class RegisterActivity extends AppCompatActivity {

    private User user = new User();

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String TAG = "EmailPassword";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button registerButton = findViewById(R.id.registerButton);
        EditText nameEditText= findViewById(R.id.registerUsername);
        EditText emailEditText = findViewById(R.id.registerEmail);
        EditText passwordEditText = findViewById(R.id.registerPassword);
        EditText confirmPasswordEditText = findViewById(R.id.registerConfirmPassword);

        TextView loginLink = findViewById(R.id.loginLink);

        // Handle the register button click
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement registration logic here (e.g., validate input, save data)
                // For now, just navigate to DashboardActivity as an example
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String confirmPassword = confirmPasswordEditText.getText().toString().trim();
                String name = nameEditText.getText().toString().trim();
                if (email.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                } else if (password.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Please enter your password", Toast.LENGTH_SHORT).show();

                }
                else if(!password.equals(confirmPassword) ) {
                    Toast.makeText(RegisterActivity.this, "Passwords don't match", Toast.LENGTH_SHORT).show();
                }else if (isValidEmail(email) && password.length() >= 6 ) {
                    createUser(email,name, password);





                } else {
                    Toast.makeText(RegisterActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                }


            }
        });

        // Handle the login link click (back to login page)
        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void createUser(String email,String name, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            user.name = name;
                            user.email = email;
                            String userid = mAuth.getCurrentUser().getUid();
                            user.setId(userid);


                            db.collection("users").document(userid)
                                    .set(user)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("CreatingUser", "DocumentSnapshot successfully written!");
                                            Intent intent = new Intent(RegisterActivity.this, DashboardActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("CreatingUser", "Error writing document", e);
                                        }
                                    });

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(RegisterActivity.this, task.getException().getLocalizedMessage(),Toast.LENGTH_SHORT).show();


                        }
                    }
                });
    }

}
