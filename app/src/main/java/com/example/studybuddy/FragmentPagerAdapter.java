package com.example.studybuddy;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class FragmentPagerAdapter extends FragmentStateAdapter {

    public FragmentPagerAdapter(FragmentActivity fa) {
        super(fa);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new MyGroupsFragment();
            case 1:
                return new SessionsFragment();
            case 2:
                return new ResourcesFragment();
            default:
                return new MyGroupsFragment(); // Fallback in case of error
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
