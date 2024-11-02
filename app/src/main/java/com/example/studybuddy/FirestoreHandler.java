package com.example.studybuddy;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class FirestoreHandler {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<StudyGroup> studyGroups;

    private ArrayList<StudySession> studySessions;

    FirestoreHandler(){

    }

    public ArrayList<StudyGroup> getStudyGroups(User user){
        db.collection("studygroups").whereArrayContains("members",user.getId())
                .get()
                .addOnCompleteListener(task->{


                        if (task.isSuccessful()) {
                            // Document exists, retrieve data

                            QuerySnapshot querySnapshot = task.getResult();
                            if(querySnapshot != null){
                                for(QueryDocumentSnapshot document: querySnapshot){
                                    studyGroups.add(document.toObject(StudyGroup.class));
                                }
                            }

                        } else {
                            // Document does not exist
                            Log.d("Firestore", "No such document");
                            studyGroups = null;
                        }


                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Firestore", "Error getting document", e);
                    }
                });

        return studyGroups;
    }
    public ArrayList<StudySession> getStudySessions(StudyGroup studyGroup){
        db.collection("studysessions").whereEqualTo("studyGroup",studyGroup)
                .get()
                .addOnCompleteListener(task->{


                    if (task.isSuccessful()) {
                        // Document exists, retrieve data

                        QuerySnapshot querySnapshot = task.getResult();
                        if(querySnapshot != null){
                            for(QueryDocumentSnapshot document: querySnapshot){

                                studySessions.add(document.toObject(StudySession.class));
                            }
                        }

                    } else {
                        // Document does not exist
                        Log.d("Firestore", "No such document");
                        studySessions = null;
                    }


                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Firestore", "Error getting document", e);
                    }
                });
        return studySessions;
    }

}
