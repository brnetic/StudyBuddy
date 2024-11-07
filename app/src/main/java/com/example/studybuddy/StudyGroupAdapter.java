package com.example.studybuddy;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class StudyGroupAdapter extends RecyclerView.Adapter<StudyGroupAdapter.StudyGroupViewHolder> {

    private List<StudyGroup> studyGroups;

    public StudyGroupAdapter(List<StudyGroup> studyGroups) {
        this.studyGroups = studyGroups;
    }

    @NonNull
    @Override
    public StudyGroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_study_group, parent, false);
        return new StudyGroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudyGroupViewHolder holder, int position) {
        StudyGroup studyGroup = studyGroups.get(position);
        holder.classNameTextView.setText("Class: " + studyGroup.course);
        holder.groupNameTextView.setText("Study Group: " + studyGroup.name);
        holder.membersTextView.setText("Members: " + String.join(", ", studyGroup.members));
    }

    @Override
    public int getItemCount() {
        return studyGroups.size();
    }

    static class StudyGroupViewHolder extends RecyclerView.ViewHolder {
        TextView classNameTextView, groupNameTextView, membersTextView;

        public StudyGroupViewHolder(@NonNull View itemView) {
            super(itemView);
            classNameTextView = itemView.findViewById(R.id.classNameTextView);
            groupNameTextView = itemView.findViewById(R.id.groupNameTextView);
            membersTextView = itemView.findViewById(R.id.membersTextView);
        }
    }
}
