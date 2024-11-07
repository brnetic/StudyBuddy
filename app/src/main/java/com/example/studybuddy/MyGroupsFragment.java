package com.example.studybuddy;

import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MyGroupsFragment extends Fragment {

    private FloatingActionButton button;

    private View view;

    public MyGroupsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my_groups, container, false);
        View view1 = inflater.inflate(R.layout.fragment_new_group,container,false);

        button = view.findViewById(R.id.roundButton);
        Button b = view1.findViewById(R.id.backButton);



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View newRootView = inflater.inflate(R.layout.fragment_new_group, null);

                // Replace the current root view with the new layout
                ViewGroup parent = (ViewGroup) view.getParent();
                if (parent != null) {
                    parent.removeView(view); // Remove the old view
                }
                Button b = newRootView.findViewById(R.id.backButton);



                // Add the new view
                parent.addView(newRootView);
                view = newRootView;
            }
        });

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View newRootView = inflater.inflate(R.layout.fragment_my_groups, null);

                // Replace the current root view with the new layout
                ViewGroup parent = (ViewGroup) view1.getParent();
                if (parent != null) {
                    parent.removeView(view); // Remove the old view
                }



                // Add the new view
                parent.addView(newRootView);
                view = newRootView;
            }
        });




        return view;
    }
}
