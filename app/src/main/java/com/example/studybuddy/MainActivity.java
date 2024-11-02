package com.example.studybuddy;



import androidx.activity.EdgeToEdge;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private User user = new User();
    private FirestoreHandler firestoreHandler = new FirestoreHandler();
    private ArrayList<StudyGroup> studyGroups;
    private TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        user = (User) getIntent().getExtras().get("user");
        TextView tv = findViewById(R.id.text123);
        tv.setText("Hi" + user.name);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        StudyGroup[] studyGroupsArray = {new StudyGroup("1",new Course(),new ArrayList<String>()),new StudyGroup("2",new Course(),new ArrayList<String>()),new StudyGroup("3",new Course(),new ArrayList<String>())};
        List<StudyGroup> example =  Arrays.asList(studyGroupsArray);
        List<StudyGroup> studyGroupList = firestoreHandler.getStudyGroups(user);

        // Set up adapter
        if (studyGroupList == null) {
            StudyGroupAdapter adapter = new StudyGroupAdapter(example);
            recyclerView.setAdapter(adapter);
        }


    }

    public void onStart(){

        super.onStart();


    }


}