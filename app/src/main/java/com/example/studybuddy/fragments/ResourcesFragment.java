package com.example.studybuddy.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.studybuddy.R;
import com.example.studybuddy.adapters.ResourcesAdapter;
import com.example.studybuddy.models.Resource;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.*;

public class ResourcesFragment extends Fragment {
    private static final int PICK_FILE_REQUEST = 1;

    private RecyclerView resourcesRecyclerView;
    private SearchView searchView;
    private TextView noResourcesTextView;
    private FloatingActionButton uploadButton;
    private ProgressBar progressBar;

    private ResourcesAdapter resourcesAdapter;
    private List<Resource> resources;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_resources, container, false);

        initializeViews(view);
        initializeFirebase();
        setupListeners();
        loadResources();

        return view;
    }

    private void initializeViews(View view) {
        resourcesRecyclerView = view.findViewById(R.id.resourcesRecyclerView);
        searchView = view.findViewById(R.id.searchView);
        noResourcesTextView = view.findViewById(R.id.noResourcesTextView);
        uploadButton = view.findViewById(R.id.uploadButton);
        progressBar = view.findViewById(R.id.progressBar);

        resources = new ArrayList<>();
        resourcesAdapter = new ResourcesAdapter(requireContext(), resources);

        resourcesRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        resourcesRecyclerView.setAdapter(resourcesAdapter);  // This should now work
    }

    private void initializeFirebase() {
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    private void setupListeners() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterResources(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterResources(newText);
                return true;
            }
        });

        uploadButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            startActivityForResult(intent, PICK_FILE_REQUEST);
        });
    }

    private void loadResources() {
        progressBar.setVisibility(View.VISIBLE);
        db.collection("resources")
                .orderBy("uploadDate", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    progressBar.setVisibility(View.GONE);

                    if (error != null) {
                        Toast.makeText(getContext(),
                                "Error loading resources", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    resources.clear();
                    if (value != null) {
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            Resource resource = doc.toObject(Resource.class);
                            if (resource != null) {
                                resource.setId(doc.getId());
                                resources.add(resource);
                            }
                        }
                    }

                    updateUI();
                });
    }

    private void filterResources(String query) {
        List<Resource> filteredList = new ArrayList<>();
        for (Resource resource : resources) {
            if (resource.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(resource);
            }
        }
        resourcesAdapter.updateList(filteredList);
        updateUI();
    }

    private void updateUI() {
        if (resources.isEmpty()) {
            noResourcesTextView.setVisibility(View.VISIBLE);
            resourcesRecyclerView.setVisibility(View.GONE);
        } else {
            noResourcesTextView.setVisibility(View.GONE);
            resourcesRecyclerView.setVisibility(View.VISIBLE);
        }
        resourcesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST &&
                resultCode == Activity.RESULT_OK &&
                data != null &&
                data.getData() != null) {

            uploadFile(data.getData());
        }
    }

    private void uploadFile(Uri fileUri) {
        progressBar.setVisibility(View.VISIBLE);
        uploadButton.setEnabled(false);

        String fileName = System.currentTimeMillis() + "_" + fileUri.getLastPathSegment();
        StorageReference fileRef = storage.getReference()
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

                        saveResourceToFirestore(resource);
                    });
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    uploadButton.setEnabled(true);
                    Toast.makeText(getContext(),
                            "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void saveResourceToFirestore(Resource resource) {
        db.collection("resources")
                .add(resource)
                .addOnSuccessListener(documentReference -> {
                    progressBar.setVisibility(View.GONE);
                    uploadButton.setEnabled(true);
                    Toast.makeText(getContext(),
                            "Resource uploaded successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    uploadButton.setEnabled(true);
                    Toast.makeText(getContext(),
                            "Failed to save resource details", Toast.LENGTH_SHORT).show();
                });
    }
}