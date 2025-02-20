package com.example.studybuddy.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.studybuddy.R;
import com.example.studybuddy.models.Resource;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ResourcesAdapter extends RecyclerView.Adapter<ResourcesAdapter.ViewHolder> {
    private final Context context;
    private List<Resource> resources;
    private final SimpleDateFormat dateFormat;

    public ResourcesAdapter(Context context, List<Resource> resources) {
        this.context = context;
        this.resources = resources;
        this.dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_resource, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Resource resource = resources.get(position);
        holder.resourceNameText.setText(resource.getName());
        holder.uploaderText.setText("Uploaded by: " + resource.getUploaderName());

        if (resource.getUploadDate() != null) {
            holder.uploadTimeText.setText("Uploaded on: " + dateFormat.format(resource.getUploadDate()));
        }

        // Set a default icon
        holder.resourceTypeIcon.setImageResource(R.drawable.ic_file);
    }

    @Override
    public int getItemCount() {
        return resources.size();
    }

    public void updateList(List<Resource> newList) {
        this.resources = newList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final ImageView resourceTypeIcon;
        public final TextView resourceNameText;
        public final TextView uploaderText;
        public final TextView uploadTimeText;
        public final ImageButton downloadButton;

        public ViewHolder(View view) {
            super(view);
            resourceTypeIcon = view.findViewById(R.id.resourceTypeIcon);
            resourceNameText = view.findViewById(R.id.resourceNameText);
            uploaderText = view.findViewById(R.id.uploaderText);
            uploadTimeText = view.findViewById(R.id.uploadTimeText);
            downloadButton = view.findViewById(R.id.downloadButton);
        }
    }
}