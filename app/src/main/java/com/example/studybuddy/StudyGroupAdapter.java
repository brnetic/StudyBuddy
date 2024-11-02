package com.example.studybuddy;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

public class StudyGroupAdapter extends RecyclerView.Adapter<StudyGroupAdapter.StudyGroupViewHolder> {

    private List<StudyGroup> studyGroupList;

    public StudyGroupAdapter(List<StudyGroup> studyGroupList) {
        this.studyGroupList = studyGroupList;
    }

    @NonNull
    @Override
    public StudyGroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_study_group, parent, false);
        return new StudyGroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudyGroupViewHolder holder, int position) {
        StudyGroup studyGroup = studyGroupList.get(position);
        holder.groupNameTextView.setText(studyGroup.name);
        holder.subjectTextView.setText(studyGroup.course.courseName);
        holder.memberCountTextView.setText("Members: " + studyGroup.members.size());
    }

    @Override
    public int getItemCount() {
        return studyGroupList.size();
    }

    static class StudyGroupViewHolder extends RecyclerView.ViewHolder {
        TextView groupNameTextView;
        TextView subjectTextView;
        TextView memberCountTextView;

        public StudyGroupViewHolder(@NonNull View itemView) {
            super(itemView);
            groupNameTextView = itemView.findViewById(R.id.groupNameTextView);
            subjectTextView = itemView.findViewById(R.id.subjectTextView);
            memberCountTextView = itemView.findViewById(R.id.memberCountTextView);
        }
    }
}