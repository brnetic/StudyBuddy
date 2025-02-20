package com.example.studybuddy.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.studybuddy.R;
import com.example.studybuddy.models.Group;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;
import java.util.List;

public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.ViewHolder> {
    private final Context context;
    private final List<Group> groups;
    private final String currentUserId;
    private final FirebaseFirestore db;
    private final OnGroupClickListener listener;

    public interface OnGroupClickListener {
        void onGroupClick(Group group);
    }

    public GroupsAdapter(Context context, List<Group> groups, OnGroupClickListener listener) {
        this.context = context;
        this.groups = groups;
        this.listener = listener;
        this.currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_group, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Group group = groups.get(position);
        holder.groupNameText.setText(group.getName());
        holder.courseNameText.setText(group.getCourseName());

        // Set join/leave button text and click listener
        boolean isMember = group.getMembers().contains(currentUserId);
        holder.actionButton.setText(isMember ? "Leave" : "Join");

        holder.actionButton.setOnClickListener(v -> {
            if (isMember) {
                leaveGroup(group, holder.getAdapterPosition());
            } else {
                joinGroup(group, holder.getAdapterPosition());
            }
        });

        // Set click listener for the whole item
        holder.itemView.setOnClickListener(v -> {
            if (isMember) {
                listener.onGroupClick(group);
            } else {
                Toast.makeText(context, "Join the group to view details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView groupNameText;
        final TextView courseNameText;
        final Button actionButton;

        ViewHolder(View itemView) {
            super(itemView);
            groupNameText = itemView.findViewById(R.id.groupNameText);
            courseNameText = itemView.findViewById(R.id.courseNameText);
            actionButton = itemView.findViewById(R.id.actionButton);
        }
    }

    private void joinGroup(Group group, int position) {
        db.collection("groups")
                .document(group.getId())
                .update("members", FieldValue.arrayUnion(currentUserId))
                .addOnSuccessListener(aVoid -> {
                    group.getMembers().add(currentUserId);
                    notifyItemChanged(position);
                    Toast.makeText(context, "Successfully joined group", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(context, "Failed to join group: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show());
    }

    private void leaveGroup(Group group, int position) {
        if (group.getMembers().size() <= 2) {
            Toast.makeText(context, "Group requires minimum 5 members", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("groups")
                .document(group.getId())
                .update("members", FieldValue.arrayRemove(currentUserId))
                .addOnSuccessListener(aVoid -> {
                    group.getMembers().remove(currentUserId);
                    notifyItemChanged(position);
                    Toast.makeText(context, "Successfully left group", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(context, "Failed to leave group: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show());
    }
}