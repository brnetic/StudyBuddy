package com.example.studybuddy.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.studybuddy.R;
import com.example.studybuddy.models.User;
import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {
    private final List<User> users;
    private final OnUserSelectedListener listener;

    public interface OnUserSelectedListener {
        void onUserSelected(User user);
    }

    public UsersAdapter(List<User> users, OnUserSelectedListener listener) {
        this.users = users;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user_select, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = users.get(position);
        holder.nameText.setText(user.getName());
        holder.emailText.setText(user.getEmail());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onUserSelected(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView nameText;
        final TextView emailText;

        ViewHolder(View view) {
            super(view);
            nameText = view.findViewById(R.id.nameText);
            emailText = view.findViewById(R.id.emailText);
        }
    }
}