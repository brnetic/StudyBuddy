package com.example.studybuddy.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.studybuddy.R;
import com.example.studybuddy.adapters.MessagesAdapter;
import com.example.studybuddy.models.Message;
import com.example.studybuddy.models.User;
import com.example.studybuddy.utils.FirebaseUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import java.util.*;

public class ChatActivity extends AppCompatActivity {
    private ListView messagesListView;
    private EditText messageInput;
    private ImageButton sendButton;
    private ProgressBar progressBar;

    private MessagesAdapter messagesAdapter;
    private List<Message> messages;
    private FirebaseFirestore db;
    private String groupId;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Get group ID from intent
        groupId = getIntent().getStringExtra("groupId");
        if (groupId == null) {
            Toast.makeText(this, "Error: Group ID not provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize views and data
        initializeViews();
        initializeFirebase();
        loadCurrentUser();
    }

    private void initializeViews() {
        messagesListView = findViewById(R.id.messagesListView);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);
        progressBar = findViewById(R.id.progressBar);

        messages = new ArrayList<>();
        messagesAdapter = new MessagesAdapter(this, messages,
                FirebaseAuth.getInstance().getCurrentUser().getUid());
        messagesListView.setAdapter(messagesAdapter);

        // Disable send button until user is loaded
        sendButton.setEnabled(false);
    }

    private void initializeFirebase() {
        db = FirebaseFirestore.getInstance();
    }

    private void loadCurrentUser() {
        progressBar.setVisibility(View.VISIBLE);
        FirebaseUtils.getCurrentUser()
                .addOnSuccessListener(user -> {
                    currentUser = user;
                    if (user != null) {
                        setupListeners();
                        loadMessages();
                    } else {
                        Toast.makeText(this, "Failed to load user data",
                                Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error loading user data: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    private void setupListeners() {
        sendButton.setEnabled(true);
        sendButton.setOnClickListener(v -> sendMessage());
    }

    private void loadMessages() {
        progressBar.setVisibility(View.VISIBLE);
        db.collection("groups")
                .document(groupId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    progressBar.setVisibility(View.GONE);
                    if (error != null) {
                        Toast.makeText(ChatActivity.this,
                                "Error loading messages", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    messages.clear();
                    if (value != null) {
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            Message message = doc.toObject(Message.class);
                            if (message != null) {
                                messages.add(message);
                            }
                        }
                    }

                    messagesAdapter.notifyDataSetChanged();
                    scrollToBottom();
                });
    }

    private void sendMessage() {
        String content = messageInput.getText().toString().trim();
        if (content.isEmpty() || currentUser == null) return;

        messageInput.setEnabled(false);
        sendButton.setEnabled(false);

        Message message = new Message();
        message.setContent(content);
        message.setSenderId(currentUser.getId());
        message.setSenderName(currentUser.getName());
        message.setTimestamp(new Date());

        db.collection("groups")
                .document(groupId)
                .collection("messages")
                .add(message)
                .addOnSuccessListener(documentReference -> {
                    messageInput.setText("");
                    messageInput.setEnabled(true);
                    sendButton.setEnabled(true);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ChatActivity.this,
                            "Failed to send message", Toast.LENGTH_SHORT).show();
                    messageInput.setEnabled(true);
                    sendButton.setEnabled(true);
                });
    }

    private void scrollToBottom() {
        messagesListView.post(() ->
                messagesListView.setSelection(messagesAdapter.getCount() - 1));
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Update last read timestamp
        if (currentUser != null) {
            Map<String, Object> update = new HashMap<>();
            update.put("lastRead", new Date());
            db.collection("groups")
                    .document(groupId)
                    .collection("members")
                    .document(currentUser.getId())
                    .update(update);
        }
    }
}