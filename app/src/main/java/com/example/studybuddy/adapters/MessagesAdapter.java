package com.example.studybuddy.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.studybuddy.R;
import com.example.studybuddy.models.Message;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class MessagesAdapter extends ArrayAdapter<Message> {
    private final Context context;
    private final List<Message> messages;
    private final String currentUserId;
    private final SimpleDateFormat timeFormat;

    public MessagesAdapter(Context context, List<Message> messages, String currentUserId) {
        super(context, 0, messages);
        this.context = context;
        this.messages = messages;
        this.currentUserId = currentUserId;
        this.timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Message message = messages.get(position);
        View view;

        if (message.getSenderId().equals(currentUserId)) {
            view = LayoutInflater.from(context)
                    .inflate(R.layout.item_message_sent, parent, false);
        } else {
            view = LayoutInflater.from(context)
                    .inflate(R.layout.item_message_received, parent, false);
            TextView senderNameText = view.findViewById(R.id.senderNameText);
            senderNameText.setText(message.getSenderName());
        }

        TextView messageText = view.findViewById(R.id.messageText);
        TextView timeText = view.findViewById(R.id.timeText);

        messageText.setText(message.getContent());
        if (message.getTimestamp() != null) {
            timeText.setText(timeFormat.format(message.getTimestamp()));
        }

        return view;
    }
}