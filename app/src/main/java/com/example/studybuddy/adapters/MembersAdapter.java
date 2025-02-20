package com.example.studybuddy.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.studybuddy.R;
import com.example.studybuddy.activities.DirectMessageActivity;
import com.example.studybuddy.models.User;
import java.util.List;
import android.content.Intent;
import com.example.studybuddy.activities.DirectMessageActivity;

public class MembersAdapter extends ArrayAdapter<User> {
    private Context context;
    private List<User> members;
    private boolean isAdmin;
    private String currentUserId;
    private OnMemberRemoveListener removeListener;

    public interface OnMemberRemoveListener {
        void onMemberRemove(User user);
    }

    public MembersAdapter(Context context, List<User> members, boolean isAdmin,
                          String currentUserId, OnMemberRemoveListener removeListener) {
        super(context, 0, members);
        this.context = context;
        this.members = members;
        this.isAdmin = isAdmin;
        this.currentUserId = currentUserId;
        this.removeListener = removeListener;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_member, parent, false);
        }

        User member = members.get(position);
        TextView memberNameText = convertView.findViewById(R.id.memberNameText);
        ImageView removeButton = convertView.findViewById(R.id.removeButton);

        memberNameText.setText(member.getName());


        if (isAdmin && !member.getId().equals(members.get(0).getId()) &&
                !member.getId().equals(currentUserId)) {
            removeButton.setVisibility(View.VISIBLE);
            removeButton.setOnClickListener(v -> {
                if (removeListener != null) {
                    removeListener.onMemberRemove(member);
                }
            });
        } else {
            removeButton.setVisibility(View.GONE);
        }


        if (!member.getId().equals(currentUserId)) {
            convertView.setOnClickListener(v -> {
                Intent intent = new Intent(context, DirectMessageActivity.class);
                intent.putExtra("userId", member.getId());
                context.startActivity(intent);
            });
            // Make it look clickable
//            convertView.setBackgroundResource(android.R.attr.selectableItemBackground);
        } else {
            convertView.setOnClickListener(null);
            convertView.setBackground(null);
        }

        return convertView;
    }

    @Override
    public int getCount() {
        return members.size();
    }
}