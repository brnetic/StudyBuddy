package com.example.studybuddy;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class Authenticator extends Activity {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String userid;
    private User currentUser;
    private String userID;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String TAG = "EmailPassword";
    private boolean loggedIn;

    Authenticator(){

    }
    public void createUser(User user, String password) {
        mAuth.createUserWithEmailAndPassword(user.email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            String userid = mAuth.getCurrentUser().getUid();
                            userID = userid;
                            user.setId(userid);
                            currentUser = user;

                            db.collection("users").document(userid)
                                    .set(user)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("CreatingUser", "DocumentSnapshot successfully written!");
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
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());


                        }
                    }
                });


    }
    public void loginUser(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task){
                        if (task.isSuccessful()) {

                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");

                            userID = mAuth.getCurrentUser().getUid();
                            updateCurrentUser();



                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(Authenticator.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            loggedIn = false;

                            }

                        }

                });
    }
    private void updateCurrentUser(){

        System.out.println("Launchingt user");

            db.collection("users").document(userID)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                // Document exists, retrieve data

                                Log.d("Firestore","Success");
                                currentUser = documentSnapshot.toObject(User.class);
                                System.out.println(currentUser.name);


                            } else {
                                // Document does not exist
                                Log.d("Firestore", "No such document");
                                currentUser = null;
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("Firestore", "Error getting document", e);
                        }
                    });





    }
    public User getCurrentUser(){

        return currentUser;
    }


    private void updateUI(FirebaseUser user) {
    }
}
