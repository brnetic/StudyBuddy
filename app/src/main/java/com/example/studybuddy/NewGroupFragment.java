package com.example.studybuddy;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;

public class NewGroupFragment extends Fragment {

    private View view;

    private Button button;
    private EditText groupName;
    private EditText className;
    private Button createGroup;

    private FirestoreHandler firestoreHandler= new FirestoreHandler();

    public NewGroupFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_new_group, container, false);

        button = view.findViewById(R.id.backButton);
        groupName = view.findViewById(R.id.groupNameInput);
        className = view.findViewById(R.id.classInput);

        createGroup = view.findViewById(R.id.createGroupButton);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });

        createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StudyGroup studyGroup = new StudyGroup(groupName.getText().toString().trim(),className.getText().toString().trim(),null);
                firestoreHandler.createNewStudyGroup(studyGroup);
            }
        });


        return view;


    }

}
