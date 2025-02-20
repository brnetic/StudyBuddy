package com.example.studybuddy.utils;

import android.content.Context;
import android.net.Uri;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.example.studybuddy.models.Resource;
import java.util.Date;

public class ResourceUploader {

    public interface UploadCallback {
        void onProgress(int progress);
        void onSuccess(Resource resource);
        void onFailure(String error);
    }

    public static void uploadResource(Context context, Uri fileUri, String groupId, UploadCallback callback) {
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
                        resource.setUploadDate(new Date());
                        callback.onSuccess(resource);
                    });
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }
}