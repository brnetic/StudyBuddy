package com.example.studybuddy;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class SessionsActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sessions);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        // Setup ViewPager with FragmentPagerAdapter
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        adapter.addFragment(new SessionsFragment(), "Sessions");
        // Add more fragments if necessary
        viewPager.setAdapter(adapter);

        // Setup TabLayout with ViewPager2
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    // Set the tab text based on position
                    switch (position) {
                        case 0:
                            tab.setText("Sessions");
                            break;
                        // Add cases for more fragments if necessary
                    }
                }).attach();
    }
}
