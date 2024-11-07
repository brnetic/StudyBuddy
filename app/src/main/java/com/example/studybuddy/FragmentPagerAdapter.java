package com.example.studybuddy;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

public class FragmentPagerAdapter extends androidx.viewpager2.adapter.FragmentStateAdapter {
    public FragmentPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @Override
    public Fragment createFragment(int position) {
        // Return the appropriate fragment for each tab
        switch (position) {
            case 0:
                return new MyGroupsFragment();
            case 1:
                return new SessionsFragment();
            case 2:
                return new ResourcesFragment();
            default:
                return new MyGroupsFragment(); // Default to MyGroupsFragment
        }
    }

    @Override
    public int getItemCount() {
        return 3; // Number of tabs (My Groups, Sessions, Resources)
    }
}
