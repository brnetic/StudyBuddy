package com.example.studybuddy;

import android.os.Bundle;
import android.text.Layout;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;


public class MyGroupsActivity extends AppCompatActivity {

    private User user;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    private RecyclerView layout;
    private List<StudyGroup> myGroups;
    private StudyGroupAdapter studyGroupAdapter;

    private FirestoreHandler firestoreHandler = new FirestoreHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_groups);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        layout = findViewById(R.id.recyclerView);

        myGroups = firestoreHandler.getStudyGroups(user);




        // Setup ViewPager with FragmentPagerAdapter
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        adapter.addFragment(new MyGroupsFragment(), "My Groups");

        studyGroupAdapter = new StudyGroupAdapter(myGroups);
        studyGroupAdapter.notifyDataSetChanged();

        viewPager.setAdapter(adapter);


        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("My Groups");
                            break;
                    }
                }).attach();
    }
}
