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

public class DirectMessageActivity extends AppCompatActivity {
    private ListView messagesListView;
    private EditText messageInput;
    private ImageButton sendButton;
    private TextView userNameTitle;
    private ProgressBar progressBar;

    private MessagesAdapter messagesAdapter;
    private List<Message> messages;
    private FirebaseFirestore db;
    private String currentUserId;
    private String otherUserId;
    private String chatRoomId;
    private User currentUser;
    private User otherUser;
    private ListenerRegistration messagesListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direct_message);

        // Get other user ID from intent
        otherUserId = getIntent().getStringExtra("userId");
        if (otherUserId == null) {
            Toast.makeText(this, "Error: User ID not provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Create chat room ID by sorting user IDs
        String[] userIds = {currentUserId, otherUserId};
        Arrays.sort(userIds);
        chatRoomId = userIds[0] + "_" + userIds[1];

        initializeViews();
        loadUsers();
        setupListeners();
        setupMessagesListener();
    }

    private void initializeViews() {
        messagesListView = findViewById(R.id.messagesListView);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);
        userNameTitle = findViewById(R.id.userNameTitle);
        progressBar = findViewById(R.id.progressBar);

        messages = new ArrayList<>();
        messagesAdapter = new MessagesAdapter(this, messages, currentUserId);
        messagesListView.setAdapter(messagesAdapter);

        // Disable send button until setup is complete
        sendButton.setEnabled(false);
    }

    private void loadUsers() {
        // Load current user
        db.collection("users")
                .document(currentUserId)
                .get()
                .addOnSuccessListener(document -> {
                    currentUser = document.toObject(User.class);
                    if (currentUser != null) {
                        currentUser.setId(document.getId());
                        sendButton.setEnabled(true);
                    }
                });

        // Load other user
        db.collection("users")
                .document(otherUserId)
                .get()
                .addOnSuccessListener(document -> {
                    otherUser = document.toObject(User.class);
                    if (otherUser != null) {
                        otherUser.setId(document.getId());
                        userNameTitle.setText(otherUser.getName());
                    }
                });
    }

    private void setupListeners() {
        sendButton.setOnClickListener(v -> sendMessage());

        // Set up back button in title bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void setupMessagesListener() {
        progressBar.setVisibility(View.VISIBLE);
        messagesListener = db.collection("direct_messages")
                .document(chatRoomId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    progressBar.setVisibility(View.GONE);

                    if (error != null) {
                        Toast.makeText(this, "Error loading messages", Toast.LENGTH_SHORT).show();
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
        message.setSenderId(currentUserId);
        message.setSenderName(currentUser.getName());
        message.setTimestamp(new Date());

        // Update chat metadata
        Map<String, Object> chatMetadata = new HashMap<>();
        chatMetadata.put("lastMessage", content);
        chatMetadata.put("lastMessageTime", new Date());
        chatMetadata.put("participants", Arrays.asList(currentUserId, otherUserId));

        // Update chat room document
        db.collection("direct_messages")
                .document(chatRoomId)
                .set(chatMetadata, SetOptions.merge());

        // Add message to messages collection
        db.collection("direct_messages")
                .document(chatRoomId)
                .collection("messages")
                .add(message)
                .addOnSuccessListener(documentReference -> {
                    messageInput.setText("");
                    messageInput.setEnabled(true);
                    sendButton.setEnabled(true);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to send message", Toast.LENGTH_SHORT).show();
                    messageInput.setEnabled(true);
                    sendButton.setEnabled(true);
                });
    }

    private void scrollToBottom() {
        messagesListView.post(() ->
                messagesListView.setSelection(messagesAdapter.getCount() - 1));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (messagesListener != null) {
            messagesListener.remove();
        }
    }
}