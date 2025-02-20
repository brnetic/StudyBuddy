package com.example.studybuddy.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import com.example.studybuddy.R;
import com.example.studybuddy.adapters.ResourcesAdapter;
import com.example.studybuddy.models.Resource;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.*;

public class ResourcesActivity extends AppCompatActivity {
    private static final int PICK_FILE_REQUEST = 1;

    private ListView resourcesListView;
    private SearchView searchView;
    private TextView noResourcesTextView;
    private FloatingActionButton uploadButton;
    private ProgressBar progressBar;
    private ResourcesAdapter resourcesAdapter;
    private List<Resource> resources;
    private FirebaseFirestore db;
    private String groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resources);

        groupId = getIntent().getStringExtra("groupId");
        if (groupId == null) {
            finish();
            return;
        }

        initializeViews();
        setupListeners();
        loadResources();
    }

    private void initializeViews() {
        resourcesListView = findViewById(R.id.resourcesListView);
        searchView = findViewById(R.id.searchView);
        noResourcesTextView = findViewById(R.id.noResourcesTextView);
        uploadButton = findViewById(R.id.uploadButton);
        progressBar = findViewById(R.id.progressBar);

        resources = new ArrayList<>();
        resourcesAdapter = new ResourcesAdapter(this, resources);
        resourcesListView.setAdapter((ListAdapter) resourcesAdapter);

        db = FirebaseFirestore.getInstance();
    }

    private void setupListeners() {
        uploadButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            startActivityForResult(intent, PICK_FILE_REQUEST);
        });
    }

    private void loadResources() {
        db.collection("groups")
                .document(groupId)
                .collection("resources")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(this, "Error loading resources", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    resources.clear();
                    if (value != null) {
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            Resource resource = doc.toObject(Resource.class);
                            if (resource != null) {
                                resources.add(resource);
                            }
                        }
                    }
                    resourcesAdapter.notifyDataSetChanged();
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            uploadFile(data.getData());
        }
    }

    private void uploadFile(Uri fileUri) {
        progressBar.setVisibility(View.VISIBLE);

        String fileName = System.currentTimeMillis() + "_" + fileUri.getLastPathSegment();
        StorageReference fileRef = FirebaseStorage.getInstance().getReference()
                .child("resources")
                .child(fileName);

        fileRef.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot -> {
                    fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        Resource resource = new Resource();
                        resource.setName(fileName);
                        resource.setUrl(uri.toString());
                        resource.setUploaderId(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        resource.setUploadDate(new Date());

                        db.collection("groups")
                                .document(groupId)
                                .collection("resources")
                                .add(resource)
                                .addOnSuccessListener(ref -> {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(this, "Upload successful", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(this, "Upload failed", Toast.LENGTH_SHORT).show();
                                });
                    });
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Upload failed", Toast.LENGTH_SHORT).show();
                });
    }
}